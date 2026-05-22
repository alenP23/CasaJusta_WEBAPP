package edu.mondragon.webengl.CasaJusta.controllers;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaPisosController {

    @GetMapping("/inicio")
    public String mostrarInicio(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("username", authentication.getName());
            String rol = authentication.getAuthorities().iterator().next().getAuthority();
            model.addAttribute("rol", rol);
        }
        return "inicio";
    }
}   
