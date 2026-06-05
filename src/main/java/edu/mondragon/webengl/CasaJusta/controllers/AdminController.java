package edu.mondragon.webengl.CasaJusta.controllers;

import edu.mondragon.webengl.CasaJusta.model.Usuario;
import edu.mondragon.webengl.CasaJusta.model.Vivienda;
import edu.mondragon.webengl.CasaJusta.service.UsuarioService;
import edu.mondragon.webengl.CasaJusta.service.ViviendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ViviendaService viviendaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ========== CONSTANTES DE REGRESIÓN LINEAL ==========
    // Precio compra: precio = -29276.88 + 2825.89 * metros
    private static final BigDecimal COMPRA_INTERCEPTO = new BigDecimal("-29276.88");
    private static final BigDecimal COMPRA_PENDIENTE = new BigDecimal("2825.89");

    // Precio alquiler: precio = 307.72 + 7.42 * metros
    private static final BigDecimal ALQUILER_INTERCEPTO = new BigDecimal("307.72");
    private static final BigDecimal ALQUILER_PENDIENTE = new BigDecimal("7.42");

    // ========== MÉTODO AUXILIAR: detectar si es admin ==========
    private boolean esAdmin(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    // ========== ENDPOINT REST: Calcular precio automático ==========
    @GetMapping("/calcular-precio")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> calcularPrecio(
            @RequestParam String tipoOperacion,
            @RequestParam Integer metrosCuadrados) {

        Map<String, Object> response = new HashMap<>();

        if (tipoOperacion == null || tipoOperacion.isEmpty() || 
            metrosCuadrados == null || metrosCuadrados <= 0) {
            response.put("error", "Tipo de operación y metros cuadrados son obligatorios");
            return ResponseEntity.badRequest().body(response);
        }

        BigDecimal precio;
        String formula;

        if ("compra".equalsIgnoreCase(tipoOperacion)) {
            // precio = -29276.88 + 2825.89 * metros
            precio = COMPRA_INTERCEPTO.add(COMPRA_PENDIENTE.multiply(new BigDecimal(metrosCuadrados)));
            formula = "precio = -29276.88 + 2825.89 * metros";
        } else if ("alquiler".equalsIgnoreCase(tipoOperacion)) {
            // precio = 307.72 + 7.42 * metros
            precio = ALQUILER_INTERCEPTO.add(ALQUILER_PENDIENTE.multiply(new BigDecimal(metrosCuadrados)));
            formula = "precio = 307.72 + 7.42 * metros";
        } else {
            response.put("error", "Tipo de operación no válido. Use 'compra' o 'alquiler'");
            return ResponseEntity.badRequest().body(response);
        }

        // Redondear a 2 decimales
        precio = precio.setScale(2, RoundingMode.HALF_UP);

        response.put("tipoOperacion", tipoOperacion);
        response.put("metrosCuadrados", metrosCuadrados);
        response.put("precio", precio);
        response.put("formula", formula);

        return ResponseEntity.ok(response);
    }

    // ========== PANEL PRINCIPAL (ANUNCIOS) ==========
    @GetMapping
    public String panelAdmin(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("esAdmin", esAdmin(authentication));  // ← AÑADIDO
        } else {
            model.addAttribute("esAdmin", false);  // ← AÑADIDO
        }

        List<Vivienda> viviendas = viviendaService.findAll();
        model.addAttribute("viviendas", viviendas);

        return "vista_casas_admin";
    }

    // ========== USUARIOS ==========
    @GetMapping("/usuarios")
    public String listarUsuarios(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("esAdmin", esAdmin(authentication));  // ← AÑADIDO
        } else {
            model.addAttribute("esAdmin", false);  // ← AÑADIDO
        }

        List<Usuario> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);

        return "admin_usuarios";
    }

    // ========== ANUNCIOS CRUD ==========
    @PostMapping("/anuncios/crear")
    public String crearAnuncio(@ModelAttribute Vivienda vivienda) {
        vivienda.setEstado(false);
        viviendaService.save(vivienda);
        return "redirect:/admin";
    }

    @PostMapping("/anuncios/eliminar")
    public String eliminarAnuncio(@RequestParam Integer id) {
        viviendaService.deleteById(id);
        return "redirect:/admin";
    }

    @PostMapping("/usuarios/eliminar")
    public String eliminarUsuario(@RequestParam Integer id) {
        usuarioService.deleteById(id);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/anuncio/{id}/datos")
    @ResponseBody
    public Vivienda getDatosAnuncio(@PathVariable Integer id) {
        return viviendaService.findById(id);
    }

    @PostMapping("/anuncios/editar")
    public String editarAnuncio(@RequestParam Integer id,
                                @ModelAttribute Vivienda viviendaActualizada) {

        Vivienda existente = viviendaService.findById(id);

        if (existente == null) {
            return "redirect:/admin";
        }

        // Actualizar todos los campos
        existente.setTitulo(viviendaActualizada.getTitulo());
        existente.setTipoOperacion(viviendaActualizada.getTipoOperacion());
        existente.setPrecio(viviendaActualizada.getPrecio());
        existente.setDireccion(viviendaActualizada.getDireccion());
        existente.setHabitaciones(viviendaActualizada.getHabitaciones());
        existente.setBanos(viviendaActualizada.getBanos());
        existente.setMetrosCuadrados(viviendaActualizada.getMetrosCuadrados());
        existente.setCupoPersonas(viviendaActualizada.getCupoPersonas());
        existente.setFumador(viviendaActualizada.getFumador());
        existente.setMascotas(viviendaActualizada.getMascotas());
        existente.setPareja(viviendaActualizada.getPareja());

        viviendaService.save(existente);
        return "redirect:/admin";
    }

    // ========== CONFIGURACIÓN / PERFIL ==========
    @GetMapping("/configuracion")
    public String verConfiguracion(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("esAdmin", esAdmin(authentication));  // ← AÑADIDO

            Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
            model.addAttribute("usuario", usuario);
        } else {
            model.addAttribute("esAdmin", false);  // ← AÑADIDO
        }

        return "admin/admin_configuracion";
    }

    @PostMapping("/configuracion/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuarioActualizado, 
                                   @RequestParam(required = false) String contrasena,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {

        if (usuarioActualizado.getUsuarioId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID de usuario no válido");
            return "redirect:/admin/configuracion";
        }

        Usuario usuarioExistente = usuarioService.findById(usuarioActualizado.getUsuarioId());

        if (usuarioExistente == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/configuracion";
        }

        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        usuarioExistente.setDni(usuarioActualizado.getDni());
        usuarioExistente.setNombreUsuario(usuarioActualizado.getNombreUsuario());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        usuarioExistente.setGenero(usuarioActualizado.getGenero());
        usuarioExistente.setFechaNacimiento(usuarioActualizado.getFechaNacimiento());

        if (contrasena != null && !contrasena.trim().isEmpty()) {
            String contrasenaEncriptada = passwordEncoder.encode(contrasena);
            usuarioExistente.setContrasena(contrasenaEncriptada);
        }

        usuarioService.save(usuarioExistente);
        redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");

        return "redirect:/admin/configuracion";
    }

    // ========== DETALLE DE PROPIEDAD ==========
    @GetMapping("/anuncio/{id}")
    public String verDetalleAnuncio(@PathVariable Integer id, 
                                     Authentication authentication, 
                                     Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("esAdmin", esAdmin(authentication));  // ← AÑADIDO
        } else {
            model.addAttribute("esAdmin", false);  // ← AÑADIDO
        }

        Vivienda vivienda = viviendaService.findById(id);
        model.addAttribute("vivienda", vivienda);

        return "property-detail";
    }
}