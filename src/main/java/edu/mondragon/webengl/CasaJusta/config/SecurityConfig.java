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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // PÚBLICAS: solo login, registro y recursos estáticos
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()

                //entrada con usuario de administrador
                .requestMatchers("/admin/**").hasRole("ADMIN") 
                
                // PROTEGIDAS: vista de casas requiere autenticación
                .requestMatchers("/vista_casas_usuario").authenticated()

                .requestMatchers("/registro").authenticated()
                
                // Todo lo demás también requiere login
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/vista_casas_usuario", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // Redirigir raíz "/" a login
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> 
                    response.sendRedirect("/login")
                )
            )
            .userDetailsService(userDetailsService);
        
        return http.build();
    }
}