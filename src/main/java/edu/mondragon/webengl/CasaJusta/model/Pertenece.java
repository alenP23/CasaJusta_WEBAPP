package edu.mondragon.webengl.CasaJusta.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pertenece")
public class Pertenece {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "DNI", referencedColumnName = "DNI", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "chatID", nullable = false)
    private ChatGrupal chat;

    @Column(name = "fecha_union")
    private LocalDateTime fechaUnion;

    @Column(name = "voto_si", columnDefinition = "TINYINT(1)")
    private Boolean votoSi; // null = no votado, true = sí, false = no

    public Pertenece() {
        this.fechaUnion = LocalDateTime.now();
        this.votoSi = null;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public ChatGrupal getChat() { return chat; }
    public void setChat(ChatGrupal chat) { this.chat = chat; }

    public LocalDateTime getFechaUnion() { return fechaUnion; }
    public void setFechaUnion(LocalDateTime fechaUnion) { this.fechaUnion = fechaUnion; }

    public Boolean getVotoSi() { return votoSi; }
    public void setVotoSi(Boolean votoSi) { this.votoSi = votoSi; }
}
