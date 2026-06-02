package edu.mondragon.webengl.CasaJusta.controllers;

import edu.mondragon.webengl.CasaJusta.model.Vivienda;
import edu.mondragon.webengl.CasaJusta.service.ViviendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MapaController {

    @Autowired
    private ViviendaService viviendaService;

    @GetMapping("/mapa")
    public String verMapa(Model model) {
    List<Vivienda> viviendas = viviendaService.findAll().stream()
            .filter(v -> v.getEstado() == null || !v.getEstado())  // ← Solo disponibles
            .collect(Collectors.toList());
    
    model.addAttribute("viviendas", viviendas);
    return "mapa";
    }
}