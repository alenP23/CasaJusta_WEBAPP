package edu.mondragon.webengl.CasaJusta.repository;

import edu.mondragon.webengl.CasaJusta.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
    
    long countByVivienda_ViviendaIDAndEstadoIn(Integer viviendaId, List<String> estados);
    
    Optional<Solicitud> findByUsuario_UsuarioIdAndVivienda_ViviendaID(Integer usuarioId, Integer viviendaId);
    
    List<Solicitud> findByVivienda_ViviendaIDAndEstado(Integer viviendaId, String estado);
    
    List<Solicitud> findByVivienda_ViviendaID(Integer viviendaId);
    
    List<Solicitud> findByUsuario_UsuarioId(Integer usuarioId);

    List<Solicitud> findByUsuario_UsuarioIdAndEstado(Integer usuarioId, String estado);
    
    List<Solicitud> findByEstado(String estado);

    Optional<Solicitud> findFirstByVivienda_ViviendaIDAndEstado(Integer viviendaId, String estado);
    
    // ⭐ AÑADE ESTO:
    Optional<Solicitud> findByUsuario_UsuarioIdAndVivienda_ViviendaIDAndEstado(
        Integer usuarioId, Integer viviendaId, String estado);
}