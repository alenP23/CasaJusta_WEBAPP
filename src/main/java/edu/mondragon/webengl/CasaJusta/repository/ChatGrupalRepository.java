package edu.mondragon.webengl.CasaJusta.repository;

import edu.mondragon.webengl.CasaJusta.model.ChatGrupal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatGrupalRepository extends JpaRepository<ChatGrupal, Integer> {
    
    Optional<ChatGrupal> findByVivienda_ViviendaID(Integer viviendaId);
}