package edu.mondragon.webengl.CasaJusta.repository;

import edu.mondragon.webengl.CasaJusta.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {
    
    // Listar mensajes ordenados por fecha (para el chat)
    List<Mensaje> findByChat_ChatIdOrderByFechaEnvioAsc(Integer chatId);
    
    // ===== NUEVO: Listar mensajes por chatId (para borrar) =====
    List<Mensaje> findByChat_ChatId(Integer chatId);
    // ==========================================================

    List<Mensaje> findByUsuario_Dni(String dni);
}