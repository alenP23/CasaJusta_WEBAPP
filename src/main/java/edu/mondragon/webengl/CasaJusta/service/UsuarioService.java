package edu.mondragon.webengl.CasaJusta.service;


import edu.mondragon.webengl.CasaJusta.model.Usuario;
import java.util.List;

public interface UsuarioService {
    List<Usuario> findAll();
    Usuario findById(Integer id);
    Usuario findByNombreUsuario(String nombreUsuario);  // ← NUEVO
    void save(Usuario usuario);                          // ← NUEVO
    void deleteById(Integer id);
}