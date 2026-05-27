package edu.mondragon.webengl.CasaJusta.controllers;

import edu.mondragon.webengl.CasaJusta.model.Vivienda;
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

    @GetMapping
    public String panelAdmin(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }
        
        List<Vivienda> viviendas = viviendaService.findAll();
        model.addAttribute("viviendas", viviendas);
        
        return "admin";
    }

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
}
