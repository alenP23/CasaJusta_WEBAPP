package edu.mondragon.webengl.CasaJusta.controllers;

import edu.mondragon.webengl.CasaJusta.model.PerfilConvivencia;
import edu.mondragon.webengl.CasaJusta.model.Usuario;
import edu.mondragon.webengl.CasaJusta.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class RegisterController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/registro")
    public String mostrarFormulario() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @RequestParam String dni,
            @RequestParam String nombreUsuario,  // ← NUEVO
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String contrasena,
            @RequestParam String fechaNacimiento,
            @RequestParam String genero,
            @RequestParam String rol,
            @RequestParam(required = false) Boolean fumador,
            @RequestParam(required = false) Boolean mascotas,
            @RequestParam(required = false) Boolean pareja,
            Model model) {

        // Verificar DNI
        if (usuarioRepository.existsByDni(dni)) {
            model.addAttribute("error", "Ya existe un usuario con ese DNI");
            return "registro";
        }

        // NUEVO: Verificar nombre de usuario
        if (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            model.addAttribute("error", "Ya existe un usuario con ese nombre de usuario");
            return "registro";
        }

        Usuario usuario = new Usuario();
        usuario.setDni(dni);
        usuario.setNombreUsuario(nombreUsuario);  // ← NUEVO
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setContrasena(passwordEncoder.encode(contrasena));
        usuario.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
        usuario.setGenero(genero);
        usuario.setRol(rol);

        if ("USER".equals(rol)) {
            PerfilConvivencia perfil = new PerfilConvivencia();
            perfil.setDni(dni);
            perfil.setUsuario(usuario);
            perfil.setFumador(fumador != null && fumador);
            perfil.setMascotas(mascotas != null && mascotas);
            perfil.setPareja(pareja != null && pareja);
            usuario.setPerfilConvivencia(perfil);
        }

        usuarioRepository.save(usuario);
        return "redirect:/login?registro=exitoso";
    }
}