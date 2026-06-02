package edu.mondragon.webengl.CasaJusta.service;

import edu.mondragon.webengl.CasaJusta.model.Vivienda;
import edu.mondragon.webengl.CasaJusta.repository.ViviendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViviendaServiceImpl implements ViviendaService {

    @Autowired
    private ViviendaRepository viviendaRepository;

    @Autowired
    private NominatimService nominatimService;

    @Override
    public List<Vivienda> findAll() {
        return viviendaRepository.findAll();
    }

    @Override
    public Vivienda findById(Integer id) {
        return viviendaRepository.findById(id).orElse(null);
    }

    @Override
    public Vivienda save(Vivienda vivienda) {
    System.out.println("=== DEBUG SAVE ===");
    System.out.println("Dirección: " + vivienda.getDireccion());
    System.out.println("Latitud antes: " + vivienda.getLatitud());
    System.out.println("Longitud antes: " + vivienda.getLongitud());
    
    if (vivienda.getLatitud() == null || vivienda.getLongitud() == null) {
        System.out.println("Entra en geocodificación");
        
        if (vivienda.getDireccion() != null && !vivienda.getDireccion().trim().isEmpty()) {
            System.out.println("Llama a Nominatim con: " + vivienda.getDireccion());
            
            NominatimService.Coordenadas coords = nominatimService.geocodificar(
                vivienda.getDireccion()
            );
            
            System.out.println("Resultado coords: " + (coords != null ? coords.getLatitud() + ", " + coords.getLongitud() : "NULL"));
            
            if (coords != null) {
                vivienda.setLatitud(coords.getLatitud());
                vivienda.setLongitud(coords.getLongitud());
                System.out.println("Coordenadas asignadas");
            } else {
                System.out.println("Nominatim devolvió NULL");
            }
        } else {
            System.out.println("Dirección vacía o nula");
        }
    } else {
        System.out.println("Ya tiene coordenadas, no geocodifica");
    }
    
    System.out.println("Latitud después: " + vivienda.getLatitud());
    System.out.println("Longitud después: " + vivienda.getLongitud());
    System.out.println("===================");
    
    return viviendaRepository.save(vivienda);
}

    @Override
    public void deleteById(Integer id) {
        viviendaRepository.deleteById(id);
    }
}