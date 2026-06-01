package edu.mondragon.webengl.CasaJusta.model;

import jakarta.persistence.*;

@Entity
@Table(name = "foto_vivienda")
public class FotoVivienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fotoID")
    private Integer fotoId;

    @ManyToOne
    @JoinColumn(name = "viviendaID", nullable = false)
    private Vivienda vivienda;

    @Column(name = "url_imagen", length = 255, nullable = false)
    private String urlImagen;

    @Column(name = "es_portada", columnDefinition = "TINYINT(1)")
    private Boolean esPortada = false;

    // Constructores
    public FotoVivienda() {}
    
    public FotoVivienda(Vivienda vivienda, String urlImagen, Boolean esPortada) {
        this.vivienda = vivienda;
        this.urlImagen = urlImagen;
        this.esPortada = esPortada;
    }

    // Getters y Setters
    public Integer getFotoId() { return fotoId; }
    public void setFotoId(Integer fotoId) { this.fotoId = fotoId; }

    public Vivienda getVivienda() { return vivienda; }
    public void setVivienda(Vivienda vivienda) { this.vivienda = vivienda; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public Boolean getEsPortada() { return esPortada; }
    public void setEsPortada(Boolean esPortada) { this.esPortada = esPortada; }
}