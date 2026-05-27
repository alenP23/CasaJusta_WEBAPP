package edu.mondragon.webengl.CasaJusta.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirigirALogin() {
        return "redirect:/login";
    }
}
