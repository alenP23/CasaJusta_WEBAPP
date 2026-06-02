package edu.mondragon.webengl.CasaJusta.service;

import edu.mondragon.webengl.CasaJusta.model.Solicitud;
import edu.mondragon.webengl.CasaJusta.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Override
    public Solicitud save(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    @Override
    public long countByViviendaId(Integer viviendaId) {
        return solicitudRepository.countByVivienda_ViviendaIDAndEstadoIn(
            viviendaId, Arrays.asList("pendiente", "aceptada"));
    }

    @Override
    public boolean usuarioYaApuntado(Integer usuarioId, Integer viviendaId) {
        return solicitudRepository.findByUsuario_UsuarioIdAndVivienda_ViviendaID(usuarioId, viviendaId).isPresent();
    }

    @Override
    public List<Solicitud> findByViviendaId(Integer viviendaId) {
        return solicitudRepository.findByVivienda_ViviendaIDAndEstado(viviendaId, "pendiente");
    }

    @Override
    public void deleteById(Integer id) {
        solicitudRepository.deleteById(id);
    }
}