package edu.mondragon.webengl.CasaJusta.service;

import edu.mondragon.webengl.CasaJusta.model.Mensaje;
import edu.mondragon.webengl.CasaJusta.model.Pertenece;
import edu.mondragon.webengl.CasaJusta.model.Solicitud;
import edu.mondragon.webengl.CasaJusta.model.Usuario;
import edu.mondragon.webengl.CasaJusta.repository.MensajeRepository;
import edu.mondragon.webengl.CasaJusta.repository.PerteneceRepository;
import edu.mondragon.webengl.CasaJusta.repository.SolicitudRepository;
import edu.mondragon.webengl.CasaJusta.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private PerteneceRepository perteneceRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private ChatService chatService;

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Usuario findByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario).orElse(null);
    }

    @Override
    public void save(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return;
        }
    
        // 1. Borrar mensajes del usuario en chats
        List<Mensaje> mensajes = mensajeRepository.findByUsuario_Dni(usuario.getDni());
        if (!mensajes.isEmpty()) {
            mensajeRepository.deleteAll(mensajes);
        }
    
        // 2. Borrar solicitudes del usuario
        List<Solicitud> solicitudes = solicitudRepository.findByUsuario_UsuarioId(id);
        if (!solicitudes.isEmpty()) {
            solicitudRepository.deleteAll(solicitudes);
        }
    
        // 3. Borrar pertenencias a chats (y resetear chats si quedan vacíos)
        List<Pertenece> pertenencias = perteneceRepository.findByUsuario_Dni(usuario.getDni());
        for (Pertenece pertenece : pertenencias) {
            Integer chatId = pertenece.getChat().getChatId();
            perteneceRepository.delete(pertenece);
            
            long miembrosRestantes = perteneceRepository.countByChat_ChatId(chatId);
            if (miembrosRestantes == 0) {
                chatService.resetearChat(chatId);
            }
        }
    
        // 4. Borrar perfil de convivencia
        if (usuario.getPerfilConvivencia() != null) {
            // CascadeType.ALL en Usuario debería manejar esto, pero por si acaso
        }
    
        // 5. Finalmente borrar el usuario
        usuarioRepository.deleteById(id);
    }
}