package edu.mondragon.webengl.CasaJusta.repository;

import edu.mondragon.webengl.CasaJusta.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
    
    // Contar cuántos están apuntados a una vivienda (estado pendiente o aceptada)
    long countByVivienda_ViviendaIDAndEstadoIn(Integer viviendaId, List<String> estados);
    
    // Verificar si un usuario ya está apuntado
    Optional<Solicitud> findByUsuario_UsuarioIdAndVivienda_ViviendaID(Integer usuarioId, Integer viviendaId);
    
    // Listar solicitudes de una vivienda
    List<Solicitud> findByVivienda_ViviendaIDAndEstado(Integer viviendaId, String estado);
    
    // Listar solicitudes de un usuario
    List<Solicitud> findByUsuario_UsuarioId(Integer usuarioId);
}