package edu.mondragon.webengl.CasaJusta.service;

import edu.mondragon.webengl.CasaJusta.controllers.WebSocketSolicitudController;
import edu.mondragon.webengl.CasaJusta.model.ChatGrupal;
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

    @Autowired
    private ChatService chatService;

    @Override
    public Solicitud save(Solicitud solicitud) {
    Solicitud guardada = solicitudRepository.save(solicitud);

        // Unir usuario al chat de la vivienda
        try {
            ChatGrupal chat = chatService.crearChat(solicitud.getVivienda().getViviendaID());
            chatService.unirUsuarioAlChat(chat.getChatId(), solicitud.getUsuario().getDni());
        } catch (Exception e) {
            System.err.println("Error al unir usuario al chat: " + e.getMessage());
        }

        // Emitir actualización WebSocket
        notificarCambio(solicitud.getVivienda().getViviendaID());

        return guardada;
    }

    @Override
    public void deleteById(Integer id) {
        Optional<Solicitud> solicitudOpt = solicitudRepository.findById(id);

        if (solicitudOpt.isPresent()) {
            Solicitud solicitud = solicitudOpt.get();
            Integer viviendaId = solicitud.getVivienda().getViviendaID();
            String dniUsuario = solicitud.getUsuario().getDni();

            solicitudRepository.deleteById(id);

            // Eliminar usuario del chat
            if (viviendaId != null && dniUsuario != null) {
                try {
                    chatService.obtenerChatPorVivienda(viviendaId).ifPresent(chat -> {
                        chatService.eliminarUsuarioDelChat(chat.getChatId(), dniUsuario);
                    });
                } catch (Exception e) {
                    System.err.println("Error al eliminar usuario del chat: " + e.getMessage());
                }
            }

            notificarCambio(viviendaId);
        } else {
            solicitudRepository.deleteById(id);
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