package edu.mondragon.webengl.CasaJusta.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) 
            throws IOException, ServletException {
        
        // DEBUG: Imprime en consola qué roles tiene el usuario
        System.out.println("=== LOGIN DEBUG ===");
        System.out.println("Usuario: " + authentication.getName());
        System.out.println("Roles: " + authentication.getAuthorities());
        System.out.println("===================");
        
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            System.out.println("→ Redirigiendo a /admin");
            response.sendRedirect("/admin");
        } else {
            System.out.println("→ Redirigiendo a /vista_casas_usuario");
            response.sendRedirect("/vista_casas_usuario");
        }
    }
}
