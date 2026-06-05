package edu.mondragon.webengl.CasaJusta.controllers;


import edu.mondragon.webengl.CasaJusta.dto.ContadorUpdateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketSolicitudController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void enviarActualizacionContador(Integer viviendaId, long inscritos, boolean completo) {
        ContadorUpdateMessage payload = new ContadorUpdateMessage(viviendaId, inscritos, completo);
        messagingTemplate.convertAndSend("/topic/vivienda/" + viviendaId, payload);
    }

    @MessageMapping("/vivienda/suscribir")
    public void suscribirVivienda(@Payload Map<String, Object> payload) {
        Integer viviendaId = (Integer) payload.get("viviendaId");
        System.out.println("Cliente suscrito a vivienda: " + viviendaId);
    }
}