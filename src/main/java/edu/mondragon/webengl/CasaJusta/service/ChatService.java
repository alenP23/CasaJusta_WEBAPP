package edu.mondragon.webengl.CasaJusta.service;

import edu.mondragon.webengl.CasaJusta.model.ChatGrupal;
import edu.mondragon.webengl.CasaJusta.model.Mensaje;
import edu.mondragon.webengl.CasaJusta.model.Pertenece;

import java.util.List;
import java.util.Optional;

public interface ChatService {
    
    // Crear chat para una vivienda (si no existe)
    ChatGrupal crearChat(Integer viviendaId);
    
    // Obtener chat por vivienda
    Optional<ChatGrupal> obtenerChatPorVivienda(Integer viviendaId);
    
    // Añadir usuario al chat (cuando se apunta)
    Pertenece unirUsuarioAlChat(Integer chatId, String dni);
    
    // Eliminar usuario del chat (cuando se desapunta)
    void eliminarUsuarioDelChat(Integer chatId, String dni);
    
    // Guardar mensaje
    Mensaje guardarMensaje(Integer chatId, String dni, String contenido);
    
    // Obtener mensajes
    List<Mensaje> obtenerMensajes(Integer chatId);
    
    // Votar
    Pertenece votar(Integer chatId, String dni, boolean votoSi);
    
    // Contar votos sí
    long contarVotosSi(Integer chatId);
    
    // Contar miembros del chat
    long contarMiembros(Integer chatId);
    
    // Usuario ya votó
    boolean usuarioYaVoto(Integer chatId, String dni);
    
    // Todos votaron sí
    boolean todosVotaronSi(Integer chatId);
    
    // Cerrar chat
    void cerrarChat(Integer chatId);
    
    // Asignar vivienda a todos los miembros del chat
    void asignarViviendaAMiembros(Integer chatId);
}