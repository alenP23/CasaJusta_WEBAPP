package edu.mondragon.webengl.CasaJusta.service;

import edu.mondragon.webengl.CasaJusta.model.Vivienda;
import java.util.List;

public interface ViviendaService {
    
    List<Vivienda> findAll();
    
    Vivienda findById(Integer id);
    
    Vivienda save(Vivienda vivienda);
    
    void deleteById(Integer id);
}
