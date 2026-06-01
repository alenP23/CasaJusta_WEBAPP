package edu.mondragon.webengl.CasaJusta.repository;

import edu.mondragon.webengl.CasaJusta.model.FotoVivienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FotoViviendaRepository extends JpaRepository<FotoVivienda, Integer> {
    
    // Todas las fotos de una vivienda
    List<FotoVivienda> findByVivienda_ViviendaID(Integer viviendaId);
    
    // La foto de portada de una vivienda
    Optional<FotoVivienda> findByVivienda_ViviendaIDAndEsPortadaTrue(Integer viviendaId);
}