package edu.mondragon.webengl.CasaJusta.service;

import edu.mondragon.webengl.CasaJusta.controllers.WebSocketSolicitudController;
import edu.mondragon.webengl.CasaJusta.model.Solicitud;
import edu.mondragon.webengl.CasaJusta.model.Vivienda;
import edu.mondragon.webengl.CasaJusta.repository.SolicitudRepository;
import edu.mondragon.webengl.CasaJusta.repository.ViviendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private ViviendaRepository viviendaRepository;  // Necesario para obtener cupo

    @Autowired
    private WebSocketSolicitudController webSocketController;

    @Override
    public Solicitud save(Solicitud solicitud) {
        Solicitud guardada = solicitudRepository.save(solicitud);
        
        // Emitir actualización WebSocket
        notificarCambio(solicitud.getVivienda().getViviendaID());
        
        return guardada;
    }

    @Override
    public void deleteById(Integer id) {
        // Obtener la solicitud antes de borrar para saber la vivienda
        Optional<Solicitud> solicitudOpt = solicitudRepository.findById(id);
        Integer viviendaId = null;
        
        if (solicitudOpt.isPresent()) {
            viviendaId = solicitudOpt.get().getVivienda().getViviendaID();
        }
        
        solicitudRepository.deleteById(id);
        
        // Emitir actualización WebSocket
        if (viviendaId != null) {
            notificarCambio(viviendaId);
        }
    }

    @Override
    public long countByViviendaId(Integer viviendaId) {
        return solicitudRepository.countByVivienda_ViviendaIDAndEstadoIn(
            viviendaId, Arrays.asList("pendiente", "aceptada"));
    }

    @Override
    public boolean usuarioYaApuntado(Integer usuarioId, Integer viviendaId) {
        return solicitudRepository.findByUsuario_UsuarioIdAndVivienda_ViviendaID(usuarioId, viviendaId).isPresent();
    }

    @Override
    public List<Solicitud> findByViviendaId(Integer viviendaId) {
        return solicitudRepository.findByVivienda_ViviendaIDAndEstado(viviendaId, "pendiente");
    }

    @Override
    public Optional<Solicitud> findByUsuarioAndVivienda(Integer usuarioId, Integer viviendaId) {
        return solicitudRepository.findByUsuario_UsuarioIdAndVivienda_ViviendaID(usuarioId, viviendaId);
    }

    /**
     * Notifica a todos los clientes conectados del cambio en el contador
     */
    private void notificarCambio(Integer viviendaId) {
        long inscritos = countByViviendaId(viviendaId);
        
        Optional<Vivienda> viviendaOpt = viviendaRepository.findById(viviendaId);
        boolean completo = viviendaOpt.isPresent() && inscritos >= viviendaOpt.get().getCupoPersonas();
        
        webSocketController.enviarActualizacionContador(viviendaId, inscritos, completo);
    }
}