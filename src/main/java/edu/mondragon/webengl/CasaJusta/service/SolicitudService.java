package edu.mondragon.webengl.CasaJusta.service;

import edu.mondragon.webengl.CasaJusta.model.Solicitud;
import java.util.List;

public interface SolicitudService {
    
    Solicitud save(Solicitud solicitud);
    
    long countByViviendaId(Integer viviendaId);
    
    boolean usuarioYaApuntado(Integer usuarioId, Integer viviendaId);
    
    List<Solicitud> findByViviendaId(Integer viviendaId);
    
    void deleteById(Integer id);
}
