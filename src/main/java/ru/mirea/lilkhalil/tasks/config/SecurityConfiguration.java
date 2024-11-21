package ru.mirea.lilkhalil.tasks.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.mirea.lilkhalil.tasks.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "bearerAuth", scheme = "bearer", bearerFormat = "JWT")
public class SecurityConfiguration {

    private static final String ADMIN = "ADMIN";
    private static final String USER = "USER";
    private static final String API_TASKS = "/api/v1/tasks";
    private static final String API_TASK_BY_ID = "/api/v1/tasks/{taskId}";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/v1/auth/**", "/error").permitAll()
                        .requestMatchers("/swagger-ui.html","/swagger-ui/**", "/swagger-resources/*", "/v3/api-docs/**").permitAll()
                        /*
                        Доступ только для пользователей с ролью администратор
                         */
                        .requestMatchers(HttpMethod.POST, API_TASKS).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, API_TASK_BY_ID).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, API_TASK_BY_ID).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PATCH, API_TASK_BY_ID + "/priority").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PATCH, API_TASK_BY_ID + "/assignee").hasRole(ADMIN)
                        /*
                        Доступ только для пользователей с ролью администратор или исполнитель
                         */
                        .requestMatchers(HttpMethod.PATCH, API_TASK_BY_ID + "/status").hasAnyRole(ADMIN, USER)
                        .requestMatchers(HttpMethod.POST, API_TASK_BY_ID + "/comments").hasAnyRole(ADMIN, USER)
                        /*
                        Доступ к методам чтения задач для любого аутентифицированного пользователя
                         */
                        .requestMatchers(HttpMethod.GET, API_TASKS, API_TASK_BY_ID).authenticated()
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .anonymous(AbstractHttpConfigurer::disable)
                .exceptionHandling(customizer -> customizer
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
