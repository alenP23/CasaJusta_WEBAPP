package edu.mondragon.webengl.CasaJusta.controllers;

import edu.mondragon.webengl.CasaJusta.dto.MapaViviendaDTO;
import edu.mondragon.webengl.CasaJusta.model.Vivienda;
import edu.mondragon.webengl.CasaJusta.service.ViviendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MapaController {

    @Autowired
    private ViviendaService viviendaService;

    @GetMapping("/mapa")
    public String verMapa(
            @RequestParam(required = false) String tipoOperacion,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) String filtroMascotas,
            @RequestParam(required = false) String filtroFumador,
            @RequestParam(required = false) String filtroPareja,
            Model model) {

        List<Vivienda> viviendas = viviendaService.findAll().stream()
                .filter(v -> v.getEstado() == null || !v.getEstado())
                .collect(Collectors.toList());

        // ===== APLICAR MISMOS FILTROS =====
        if (tipoOperacion != null && !tipoOperacion.isEmpty() && !"todos".equals(tipoOperacion)) {
            viviendas = viviendas.stream()
                    .filter(v -> tipoOperacion.equalsIgnoreCase(v.getTipoOperacion()))
                    .collect(Collectors.toList());
        }
        model.addAttribute("tipoOperacionSeleccionado", 
            (tipoOperacion != null && !tipoOperacion.isEmpty()) ? tipoOperacion : "todos");

        BigDecimal precioMaximo = (precioMax != null) ? precioMax : new BigDecimal("5000000");
        if (precioMax != null) {
            viviendas = viviendas.stream()
                    .filter(v -> v.getPrecio().compareTo(precioMax) <= 0)
                    .collect(Collectors.toList());
        }
        model.addAttribute("precioMaxSeleccionado", precioMaximo);

        boolean mascotasActivo = "on".equals(filtroMascotas) || "true".equals(filtroMascotas);
        boolean fumadorActivo = "on".equals(filtroFumador) || "true".equals(filtroFumador);
        boolean parejaActivo = "on".equals(filtroPareja) || "true".equals(filtroPareja);

        if (mascotasActivo) {
            viviendas = viviendas.stream().filter(Vivienda::getMascotas).collect(Collectors.toList());
        }
        model.addAttribute("filtroMascotasActivo", mascotasActivo);

        if (fumadorActivo) {
            viviendas = viviendas.stream().filter(Vivienda::getFumador).collect(Collectors.toList());
        }
        model.addAttribute("filtroFumadorActivo", fumadorActivo);

        if (parejaActivo) {
            viviendas = viviendas.stream().filter(Vivienda::getPareja).collect(Collectors.toList());
        }
        model.addAttribute("filtroParejaActivo", parejaActivo);

        // ===== CONVERTIR A DTO PARA EVITAR SERIALIZAR RELACIONES =====
        List<MapaViviendaDTO> viviendasMapa = viviendas.stream()
            .map(v -> {
                String urlPortada = v.getFotos().stream()
                    .filter(f -> f.getEsPortada() != null && f.getEsPortada())
                    .findFirst()
                    .map(f -> f.getUrlImagen())
                    .orElse(null);
                
                return new MapaViviendaDTO(
                    v.getViviendaID(),        // ← CORREGIDO: getViviendaID() con D mayúscula
                    v.getTitulo(),
                    v.getDireccion(),
                    v.getPrecio(),
                    v.getLatitud(),
                    v.getLongitud(),
                    v.getTipoOperacion(),
                    v.getMascotas(),
                    v.getFumador(),
                    v.getPareja(),
                    urlPortada,
                    v.getHabitaciones() != null ? v.getHabitaciones().intValue() : null,
                    v.getBanos() != null ? v.getBanos().intValue() : null,
                    v.getMetrosCuadrados(),
                    v.getCupoPersonas() != null ? v.getCupoPersonas().intValue() : null
                );
            })
            .collect(Collectors.toList());

        model.addAttribute("viviendas", viviendasMapa);
        return "mapa";
    }
}