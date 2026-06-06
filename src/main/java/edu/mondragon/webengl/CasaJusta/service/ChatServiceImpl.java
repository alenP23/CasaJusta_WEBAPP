package edu.mondragon.webengl.CasaJusta.service;

import edu.mondragon.webengl.CasaJusta.model.ChatGrupal;
import edu.mondragon.webengl.CasaJusta.model.Mensaje;
import edu.mondragon.webengl.CasaJusta.model.Pertenece;
import edu.mondragon.webengl.CasaJusta.model.Solicitud;
import edu.mondragon.webengl.CasaJusta.model.Usuario;
import edu.mondragon.webengl.CasaJusta.model.Vivienda;
import edu.mondragon.webengl.CasaJusta.repository.ChatGrupalRepository;
import edu.mondragon.webengl.CasaJusta.repository.MensajeRepository;
import edu.mondragon.webengl.CasaJusta.repository.PerteneceRepository;
import edu.mondragon.webengl.CasaJusta.repository.SolicitudRepository;
import edu.mondragon.webengl.CasaJusta.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatGrupalRepository chatGrupalRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private PerteneceRepository perteneceRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private ViviendaService viviendaService;

    @Override
    public ChatGrupal crearChat(Integer viviendaId) {
        Optional<ChatGrupal> existente = chatGrupalRepository.findByVivienda_ViviendaID(viviendaId);
        if (existente.isPresent()) {
            return existente.get();
        }

        Vivienda vivienda = viviendaService.findById(viviendaId);
        if (vivienda == null) {
            throw new IllegalArgumentException("Vivienda no encontrada");
        }

        ChatGrupal chat = new ChatGrupal();
        chat.setVivienda(vivienda);
        return chatGrupalRepository.save(chat);
    }

    @Override
    public Optional<ChatGrupal> obtenerChatPorVivienda(Integer viviendaId) {
        return chatGrupalRepository.findByVivienda_ViviendaID(viviendaId);
    }

    @Override
    public Pertenece unirUsuarioAlChat(Integer chatId, String dni) {
        Optional<Pertenece> existente = perteneceRepository.findByChat_ChatIdAndUsuario_Dni(chatId, dni);
        if (existente.isPresent()) {
            return existente.get();
        }

        ChatGrupal chat = chatGrupalRepository.findById(chatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat no encontrado"));

        Usuario usuario = usuarioRepository.findByDni(dni)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Pertenece pertenece = new Pertenece();
        pertenece.setChat(chat);
        pertenece.setUsuario(usuario);

        return perteneceRepository.save(pertenece);
    }

    @Override
    public void eliminarUsuarioDelChat(Integer chatId, String dni) {
        perteneceRepository.findByChat_ChatIdAndUsuario_Dni(chatId, dni)
            .ifPresent(perteneceRepository::delete);
    }

    @Override
    public Mensaje guardarMensaje(Integer chatId, String dni, String contenido) {
        ChatGrupal chat = chatGrupalRepository.findById(chatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat no encontrado"));

        Usuario usuario = usuarioRepository.findByDni(dni)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Mensaje mensaje = new Mensaje();
        mensaje.setChat(chat);
        mensaje.setUsuario(usuario);
        mensaje.setContenido(contenido);

        return mensajeRepository.save(mensaje);
    }

    @Override
    public List<Mensaje> obtenerMensajes(Integer chatId) {
        return mensajeRepository.findByChat_ChatIdOrderByFechaEnvioAsc(chatId);
    }

    @Override
    public Pertenece votar(Integer chatId, String dni, boolean votoSi) {
        Pertenece pertenece = perteneceRepository.findByChat_ChatIdAndUsuario_Dni(chatId, dni)
            .orElseThrow(() -> new IllegalArgumentException("No perteneces a este chat"));

        pertenece.setVotoSi(votoSi);
        return perteneceRepository.save(pertenece);
    }

    @Override
    public long contarVotosSi(Integer chatId) {
        return perteneceRepository.countByChat_ChatIdAndVotoSiTrue(chatId);
    }

    @Override
    public long contarMiembros(Integer chatId) {
        return perteneceRepository.countByChat_ChatId(chatId);
    }

    @Override
    public boolean usuarioYaVoto(Integer chatId, String dni) {
        return perteneceRepository.findByChat_ChatIdAndUsuario_Dni(chatId, dni)
            .map(p -> p.getVotoSi() != null)
            .orElse(false);
    }

    @Override
    public boolean todosVotaronSi(Integer chatId) {
        long miembros = contarMiembros(chatId);
        long votosSi = contarVotosSi(chatId);
        return miembros > 0 && votosSi >= miembros;
    }

    @Override
    public void cerrarChat(Integer chatId) {
        ChatGrupal chat = chatGrupalRepository.findById(chatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat no encontrado"));
        chat.setEstado(true);
        chatGrupalRepository.save(chat);
    }

    // ⭐ NUEVO: Asignar vivienda a todos los miembros del chat
    @Override
    public void asignarViviendaAMiembros(Integer chatId) {
        ChatGrupal chat = chatGrupalRepository.findById(chatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat no encontrado"));
        
        Vivienda vivienda = chat.getVivienda();
        
        // 1. Marcar vivienda como asignada (estado = true)
        vivienda.setEstado(true);
        viviendaService.save(vivienda);
        
        // 2. Obtener todos los miembros del chat
        List<Pertenece> miembros = perteneceRepository.findByChat_ChatId(chatId);
        
        // 3. Crear solicitud "completada" para cada miembro que no la tenga ya
        for (Pertenece pertenece : miembros) {
            Usuario usuario = pertenece.getUsuario();
            
            // Verificar si ya tiene una solicitud completada para esta vivienda
            boolean yaTieneSolicitud = solicitudRepository
                .findByUsuario_UsuarioIdAndVivienda_ViviendaIDAndEstado(
                    usuario.getUsuarioId(), vivienda.getViviendaID(), "completada")
                .isPresent();
            
            if (!yaTieneSolicitud) {
                Solicitud solicitud = new Solicitud();
                solicitud.setUsuario(usuario);
                solicitud.setVivienda(vivienda);
                solicitud.setEstado("completada");
                solicitudRepository.save(solicitud);
            }
        }
    }
}