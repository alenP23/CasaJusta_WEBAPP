package edu.mondragon.webengl.CasaJusta.model;

import jakarta.persistence.*;

@Entity
@Table(name = "perfil_convivencia")
public class PerfilConvivencia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "perfil_id")
    private Integer perfilId;
    
    // One-to-One: Un perfil pertenece a un usuario
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "usuario_id")
    private Usuario usuario;
    
    private Boolean fumador;
    private Boolean mascotas;
    private Boolean pareja;
    
    // Getters y Setters
    public Integer getPerfilId() { return perfilId; }
    public void setPerfilId(Integer perfilId) { this.perfilId = perfilId; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public Boolean getFumador() { return fumador; }
    public void setFumador(Boolean fumador) { this.fumador = fumador; }
    
    public Boolean getMascotas() { return mascotas; }
    public void setMascotas(Boolean mascotas) { this.mascotas = mascotas; }
    
    public Boolean getPareja() { return pareja; }
    public void setPareja(Boolean pareja) { this.pareja = pareja; }
}