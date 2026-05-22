package edu.mondragon.webengl.CasaJusta.model;

import jakarta.persistence.*;

@Entity
@Table(name = "perfil_convivencia")
public class PerfilConvivencia {

    @Id
    @Column(name = "DNI")
    private String dni;

    @OneToOne
    @MapsId
    @JoinColumn(name = "DNI")
    private Usuario usuario;

    @Column(name = "fumador")
    private Boolean fumador;

    @Column(name = "mascotas")
    private Boolean mascotas;

    @Column(name = "pareja")
    private Boolean pareja;

    public PerfilConvivencia() {}

    // Getters y Setters
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Boolean getFumador() { return fumador; }
    public void setFumador(Boolean fumador) { this.fumador = fumador; }
    public Boolean getMascotas() { return mascotas; }
    public void setMascotas(Boolean mascotas) { this.mascotas = mascotas; }
    public Boolean getPareja() { return pareja; }
    public void setPareja(Boolean pareja) { this.pareja = pareja; }
}