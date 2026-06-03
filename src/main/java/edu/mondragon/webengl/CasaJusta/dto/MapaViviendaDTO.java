package edu.mondragon.webengl.CasaJusta.dto;

import java.math.BigDecimal;

public class MapaViviendaDTO {
    private Integer viviendaId;
    private String titulo;
    private String direccion;
    private BigDecimal precio;
    private Double latitud;
    private Double longitud;
    private String tipoOperacion;
    private Boolean mascotas;
    private Boolean fumador;
    private Boolean pareja;
    private String urlImagenPortada;
    private Integer habitaciones;
    private Integer banos;
    private Integer metrosCuadrados;
    private Integer cupoPersonas;

    public MapaViviendaDTO(Integer viviendaId, String titulo, String direccion, 
                          BigDecimal precio, Double latitud, Double longitud,
                          String tipoOperacion, Boolean mascotas, Boolean fumador, 
                          Boolean pareja, String urlImagenPortada,
                          Integer habitaciones, Integer banos, 
                          Integer metrosCuadrados, Integer cupoPersonas) {
        this.viviendaId = viviendaId;
        this.titulo = titulo;
        this.direccion = direccion;
        this.precio = precio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tipoOperacion = tipoOperacion;
        this.mascotas = mascotas;
        this.fumador = fumador;
        this.pareja = pareja;
        this.urlImagenPortada = urlImagenPortada;
        this.habitaciones = habitaciones;
        this.banos = banos;
        this.metrosCuadrados = metrosCuadrados;
        this.cupoPersonas = cupoPersonas;
    }

    // Getters (necesarios para Jackson)
    public Integer getViviendaId() { return viviendaId; }
    public String getTitulo() { return titulo; }
    public String getDireccion() { return direccion; }
    public BigDecimal getPrecio() { return precio; }
    public Double getLatitud() { return latitud; }
    public Double getLongitud() { return longitud; }
    public String getTipoOperacion() { return tipoOperacion; }
    public Boolean getMascotas() { return mascotas; }
    public Boolean getFumador() { return fumador; }
    public Boolean getPareja() { return pareja; }
    public String getUrlImagenPortada() { return urlImagenPortada; }
    public Integer getHabitaciones() { return habitaciones; }
    public Integer getBanos() { return banos; }
    public Integer getMetrosCuadrados() { return metrosCuadrados; }
    public Integer getCupoPersonas() { return cupoPersonas; }
}