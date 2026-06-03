package edu.mondragon.webengl.CasaJusta.dto;

public class ContadorUpdateMessage {
    private Integer viviendaId;
    private long inscritos;
    private boolean completo;
    private String tipo;

    public ContadorUpdateMessage() {}

    public ContadorUpdateMessage(Integer viviendaId, long inscritos, boolean completo) {
        this.viviendaId = viviendaId;
        this.inscritos = inscritos;
        this.completo = completo;
        this.tipo = "contador";
    }

    // Getters y Setters
    public Integer getViviendaId() { return viviendaId; }
    public void setViviendaId(Integer viviendaId) { this.viviendaId = viviendaId; }

    public long getInscritos() { return inscritos; }
    public void setInscritos(long inscritos) { this.inscritos = inscritos; }

    public boolean isCompleto() { return completo; }
    public void setCompleto(boolean completo) { this.completo = completo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
