package edu.mondragon.webengl.CasaJusta.repository;

import edu.mondragon.webengl.CasaJusta.model.Vivienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViviendaRepository extends JpaRepository<Vivienda, Integer> {
    // Spring Data JPA genera automáticamente findAll, save, deleteById...
}
