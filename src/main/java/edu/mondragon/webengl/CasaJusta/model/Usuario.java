package edu.mondragon.webengl.CasaJusta.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(unique = true, nullable = false)
    private String dni;

    @Column(name = "nombre_usuario", unique = true, nullable = false)
    private String nombreUsuario;

    private String nombre;
    private String apellido;
    private String email;
    private String contrasena;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String genero;
    private String rol;

    // One-to-One: Un usuario tiene un perfil de convivencia
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    private PerfilConvivencia perfilConvivencia;

    // Getters y Setters
    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public PerfilConvivencia getPerfilConvivencia() {
        return perfilConvivencia;
    }

    public void setPerfilConvivencia(PerfilConvivencia perfilConvivencia) {
        this.perfilConvivencia = perfilConvivencia;
    }
}
