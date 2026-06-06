package edu.mondragon.webengl.CasaJusta.controllers;

import edu.mondragon.webengl.CasaJusta.dto.ChatMessageDTO;
import edu.mondragon.webengl.CasaJusta.model.ChatGrupal;
import edu.mondragon.webengl.CasaJusta.model.Mensaje;
import edu.mondragon.webengl.CasaJusta.model.Usuario;
import edu.mondragon.webengl.CasaJusta.service.ChatService;
import edu.mondragon.webengl.CasaJusta.service.SolicitudService;
import edu.mondragon.webengl.CasaJusta.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    // ===== PÁGINA DEL CHAT =====
    @GetMapping("/chat/{viviendaId}")
    public String verChat(@PathVariable Integer viviendaId,
                          Authentication authentication,
                          Model model) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Usuario usuario = usuarioService.findByNombreUsuario(username);
        
        if (usuario == null) {
            return "redirect:/vista_casas_usuario";
        }

        // Verificar que el usuario está apuntado a esta vivienda
        if (!solicitudService.usuarioYaApuntado(usuario.getUsuarioId(), viviendaId)) {
            return "redirect:/vista_casas_usuario?error=no-apuntado";
        }

        // Obtener o crear chat
        ChatGrupal chat = chatService.crearChat(viviendaId);
        
        // Unir usuario al chat (si no está ya)
        chatService.unirUsuarioAlChat(chat.getChatId(), usuario.getDni());

        // Datos básicos
        model.addAttribute("username", username);
        model.addAttribute("usuario", usuario);
        model.addAttribute("vivienda", chat.getVivienda());
        model.addAttribute("chatId", chat.getChatId());

        // Mensajes históricos
        List<ChatMessageDTO> mensajes = chatService.obtenerMensajes(chat.getChatId()).stream()
            .map(m -> new ChatMessageDTO(
                chat.getChatId(),
                m.getMensajeId(),
                m.getUsuario().getDni(),
                m.getUsuario().getNombreUsuario(),
                m.getContenido(),
                m.getFechaEnvio().format(FORMATTER)
            ))
            .collect(Collectors.toList());
        model.addAttribute("mensajes", mensajes);

        // Datos de votación
        long miembros = chatService.contarMiembros(chat.getChatId());
        long votosSi = chatService.contarVotosSi(chat.getChatId());
        boolean yaVoto = chatService.usuarioYaVoto(chat.getChatId(), usuario.getDni());
        boolean todosVotaronSi = chatService.todosVotaronSi(chat.getChatId());
        boolean chatCerrado = chat.getEstado() != null && chat.getEstado();

        model.addAttribute("totalMiembros", miembros);
        model.addAttribute("votosSi", votosSi);
        model.addAttribute("yaVoto", yaVoto);
        model.addAttribute("todosVotaronSi", todosVotaronSi);
        model.addAttribute("chatCerrado", chatCerrado);

        return "chat-vivienda";
    }

    // ===== ENVIAR MENSAJE (AJAX) =====
    @PostMapping("/api/chat/{chatId}/mensaje")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> enviarMensaje(
            @PathVariable Integer chatId,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "Debes iniciar sesión");
            return ResponseEntity.status(401).body(response);
        }

        String username = authentication.getName();
        Usuario usuario = usuarioService.findByNombreUsuario(username);

        if (usuario == null) {
            response.put("success", false);
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        String contenido = payload.get("contenido");
        if (contenido == null || contenido.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "El mensaje no puede estar vacío");
            return ResponseEntity.badRequest().body(response);
        }

        // Guardar en BD
        Mensaje mensaje = chatService.guardarMensaje(chatId, usuario.getDni(), contenido.trim());

        // Crear DTO para WebSocket
        ChatMessageDTO dto = new ChatMessageDTO(
            chatId,
            mensaje.getMensajeId(),
            usuario.getDni(),
            usuario.getNombreUsuario(),
            mensaje.getContenido(),
            mensaje.getFechaEnvio().format(FORMATTER)
        );

        // Broadcast
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, dto);

        response.put("success", true);
        response.put("mensaje", dto);
        return ResponseEntity.ok(response);
    }

    // ===== VOTAR (AJAX) =====
    @PostMapping("/api/chat/{chatId}/votar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> votar(
            @PathVariable Integer chatId,
            @RequestBody Map<String, Boolean> payload,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "Debes iniciar sesión");
            return ResponseEntity.status(401).body(response);
        }

        String username = authentication.getName();
        Usuario usuario = usuarioService.findByNombreUsuario(username);

        if (usuario == null) {
            response.put("success", false);
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        Boolean votoSi = payload.get("votoSi");
        if (votoSi == null) {
            response.put("success", false);
            response.put("message", "Voto inválido");
            return ResponseEntity.badRequest().body(response);
        }

        // Guardar voto
        chatService.votar(chatId, usuario.getDni(), votoSi);

        long miembros = chatService.contarMiembros(chatId);
        long votosSi = chatService.contarVotosSi(chatId);
        boolean todosVotaronSi = chatService.todosVotaronSi(chatId);

        // Notificar voto
        ChatMessageDTO dtoVoto = new ChatMessageDTO();
        dtoVoto.setChatId(chatId);
        dtoVoto.setTipo("voto");
        dtoVoto.setContenido(votoSi ? "SÍ" : "NO");
        dtoVoto.setNombreUsuario(usuario.getNombreUsuario());
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, dtoVoto);

        // Si todos votaron sí, cerrar chat y ASIGNAR VIVIENDA A TODOS
        if (todosVotaronSi) {
            chatService.cerrarChat(chatId);
            
            // ⭐ NUEVO: Asignar vivienda a todos los miembros del chat
            chatService.asignarViviendaAMiembros(chatId);

            ChatMessageDTO dtoCierre = ChatMessageDTO.sistema(chatId, 
                "¡Todos han aceptado! El piso ha sido asignado.");
            messagingTemplate.convertAndSend("/topic/chat/" + chatId, dtoCierre);
        }

        response.put("success", true);
        response.put("votosSi", votosSi);
        response.put("totalMiembros", miembros);
        response.put("todosVotaronSi", todosVotaronSi);
        return ResponseEntity.ok(response);
    }
}