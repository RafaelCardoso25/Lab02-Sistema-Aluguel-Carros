package br.pucminas.aluguelcarros.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private static final String[] AGENTES = {"EMPRESA", "BANCO"};

    @Bean
    public SecurityFilterChain filtroSeguranca(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(regras -> regras
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/clientes", "/api/agentes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/clientes").hasAnyRole(AGENTES)
                        .requestMatchers(HttpMethod.POST, "/api/automoveis").hasAnyRole(AGENTES)
                        .requestMatchers(HttpMethod.PUT, "/api/automoveis/*").hasAnyRole(AGENTES)
                        .requestMatchers(HttpMethod.DELETE, "/api/automoveis/*").hasAnyRole(AGENTES)
                        .requestMatchers(HttpMethod.POST, "/api/pedidos").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/pedidos/*/cancelamento").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/pedidos/*/avaliacao").hasAnyRole(AGENTES)
                        .requestMatchers(HttpMethod.POST, "/api/contratos").hasAnyRole(AGENTES)
                        .anyRequest().authenticated())
                .oauth2ResourceServer(servidor -> servidor
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(conversorAutenticacao())))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder(@Value("${app.jwt.secret}") String segredo) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chave(segredo)));
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String segredo) {
        return NimbusJwtDecoder.withSecretKey(chave(segredo)).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origins}") String origens) {
        CorsConfiguration configuracao = new CorsConfiguration();
        configuracao.setAllowedOrigins(Arrays.stream(origens.split(",")).map(String::trim).toList());
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/api/**", configuracao);
        return fonte;
    }

    private JwtAuthenticationConverter conversorAutenticacao() {
        JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
        conversor.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> permissoes = List.of(new SimpleGrantedAuthority("ROLE_" + jwt.getClaimAsString("perfil")));
            return permissoes;
        });
        return conversor;
    }

    private SecretKey chave(String segredo) {
        byte[] bytes = segredo.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET precisa ter pelo menos 32 caracteres.");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }
}
