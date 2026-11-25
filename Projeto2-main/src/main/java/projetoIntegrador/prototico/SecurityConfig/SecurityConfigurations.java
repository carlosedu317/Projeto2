package projetoIntegrador.prototico.SecurityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(req -> {
                    // 1. ROTAS PÚBLICAS (Navegar, ver catálogo, login e cadastro)
                    req.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/api/produtos/**").permitAll();

                    // 2. PRODUTOS: VENDEDOR e ADMIN adicionam, editam e removem
                    req.requestMatchers(HttpMethod.POST, "/api/produtos/**").hasAnyRole("VENDEDOR", "ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/api/produtos/**").hasAnyRole("VENDEDOR", "ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/api/produtos/**").hasAnyRole("VENDEDOR", "ADMIN");

                    // 3. COMPRAS: CLIENTE (e VENDEDOR/ADMIN que herdam CLIENTE) compram e pagam
                    req.requestMatchers(HttpMethod.POST, "/api/pedidos/**").hasRole("CLIENTE");
                    req.requestMatchers(HttpMethod.POST, "/api/pagamentos/**").hasRole("CLIENTE");

                    // 4. PROMOÇÃO DE CONTA: Cliente vira Vendedor
                    req.requestMatchers(HttpMethod.PATCH, "/api/usuarios/tornar-vendedor").hasRole("CLIENTE");

                    // 5. ADMINISTRAÇÃO: Somente ADMIN
                    req.requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/api/pagamentos/**").hasRole("ADMIN");

                    // 6. Qualquer outra rota exige apenas estar autenticado
                    req.anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults()) // Permite testar fácil no Postman/Insomnia
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
