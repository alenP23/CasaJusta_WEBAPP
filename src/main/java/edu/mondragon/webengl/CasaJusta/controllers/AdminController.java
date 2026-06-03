package edu.mondragon.webengl.CasaJusta.controllers;

import edu.mondragon.webengl.CasaJusta.model.FotoVivienda;
import edu.mondragon.webengl.CasaJusta.model.Usuario;
import edu.mondragon.webengl.CasaJusta.model.Vivienda;
import edu.mondragon.webengl.CasaJusta.repository.FotoViviendaRepository;
import edu.mondragon.webengl.CasaJusta.repository.ImagenStorageService;
import edu.mondragon.webengl.CasaJusta.service.UsuarioService;
import edu.mondragon.webengl.CasaJusta.service.ViviendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            model.addAttribute("esAdmin", esAdmin(authentication));
        } else {
            model.addAttribute("esAdmin", false);
        }
        
        List<Vivienda> viviendas = viviendaService.findAll();
        model.addAttribute("viviendas", viviendas);
        
        // ===== MAPA: viviendaID -> url de foto portada =====
        Map<Integer, String> fotosPortada = new HashMap<>();
        for (Vivienda v : viviendas) {
            Optional<FotoVivienda> foto = fotoViviendaRepository
                .findByVivienda_ViviendaIDAndEsPortadaTrue(v.getViviendaID());
            foto.ifPresent(f -> fotosPortada.put(v.getViviendaID(), f.getUrlImagen()));
        }
        model.addAttribute("fotosPortada", fotosPortada);
        // ===================================================
        
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

        @PostMapping("/anuncios/eliminar")
        public String eliminarAnuncio(@RequestParam Integer id) {
        // Las fotos y solicitudes se borran automáticamente por cascade
        
        // Borrar archivos físicos de las fotos
        List<FotoVivienda> fotos = fotoViviendaRepository.findByVivienda_ViviendaID(id);
        for (FotoVivienda foto : fotos) {
            imagenStorageService.eliminarImagen(foto.getUrlImagen());
        }

        // Borrar la vivienda (cascade borra fotos y solicitudes en BD)
        viviendaService.deleteById(id);

        // Intentar borrar carpeta
        try {
            imagenStorageService.eliminarCarpetaAnuncio(id);
        } catch (Exception e) {
            // Ignorar si la carpeta ya no existe
        }

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
            model.addAttribute("esAdmin", esAdmin(authentication));
            
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
}