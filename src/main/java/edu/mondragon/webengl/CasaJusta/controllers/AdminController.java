package edu.mondragon.webengl.CasaJusta.controllers;

import edu.mondragon.webengl.CasaJusta.model.FotoVivienda;
import edu.mondragon.webengl.CasaJusta.model.Mensaje;
import edu.mondragon.webengl.CasaJusta.model.Pertenece;
import edu.mondragon.webengl.CasaJusta.model.Solicitud;
import edu.mondragon.webengl.CasaJusta.model.Usuario;
import edu.mondragon.webengl.CasaJusta.model.Vivienda;
import edu.mondragon.webengl.CasaJusta.repository.ChatGrupalRepository;
import edu.mondragon.webengl.CasaJusta.repository.FotoViviendaRepository;
import edu.mondragon.webengl.CasaJusta.repository.ImagenStorageService;
import edu.mondragon.webengl.CasaJusta.repository.MensajeRepository;
import edu.mondragon.webengl.CasaJusta.repository.PerteneceRepository;
import edu.mondragon.webengl.CasaJusta.repository.SolicitudRepository;
import edu.mondragon.webengl.CasaJusta.service.SolicitudService;
import edu.mondragon.webengl.CasaJusta.service.UsuarioService;
import edu.mondragon.webengl.CasaJusta.service.ViviendaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ViviendaService viviendaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImagenStorageService imagenStorageService;
    
    @Autowired
    private FotoViviendaRepository fotoViviendaRepository;

    // ===== NUEVOS REPOSITORIOS PARA BORRADO EN CASCADA =====
    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private ChatGrupalRepository chatGrupalRepository;

    @Autowired
    private PerteneceRepository perteneceRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private SolicitudService solicitudService;
    // =======================================================
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
            model.addAttribute("esAdmin", esAdmin(authentication));
        } else {
            model.addAttribute("esAdmin", false);
        }
    
        // ===== SOLO VIVIENDAS LIBRES (estado = false o null) =====
        List<Vivienda> viviendas = viviendaService.findAll().stream()
            .filter(v -> v.getEstado() == null || !v.getEstado())
            .collect(Collectors.toList());
    
        model.addAttribute("viviendas", viviendas);
        
        // ===== MAPA: viviendaID -> url de foto portada =====
        Map<Integer, String> fotosPortada = new HashMap<>();
        for (Vivienda v : viviendas) {
            Optional<FotoVivienda> foto = fotoViviendaRepository
                .findByVivienda_ViviendaIDAndEsPortadaTrue(v.getViviendaID());
            foto.ifPresent(f -> fotosPortada.put(v.getViviendaID(), f.getUrlImagen()));
        }
        model.addAttribute("fotosPortada", fotosPortada);

        return "vista_casas_admin";
    }
       
    // ========== USUARIOS ==========
    @GetMapping("/usuarios")
    public String listarUsuarios(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("esAdmin", esAdmin(authentication));
        } else {
            model.addAttribute("esAdmin", false);
        }

        List<Usuario> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);

        return "admin_usuarios";
    }

    // ========== ANUNCIOS CRUD ==========
    
    @PostMapping("/anuncios/crear")
    public String crearAnuncio(@ModelAttribute Vivienda vivienda,
                           @RequestParam(value = "imagen", required = false) MultipartFile imagen,
                           RedirectAttributes redirectAttrs) {
        vivienda.setEstado(false);
        Vivienda guardada = viviendaService.save(vivienda);
                            
        System.out.println(">>> Vivienda guardada con ID: " + guardada.getViviendaID());
                            
        if (imagen != null && !imagen.isEmpty()) {
            try {
                String rutaImagen = imagenStorageService.guardarImagen(imagen, guardada.getViviendaID());
                System.out.println(">>> Imagen guardada en: " + rutaImagen);

                FotoVivienda foto = new FotoVivienda();
                foto.setVivienda(guardada);
                foto.setUrlImagen(rutaImagen);
                foto.setEsPortada(true);
                fotoViviendaRepository.save(foto);
                System.out.println(">>> FotoVivienda guardada en BD");

                redirectAttrs.addFlashAttribute("mensajeExito", "Anuncio creado con imagen");

            } catch (Exception e) {
                System.err.println(">>> ERROR al guardar imagen: " + e.getMessage());
                e.printStackTrace();
                redirectAttrs.addFlashAttribute("mensajeError", "Anuncio creado pero error al guardar imagen");
            }
        } else {
            System.out.println(">>> No se subió imagen");
            redirectAttrs.addFlashAttribute("mensajeExito", "Anuncio creado sin imagen");
        }

        return "redirect:/admin";
    }

    // ===== MÉTODO ELIMINAR CORREGIDO =====
    @PostMapping("/anuncios/eliminar")
    public String eliminarAnuncio(@RequestParam Integer id) {
        
        // 1. BORRAR CHAT Y MENSAJES (si existen)
        chatGrupalRepository.findByVivienda_ViviendaID(id).ifPresent(chat -> {
            // Borrar mensajes del chat
            List<Mensaje> mensajes = mensajeRepository.findByChat_ChatId(chat.getChatId());
            if (!mensajes.isEmpty()) {
                mensajeRepository.deleteAll(mensajes);
            }
            
            // Borrar pertenencias
            List<Pertenece> pertenencias = perteneceRepository.findByChat_ChatId(chat.getChatId());
            if (!pertenencias.isEmpty()) {
                perteneceRepository.deleteAll(pertenencias);
            }
            
            // Borrar chat
            chatGrupalRepository.delete(chat);
        });
        
        // 2. BORRAR SOLICITUDES (inscripciones)
        List<Solicitud> solicitudes = solicitudRepository.findByVivienda_ViviendaID(id);
        if (!solicitudes.isEmpty()) {
            solicitudRepository.deleteAll(solicitudes);
        }
        
        // 3. BORRAR FOTOS FÍSICAS
        List<FotoVivienda> fotos = fotoViviendaRepository.findByVivienda_ViviendaID(id);
        for (FotoVivienda foto : fotos) {
            imagenStorageService.eliminarImagen(foto.getUrlImagen());
        }

        // 4. BORRAR VIVIENDA (cascade borra fotos en BD)
        viviendaService.deleteById(id);

        // 5. BORRAR CARPETA
        try {
            imagenStorageService.eliminarCarpetaAnuncio(id);
        } catch (Exception e) {
            // Ignorar si la carpeta ya no existe
        }

        return "redirect:/admin";
    }
    // =====================================

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
                                @ModelAttribute Vivienda viviendaActualizada,
                                @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        Vivienda existente = viviendaService.findById(id);

        if (existente == null) {
            return "redirect:/admin";
        }

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
        
        if (imagen != null && !imagen.isEmpty()) {
            try {
                fotoViviendaRepository.findByVivienda_ViviendaIDAndEsPortadaTrue(id)
                    .ifPresent(foto -> {
                        imagenStorageService.eliminarImagen(foto.getUrlImagen());
                        fotoViviendaRepository.delete(foto);
                    });
                
                String rutaImagen = imagenStorageService.guardarImagen(imagen, id);
                FotoVivienda nuevaFoto = new FotoVivienda();
                nuevaFoto.setVivienda(existente);
                nuevaFoto.setUrlImagen(rutaImagen);
                nuevaFoto.setEsPortada(true);
                fotoViviendaRepository.save(nuevaFoto);
                
            } catch (Exception e) {
                System.err.println("Error al actualizar imagen: " + e.getMessage());
            }
        }

        return "redirect:/admin";
    }

    @GetMapping("/configuracion")
    public String verConfiguracion(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("esAdmin", esAdmin(authentication));  // ← AÑADIDO

            Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
            model.addAttribute("usuario", usuario);
        } else {
            model.addAttribute("esAdmin", false);
        }

        return "admin/admin_configuracion";
    }

    @PostMapping("/configuracion/actualizar")
