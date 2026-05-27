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
        return viviendaRepository.save(vivienda);
    }

    @Override
    public void deleteById(Integer id) {
        viviendaRepository.deleteById(id);
    }
}