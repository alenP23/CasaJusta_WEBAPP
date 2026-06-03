package edu.mondragon.webengl.CasaJusta.repository;

import edu.mondragon.webengl.CasaJusta.model.Pertenece;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerteneceRepository extends JpaRepository<Pertenece, Integer> {
    
    Optional<Pertenece> findByChat_ChatIdAndUsuario_Dni(Integer chatId, String dni);
    
    List<Pertenece> findByChat_ChatId(Integer chatId);
    
    long countByChat_ChatIdAndVotoSiTrue(Integer chatId);
    
    long countByChat_ChatId(Integer chatId);
}
