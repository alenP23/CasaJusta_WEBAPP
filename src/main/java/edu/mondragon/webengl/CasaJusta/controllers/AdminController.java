package edu.mondragon.webengl.CasaJusta.controllers;

import edu.mondragon.webengl.CasaJusta.model.Usuario;
import edu.mondragon.webengl.CasaJusta.model.Vivienda;
import edu.mondragon.webengl.CasaJusta.service.UsuarioService;
import edu.mondragon.webengl.CasaJusta.service.ViviendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ViviendaService viviendaService;
    
    @Autowired
    private UsuarioService usuarioService;  

    // ========== PANEL PRINCIPAL (ANUNCIOS) ==========
    @GetMapping
    public String panelAdmin(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
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
        }
        
        List<Usuario> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        
        return "admin_usuarios";  // ← NUEVA VISTA
    }

    // ========== ANUNCIOS CRUD ==========
    @PostMapping("/anuncios/crear")
    public String crearAnuncio(@ModelAttribute Vivienda vivienda) {
        viviendaService.save(vivienda);
        return "redirect:/admin";
    }

    @PostMapping("/anuncios/eliminar")
    public String eliminarAnuncio(@RequestParam Integer id) {
        viviendaService.deleteById(id);
        return "redirect:/admin";
    }

    @PostMapping("/usuarios/eliminar")
    public String eliminarUsuario(@RequestParam Integer id) {   // ← Integer
    usuarioService.deleteById(id);
    return "redirect:/admin/usuarios";
    }

    @GetMapping("/configuracion")
    public String verConfiguracion(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        
            // Obtener el usuario actualmente logueado
            Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
            model.addAttribute("usuario", usuario);
        }
    
        return "admin/admin_configuracion";
    }

    @PostMapping("/configuracion/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuarioActualizado, 
                                @RequestParam(required = false) String contrasena,
                                Authentication authentication) {
    
        // Obtener usuario original para no perder datos sensibles
        Usuario usuarioExistente = usuarioService.findById(usuarioActualizado.getUsuarioId());
                                    
        // Actualizar campos editables
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        usuarioExistente.setDni(usuarioActualizado.getDni());
        usuarioExistente.setNombreUsuario(usuarioActualizado.getNombreUsuario());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        usuarioExistente.setGenero(usuarioActualizado.getGenero());
        usuarioExistente.setFechaNacimiento(usuarioActualizado.getFechaNacimiento());
    
        // Solo actualizar contraseña si se proporcionó una nueva
        if (contrasena != null && !contrasena.isEmpty()) {
            // Aquí deberías encriptar la contraseña con BCrypt
            usuarioExistente.setContrasena(contrasena);
        }
    
        usuarioService.save(usuarioExistente);
    
        return "redirect:/admin/configuracion";
    }

}