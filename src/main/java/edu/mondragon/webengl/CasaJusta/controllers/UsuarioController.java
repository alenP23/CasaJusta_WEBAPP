package edu.mondragon.webengl.CasaJusta.controllers;

import edu.mondragon.webengl.CasaJusta.model.Usuario;
import edu.mondragon.webengl.CasaJusta.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ========== VISTA: CONFIGURACIÓN DE PERFIL USUARIO ==========
    @GetMapping("/usuario/configuracion")
    public String verConfiguracionUsuario(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        model.addAttribute("username", username);

        Usuario usuario = usuarioService.findByNombreUsuario(username);
        model.addAttribute("usuario", usuario);

        return "usuario/usuario_configuracion";
    }

   @PostMapping("/usuario/configuracion/actualizar")
public String actualizarPerfilUsuario(
        @ModelAttribute Usuario usuarioActualizado,
        @RequestParam(required = false) String contrasena,
        Authentication authentication,
        HttpServletRequest request,
        RedirectAttributes redirectAttributes) {

    if (authentication == null || !authentication.isAuthenticated()) {
        return "redirect:/login";
    }

    if (usuarioActualizado.getUsuarioId() == null) {
        redirectAttributes.addFlashAttribute("error", "ID de usuario no válido");
        return "redirect:/usuario/configuracion";
    }

    Usuario usuarioExistente = usuarioService.findById(usuarioActualizado.getUsuarioId());

    if (usuarioExistente == null) {
        redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
        return "redirect:/usuario/configuracion";
    }

    String nombreUsuarioAnterior = usuarioExistente.getNombreUsuario();
    String nuevoNombreUsuario = usuarioActualizado.getNombreUsuario();

    // ===== VALIDACIÓN: Nombre de usuario único =====
    if (nuevoNombreUsuario != null && !nuevoNombreUsuario.equals(nombreUsuarioAnterior)) {
        Usuario usuarioConMismoNombre = usuarioService.findByNombreUsuario(nuevoNombreUsuario);
        if (usuarioConMismoNombre != null && !usuarioConMismoNombre.getUsuarioId().equals(usuarioExistente.getUsuarioId())) {
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario '" + nuevoNombreUsuario + "' ya está en uso");
            return "redirect:/usuario/configuracion";
        }
    }

    // Actualizar datos
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
        // Crear nueva autenticación con el nuevo nombre de usuario
        UsernamePasswordAuthenticationToken nuevaAuth = new UsernamePasswordAuthenticationToken(
            nuevoNombreUsuario,
            usuarioExistente.getContrasena(), // la contraseña encriptada
            authentication.getAuthorities()
        );
        
        // Establecer detalles de la nueva autenticación
        nuevaAuth.setDetails(authentication.getDetails());
        
        // Actualizar el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(nuevaAuth);
        
        // Actualizar la sesión HTTP
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        }
    }

    redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
    return "redirect:/usuario/configuracion";
}
}