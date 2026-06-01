package edu.mondragon.webengl.CasaJusta.controllers;

import edu.mondragon.webengl.CasaJusta.model.Usuario;
import edu.mondragon.webengl.CasaJusta.model.Vivienda;
import edu.mondragon.webengl.CasaJusta.service.UsuarioService;
import edu.mondragon.webengl.CasaJusta.service.ViviendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ViviendaService viviendaService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ========== MÉTODO AUXILIAR: detectar si es admin ==========
    private boolean esAdmin(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
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

        if (vivienda.getFumador() == null) vivienda.setFumador(false);
        if (vivienda.getMascotas() == null) vivienda.setMascotas(false);
        if (vivienda.getPareja() == null) vivienda.setPareja(false);

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