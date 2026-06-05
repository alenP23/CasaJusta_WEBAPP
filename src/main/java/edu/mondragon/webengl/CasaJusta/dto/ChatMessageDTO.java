package edu.mondragon.webengl.CasaJusta.dto;

public class ChatMessageDTO {
    private Integer chatId;
    private Integer mensajeId;
    private String dniUsuario;
    private String nombreUsuario;
    private String contenido;
    private String fechaEnvio;
    private String tipo; // "mensaje", "voto", "sistema"

    public ChatMessageDTO() {}

    public ChatMessageDTO(Integer chatId, Integer mensajeId, String dniUsuario, 
                          String nombreUsuario, String contenido, String fechaEnvio) {
        this.chatId = chatId;
        this.mensajeId = mensajeId;
        this.dniUsuario = dniUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contenido = contenido;
        this.fechaEnvio = fechaEnvio;
        this.tipo = "mensaje";
    }

    public static ChatMessageDTO sistema(Integer chatId, String contenido) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.chatId = chatId;
        dto.contenido = contenido;
        dto.tipo = "sistema";
        return dto;
    }

    // Getters y Setters
    public Integer getChatId() { return chatId; }
    public void setChatId(Integer chatId) { this.chatId = chatId; }

    public Integer getMensajeId() { return mensajeId; }
    public void setMensajeId(Integer mensajeId) { this.mensajeId = mensajeId; }

    public String getDniUsuario() { return dniUsuario; }
    public void setDniUsuario(String dniUsuario) { this.dniUsuario = dniUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(String fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
