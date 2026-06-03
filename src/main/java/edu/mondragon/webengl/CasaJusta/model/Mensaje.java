package edu.mondragon.webengl.CasaJusta.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mensajeID")
    private Integer mensajeId;

    @ManyToOne
    @JoinColumn(name = "chatID", nullable = false)
    private ChatGrupal chat;

    @ManyToOne
    @JoinColumn(name = "DNI", referencedColumnName = "DNI", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "contenido", columnDefinition = "TEXT", nullable = false)
    private String contenido;

    public Mensaje() {
        this.fechaEnvio = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getMensajeId() { return mensajeId; }
    public void setMensajeId(Integer mensajeId) { this.mensajeId = mensajeId; }

    public ChatGrupal getChat() { return chat; }
    public void setChat(ChatGrupal chat) { this.chat = chat; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
}
