package edu.mondragon.webengl.CasaJusta.service;

import edu.mondragon.webengl.CasaJusta.model.Solicitud;
import java.util.List;
import java.util.Optional;

public interface SolicitudService {
    
    Solicitud save(Solicitud solicitud);
    
    long countByViviendaId(Integer viviendaId);
    
    boolean usuarioYaApuntado(Integer usuarioId, Integer viviendaId);
    
    List<Solicitud> findByViviendaId(Integer viviendaId);

    Optional<Solicitud> findByUsuarioAndVivienda(Integer usuarioId, Integer viviendaId);
    
    void deleteById(Integer id);

    List<Solicitud> findByUsuarioIdAndEstado(Integer usuarioId, String estado);
    
    // Buscar TODAS las solicitudes completadas (para admin)
    List<Solicitud> findByEstado(String estado);
    
    // Buscar solicitud completada de una vivienda
    Optional<Solicitud> findFirstByViviendaIdAndEstado(Integer viviendaId, String estado);

}
