package edu.mondragon.webengl.CasaJusta.model;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vivienda")
public class Vivienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "viviendaID")
    private Integer viviendaID;

    @Column(name = "titulo", length = 80, nullable = false)
    private String titulo;

    @Column(name = "direccion", length = 50, nullable = false)
    private String direccion;

    @Column(name = "metros_cuadrados", nullable = false)
    private Integer metrosCuadrados;

    @Column(name = "habitaciones", nullable = false)
    private Short habitaciones;

    @Column(name = "banos", nullable = false)
    private Short banos;

    @Column(name = "precio", nullable = false, precision = 20, scale = 3)
    private BigDecimal precio;

    @Column(name = "tipo_operacion", length = 20, nullable = false)
    private String tipoOperacion;

    @Column(name = "estado", nullable = false)
    private Boolean estado;

    @Column(name = "cupo_personas", nullable = false)
    private Short cupoPersonas;

    @Column(name = "fumador", nullable = false)
    private Boolean fumador;

    @Column(name = "mascotas", nullable = false)
    private Boolean mascotas;

    @Column(name = "pareja", nullable = false)
    private Boolean pareja;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    // Relaciones con cascade para borrado automático
    @OneToMany(mappedBy = "vivienda", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FotoVivienda> fotos = new ArrayList<>();

    @OneToMany(mappedBy = "vivienda", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Solicitud> solicitudes = new ArrayList<>();

    public Vivienda() {
    }

    // Getters y Setters existentes...

    public Integer getViviendaID() {
        return viviendaID;
    }

    public void setViviendaID(Integer viviendaID) {
        this.viviendaID = viviendaID;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getMetrosCuadrados() {
        return metrosCuadrados;
    }

    public void setMetrosCuadrados(Integer metrosCuadrados) {
        this.metrosCuadrados = metrosCuadrados;
    }

    public Short getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(Short habitaciones) {
        this.habitaciones = habitaciones;
    }

    public Short getBanos() {
        return banos;
    }

    public void setBanos(Short banos) {
        this.banos = banos;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Short getCupoPersonas() {
        return cupoPersonas;
    }

    public void setCupoPersonas(Short cupoPersonas) {
        this.cupoPersonas = cupoPersonas;
    }

    public Boolean getFumador() {
        return fumador;
    }

    public void setFumador(Boolean fumador) {
        this.fumador = fumador;
    }

    public Boolean getMascotas() {
        return mascotas;
    }

    public void setMascotas(Boolean mascotas) {
        this.mascotas = mascotas;
    }

    public Boolean getPareja() {
        return pareja;
    }

    public void setPareja(Boolean pareja) {
        this.pareja = pareja;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    // Getters y Setters para las nuevas relaciones
    public List<FotoVivienda> getFotos() {
        return fotos;
    }

    public void setFotos(List<FotoVivienda> fotos) {
        this.fotos = fotos;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(List<Solicitud> solicitudes) {
        this.solicitudes = solicitudes;
    }
}