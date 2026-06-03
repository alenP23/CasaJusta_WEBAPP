package edu.mondragon.webengl.CasaJusta.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_grupal")
public class ChatGrupal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatID")
    private Integer chatId;

    @OneToOne
    @JoinColumn(name = "viviendaID", nullable = false)
    private Vivienda vivienda;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "estado", columnDefinition = "TINYINT(1)")
    private Boolean estado; // false = abierto, true = cerrado/asignado

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Mensaje> mensajes = new ArrayList<>();

    public ChatGrupal() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = false;
    }

    // Getters y Setters
    public Integer getChatId() { return chatId; }
    public void setChatId(Integer chatId) { this.chatId = chatId; }

    public Vivienda getVivienda() { return vivienda; }
    public void setVivienda(Vivienda vivienda) { this.vivienda = vivienda; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public List<Mensaje> getMensajes() { return mensajes; }
    public void setMensajes(List<Mensaje> mensajes) { this.mensajes = mensajes; }
}