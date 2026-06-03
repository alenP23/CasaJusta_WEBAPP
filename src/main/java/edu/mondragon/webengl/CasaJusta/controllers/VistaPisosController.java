package edu.mondragon.webengl.CasaJusta.controllers;


import edu.mondragon.webengl.CasaJusta.model.FotoVivienda;
import edu.mondragon.webengl.CasaJusta.model.Solicitud;
import edu.mondragon.webengl.CasaJusta.model.Usuario;
import edu.mondragon.webengl.CasaJusta.model.Vivienda;
import edu.mondragon.webengl.CasaJusta.repository.FotoViviendaRepository;
import edu.mondragon.webengl.CasaJusta.service.SolicitudService;
import edu.mondragon.webengl.CasaJusta.service.UsuarioService;
import edu.mondragon.webengl.CasaJusta.service.ViviendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class VistaPisosController {

    @Autowired
    private ViviendaService viviendaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FotoViviendaRepository fotoViviendaRepository;

    @Autowired
    private SolicitudService solicitudService;

    private boolean esAdmin(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

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
        Usuario usuarioActual = null;
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);
            
            String rol = authentication.getAuthorities().iterator().next().getAuthority();
            model.addAttribute("rol", rol);

            usuarioActual = usuarioService.findByNombreUsuario(username);
            if (usuarioActual != null) {
                model.addAttribute("usuario", usuarioActual);
                model.addAttribute("perfilConvivencia", usuarioActual.getPerfilConvivencia());
            }
        }

        // ===== OBTENER VIVIENDAS ACTIVAS =====
        List<Vivienda> viviendas = viviendaService.findAll().stream()
                .filter(v -> v.getEstado() == null || !v.getEstado())
                .collect(Collectors.toList());

        // ===== FILTRO: TIPO DE OPERACIÓN =====
        if (tipoOperacion != null && !tipoOperacion.isEmpty() && !"todos".equals(tipoOperacion)) {
            viviendas = viviendas.stream()
                    .filter(v -> tipoOperacion.equalsIgnoreCase(v.getTipoOperacion()))
                    .collect(Collectors.toList());
        }
        model.addAttribute("tipoOperacionSeleccionado", 
            (tipoOperacion != null && !tipoOperacion.isEmpty()) ? tipoOperacion : "todos");

        // ===== FILTRO: PRECIO MÁXIMO =====
        BigDecimal precioMaximo = (precioMax != null) ? precioMax : new BigDecimal("5000000");
        if (precioMax != null) {
            viviendas = viviendas.stream()
                    .filter(v -> v.getPrecio().compareTo(precioMax) <= 0)
                    .collect(Collectors.toList());
        }
        model.addAttribute("precioMaxSeleccionado", precioMaximo);

        // ===== FILTRO: CONVIVENCIA =====
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

        model.addAttribute("viviendas", viviendas);

        // ===== MAPA: viviendaID -> url de foto portada =====
        Map<Integer, String> fotosPortada = new HashMap<>();
        for (Vivienda v : viviendas) {
            Optional<FotoVivienda> foto = fotoViviendaRepository
                .findByVivienda_ViviendaIDAndEsPortadaTrue(v.getViviendaID());
            foto.ifPresent(f -> fotosPortada.put(v.getViviendaID(), f.getUrlImagen()));
        }
        model.addAttribute("fotosPortada", fotosPortada);

        // ===== DATOS DE SOLICITUDES (para el botón apuntarse) =====
        if (usuarioActual != null) {
            Map<Integer, Boolean> usuarioApuntado = new HashMap<>();
            Map<Integer, Long> contadorInscritos = new HashMap<>();

            for (Vivienda v : viviendas) {
                boolean apuntado = solicitudService.usuarioYaApuntado(
                    usuarioActual.getUsuarioId(), v.getViviendaID());
                long inscritos = solicitudService.countByViviendaId(v.getViviendaID());

                usuarioApuntado.put(v.getViviendaID(), apuntado);
                contadorInscritos.put(v.getViviendaID(), inscritos);
            }

            model.addAttribute("usuarioApuntado", usuarioApuntado);
            model.addAttribute("contadorInscritos", contadorInscritos);
        }

        return "vista_casas_usuario";
    }

    // ===== POST: APUNTARSE A UNA VIVIENDA =====
    @PostMapping("/anuncio/{id}/apuntarse")
    public String apuntarseAVivienda(@PathVariable Integer id,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttrs) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttrs.addFlashAttribute("error", "Debes iniciar sesión para apuntarte");
            return "redirect:/login";
        }

        String username = authentication.getName();
        Usuario usuario = usuarioService.findByNombreUsuario(username);
        Vivienda vivienda = viviendaService.findById(id);

        if (usuario == null || vivienda == null) {
            redirectAttrs.addFlashAttribute("error", "Error al procesar la solicitud");
            return "redirect:/vista_casas_usuario";
        }

        // Verificar si ya está apuntado
        if (solicitudService.usuarioYaApuntado(usuario.getUsuarioId(), id)) {
            redirectAttrs.addFlashAttribute("info", "Ya estás apuntado a esta vivienda");
            return "redirect:/vista_casas_usuario";
        }

        // Verificar si hay cupo
        long inscritos = solicitudService.countByViviendaId(id);
        if (inscritos >= vivienda.getCupoPersonas()) {
            redirectAttrs.addFlashAttribute("error", "El cupo de esta vivienda está completo");
            return "redirect:/vista_casas_usuario";
        }

        // Crear solicitud
        Solicitud solicitud = new Solicitud();
        solicitud.setUsuario(usuario);
        solicitud.setVivienda(vivienda);
        solicitudService.save(solicitud);

        redirectAttrs.addFlashAttribute("success", "¡Te has apuntado correctamente!");
        return "redirect:/vista_casas_usuario";
    }

    // ===== POST: DESAPUNTARSE DE UNA VIVIENDA =====
    @PostMapping("/anuncio/{id}/desapuntarse")
    public String desapuntarseDeVivienda(@PathVariable Integer id,
                                          Authentication authentication,
                                          RedirectAttributes redirectAttrs) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttrs.addFlashAttribute("error", "Debes iniciar sesión");
            return "redirect:/login";
        }

        String username = authentication.getName();
        Usuario usuario = usuarioService.findByNombreUsuario(username);

        if (usuario == null) {
            redirectAttrs.addFlashAttribute("error", "Error al procesar la solicitud");
            return "redirect:/vista_casas_usuario";
        }

        // Buscar la solicitud del usuario para esta vivienda
        Optional<Solicitud> solicitudOpt = solicitudService.findByUsuarioAndVivienda(
            usuario.getUsuarioId(), id);

        if (solicitudOpt.isPresent()) {
            solicitudService.deleteById(solicitudOpt.get().getSolicitudId());
            redirectAttrs.addFlashAttribute("success", "Te has desapuntado correctamente");
        } else {
            redirectAttrs.addFlashAttribute("info", "No estabas apuntado a esta vivienda");
        }

        return "redirect:/vista_casas_usuario";
    }

    @GetMapping("/property-detail/{id}")
    public String verDetallePropiedad(@PathVariable Integer id, 
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
}