package edu.mondragon.webengl.CasaJusta.controllers;

import edu.mondragon.webengl.CasaJusta.model.Usuario;
import edu.mondragon.webengl.CasaJusta.model.Vivienda;
import edu.mondragon.webengl.CasaJusta.service.UsuarioService;
import edu.mondragon.webengl.CasaJusta.service.ViviendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class VistaPisosController {

    @Autowired
    private ViviendaService viviendaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/vista_casas_usuario")
    public String mostrarViviendas(
            @RequestParam(required = false) String tipoOperacion,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Boolean filtroMascotas,
            @RequestParam(required = false) Boolean filtroFumador,
            @RequestParam(required = false) Boolean filtroPareja,
            Authentication authentication,
            Model model) {

        // ===== DATOS DEL USUARIO LOGUEADO =====
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);
            
            String rol = authentication.getAuthorities().iterator().next().getAuthority();
            model.addAttribute("rol", rol);

            // Perfil de convivencia para mostrar compatibilidad
            Usuario usuario = usuarioService.findByNombreUsuario(username);
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
                model.addAttribute("perfilConvivencia", usuario.getPerfilConvivencia());
            }
        }

        // ===== OBTENER VIVIENDAS ACTIVAS =====
        List<Vivienda> viviendas = viviendaService.findAll().stream()
                .filter(v -> v.getEstado() != null && v.getEstado())
                .collect(Collectors.toList());

        // ===== FILTRO: TIPO DE OPERACIÓN (radio: todos/alquiler/compra) =====
        // Mismo patrón que admin: "todos" o null = no filtrar
        if (tipoOperacion != null && !tipoOperacion.isEmpty() && !"todos".equals(tipoOperacion)) {
            viviendas = viviendas.stream()
                    .filter(v -> tipoOperacion.equalsIgnoreCase(v.getTipoOperacion()))
                    .collect(Collectors.toList());
        }
        // Guardar selección para mantener estado en la vista (igual que admin tiene checked)
        model.addAttribute("tipoOperacionSeleccionado", 
            (tipoOperacion != null && !tipoOperacion.isEmpty()) ? tipoOperacion : "todos");

        // ===== FILTRO: PRECIO MÁXIMO (slider) =====
        // Mismo patrón que admin: precioMaximo con valor por defecto 5.000.000
        BigDecimal precioMaximo = (precioMax != null) ? precioMax : new BigDecimal("5000000");
        if (precioMax != null) {
            viviendas = viviendas.stream()
                    .filter(v -> v.getPrecio().compareTo(precioMax) <= 0)
                    .collect(Collectors.toList());
        }
        model.addAttribute("precioMaxSeleccionado", precioMaximo);

        // ===== FILTRO: CONVIVENCIA (checkboxes: mascotas, fumador, pareja) =====
        // Mismo patrón que admin: cada checkbox es independiente, se aplican con "Aplicar"
        if (filtroMascotas != null && filtroMascotas) {
            viviendas = viviendas.stream()
                    .filter(Vivienda::getMascotas)
                    .collect(Collectors.toList());
        }
        model.addAttribute("filtroMascotasActivo", filtroMascotas != null && filtroMascotas);

        if (filtroFumador != null && filtroFumador) {
            viviendas = viviendas.stream()
                    .filter(Vivienda::getFumador)
                    .collect(Collectors.toList());
        }
        model.addAttribute("filtroFumadorActivo", filtroFumador != null && filtroFumador);

        if (filtroPareja != null && filtroPareja) {
            viviendas = viviendas.stream()
                    .filter(Vivienda::getPareja)
                    .collect(Collectors.toList());
        }
        model.addAttribute("filtroParejaActivo", filtroPareja != null && filtroPareja);

        // ===== PASAR VIVIENDAS AL MODELO =====
        model.addAttribute("viviendas", viviendas);

        return "vista_casas_usuario";
    }
}