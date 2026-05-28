package edu.mondragon.webengl.CasaJusta.config;

import edu.mondragon.webengl.CasaJusta.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CustomSuccessHandler customSuccessHandler;  // ← NUEVO

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // PÚBLICAS: login, registro y recursos estáticos
                .requestMatchers("/login", "/registro", "/css/**", "/js/**", "/images/**").permitAll()
                
                // ADMIN: solo usuarios con rol ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // USER: usuarios logueados
                .requestMatchers("/vista_casas_usuario").authenticated()
                
                // Todo lo demás requiere login
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customSuccessHandler)  // ← CAMBIO: usa handler personalizado
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> 
                    response.sendRedirect("/login")
                )
            )
            .userDetailsService(userDetailsService);
        
        return http.build();
    }
}