package edu.mondragon.webengl.CasaJusta.config;

import edu.mondragon.webengl.CasaJusta.model.Usuario;
import edu.mondragon.webengl.CasaJusta.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // Verifica si ya existe admin
        if (!usuarioRepository.existsByNombreUsuario("admin")) {
            
            Usuario admin = new Usuario();
            admin.setDni("00000000A");
            admin.setNombreUsuario("admin");
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setEmail("admin@casajusta.com");
            admin.setContrasena(passwordEncoder.encode("admin123"));
            admin.setFechaNacimiento(LocalDate.of(1990, 1, 1));
            admin.setGenero("Otro");
            admin.setRol("ADMIN");
            
            usuarioRepository.save(admin);
            
            System.out.println("========================================");
            System.out.println("✅ ADMIN CREADO AUTOMÁTICAMENTE");
            System.out.println("   Usuario: admin");
            System.out.println("   Contraseña: admin123");
            System.out.println("========================================");
        } else {
            System.out.println("ℹ️ El usuario admin ya existe, no se crea de nuevo");
        }
    }
}