public String actualizarPerfil(@ModelAttribute Usuario usuarioActualizado, 
                               @RequestParam(required = false) String contrasena,
                               Authentication authentication,
                               HttpServletRequest request,
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

    // Guardar nombre anterior para comparar
    String nombreUsuarioAnterior = usuarioExistente.getNombreUsuario();
    String nuevoNombreUsuario = usuarioActualizado.getNombreUsuario();

    // ===== VALIDACIÓN: Nombre de usuario único =====
    if (nuevoNombreUsuario != null && !nuevoNombreUsuario.equals(nombreUsuarioAnterior)) {
        Usuario usuarioConMismoNombre = usuarioService.findByNombreUsuario(nuevoNombreUsuario);
        if (usuarioConMismoNombre != null && !usuarioConMismoNombre.getUsuarioId().equals(usuarioExistente.getUsuarioId())) {
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario '" + nuevoNombreUsuario + "' ya está en uso");
            return "redirect:/admin/configuracion";
        }
    }

    // Actualizar datos (SIN TOCAR EL DNI)
    usuarioExistente.setNombre(usuarioActualizado.getNombre());
    usuarioExistente.setApellido(usuarioActualizado.getApellido());
    usuarioExistente.setNombreUsuario(nuevoNombreUsuario);
    usuarioExistente.setEmail(usuarioActualizado.getEmail());
    usuarioExistente.setGenero(usuarioActualizado.getGenero());
    usuarioExistente.setFechaNacimiento(usuarioActualizado.getFechaNacimiento());

    if (contrasena != null && !contrasena.trim().isEmpty()) {
        String contrasenaEncriptada = passwordEncoder.encode(contrasena);
        usuarioExistente.setContrasena(contrasenaEncriptada);
    }

    usuarioService.save(usuarioExistente);

    // ===== ACTUALIZAR SESIÓN DE SPRING SECURITY =====
    if (!nuevoNombreUsuario.equals(nombreUsuarioAnterior)) {
        UsernamePasswordAuthenticationToken nuevaAuth = new UsernamePasswordAuthenticationToken(
            nuevoNombreUsuario,
            usuarioExistente.getContrasena(),
            authentication.getAuthorities()
        );
        nuevaAuth.setDetails(authentication.getDetails());
        SecurityContextHolder.getContext().setAuthentication(nuevaAuth);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        }
    }

    redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
    return "redirect:/admin/configuracion";
    }
    
    @GetMapping("/anuncio/{id}")
    public String verDetalleAnuncio(@PathVariable Integer id, 
                                     Authentication authentication, 
                                     Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("esAdmin", esAdmin(authentication));
        } else {
            model.addAttribute("esAdmin", false);
        }

        Vivienda vivienda = viviendaService.findById(id);
        model.addAttribute("vivienda", vivienda);
        
        Optional<FotoVivienda> fotoPortada = fotoViviendaRepository
            .findByVivienda_ViviendaIDAndEsPortadaTrue(id);
        model.addAttribute("fotoPortada", fotoPortada.orElse(null));
        
        return "property-detail";
    }

    @PostMapping("/anuncio/{id}/imagen")
    public String subirImagen(@PathVariable Integer id,
                              @RequestParam("imagen") MultipartFile imagen,
                              RedirectAttributes redirectAttrs) {
                            
        try {
            Vivienda vivienda = viviendaService.findById(id);
            String rutaImagen = imagenStorageService.guardarImagen(imagen, id);
            
            FotoVivienda foto = new FotoVivienda();
            foto.setVivienda(vivienda);
            foto.setUrlImagen(rutaImagen);
            foto.setEsPortada(true);
            
            fotoViviendaRepository.findByVivienda_ViviendaIDAndEsPortadaTrue(id)
                .ifPresent(f -> {
                    f.setEsPortada(false);
                    fotoViviendaRepository.save(f);
                });
            
            fotoViviendaRepository.save(foto);
            redirectAttrs.addFlashAttribute("mensajeExito", "Imagen subida correctamente");
            
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("mensajeError", "Error al subir la imagen: " + e.getMessage());
        }
        
        return "redirect:/admin/anuncio/" + id;
    }

    // ===== VISTA: VIVIENDAS VENDIDAS/ALQUILADAS (ADMIN) =====
    @GetMapping("/vendidas")
    public String viviendasVendidas(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("esAdmin", esAdmin(authentication));
        } else {
            model.addAttribute("esAdmin", false);
        }
    
        // Buscar TODAS las solicitudes completadas
        List<Solicitud> solicitudesCompletadas = solicitudService.findByEstado("completada");
    
        // Extraer las viviendas (distinct para no repetir)
        List<Vivienda> viviendasVendidas = solicitudesCompletadas.stream()
            .map(Solicitud::getVivienda)
            .distinct()
            .collect(Collectors.toList());
    
        model.addAttribute("viviendas", viviendasVendidas);
    
        // ===== MAPA: viviendaID -> nombre de usuario que la compró/alquiló =====
        Map<Integer, String> compradorPorVivienda = new HashMap<>();
        for (Vivienda v : viviendasVendidas) {
            solicitudService.findFirstByViviendaIdAndEstado(v.getViviendaID(), "completada")
                .ifPresent(solicitud -> {
                    String nombreUsuario = solicitud.getUsuario().getNombreUsuario();
                    compradorPorVivienda.put(v.getViviendaID(), nombreUsuario);
                });
        }
        model.addAttribute("compradorPorVivienda", compradorPorVivienda);
    
        // Fotos portada
        Map<Integer, String> fotosPortada = new HashMap<>();
        for (Vivienda v : viviendasVendidas) {
            Optional<FotoVivienda> foto = fotoViviendaRepository
                .findByVivienda_ViviendaIDAndEsPortadaTrue(v.getViviendaID());
            foto.ifPresent(f -> fotosPortada.put(v.getViviendaID(), f.getUrlImagen()));
        }
        model.addAttribute("fotosPortada", fotosPortada);
    
        return "admin/viviendas_vendidas";
    }
}