
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

                                        // 1. ROTAS PÚBLICAS
                                        req.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll();
                                        req.requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll();
                                        req.requestMatchers(HttpMethod.GET, "/api/produtos/**").permitAll();

                                        // 2. PRODUTOS: VENDEDOR e ADMIN
                                        req.requestMatchers(HttpMethod.POST, "/api/produtos/**")
                                                        .hasAnyRole("VENDEDOR", "ADMIN");

                                        req.requestMatchers(HttpMethod.PUT, "/api/produtos/**")
                                                        .hasAnyRole("VENDEDOR", "ADMIN");

                                        req.requestMatchers(HttpMethod.DELETE, "/api/produtos/**")
                                                        .hasAnyRole("VENDEDOR", "ADMIN");

                                        // 3. COMPRAS: CLIENTE
                                        req.requestMatchers(HttpMethod.POST, "/api/pedidos/**")
                                                        .hasRole("CLIENTE");

                                        req.requestMatchers(HttpMethod.POST, "/api/pagamentos/**")
                                                        .hasRole("CLIENTE");

                                        // 4. CLIENTE PODE SE TORNAR VENDEDOR
                                        req.requestMatchers(HttpMethod.PATCH, "/api/usuarios/tornar-vendedor")
                                                        .hasRole("CLIENTE");

                                        // 5. ADMINISTRAÇÃO: SOMENTE ADMIN
                                        req.requestMatchers(HttpMethod.GET, "/api/usuarios")
                                                        .hasRole("ADMIN");

                                        req.requestMatchers(HttpMethod.DELETE, "/api/usuarios/**")
                                                        .hasRole("ADMIN");

                                        req.requestMatchers(HttpMethod.DELETE, "/api/pagamentos/**")
                                                        .hasRole("ADMIN");

                                        // 6. DEMAIS ROTAS EXIGEM AUTENTICAÇÃO
                                        req.anyRequest().authenticated();

                                })

                                // Desativa autenticação HTTP Basic
                                .httpBasic(httpBasic -> httpBasic.disable())
                                .build();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration) throws Exception {

                return configuration.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
