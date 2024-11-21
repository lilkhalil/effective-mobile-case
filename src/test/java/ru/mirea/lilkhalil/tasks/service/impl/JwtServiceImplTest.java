package ru.mirea.lilkhalil.tasks.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import ru.mirea.lilkhalil.tasks.domain.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    private static final long EXPIRATION_TIME = 3600000;
    private static final String SECRET = "e9a6bfc521767ba5a2c7dc6242df3da26f8586aef53d6f947d6ab14473fb9965";

    @InjectMocks
    private JwtServiceImpl jwtService;

    private UserDetails userDetails;
    private String token;

    @BeforeEach
    void tearUp() {
        jwtService.setExpirationTime(EXPIRATION_TIME);
        jwtService.setSecretKey(SECRET);

        userDetails = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        token = Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(EXPIRATION_TIME, ChronoUnit.MILLIS)))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
                .compact();
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo(userDetails.getUsername());
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String generatedToken = jwtService.generateToken(userDetails);

        assertThat(jwtService.isTokenValid(generatedToken, userDetails)).isTrue();
        assertThat(jwtService.extractUsername(generatedToken)).isEqualTo(userDetails.getUsername());
    }

    @Test
    void isTokenValid_shouldThrowExpiredJwtExceptionForExpiredToken() {
        String expiredToken = Jwts.builder()
                .claims(new HashMap<>())
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().minus(EXPIRATION_TIME, ChronoUnit.MILLIS)))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
                .compact();

        assertThatThrownBy(() -> jwtService.isTokenValid(expiredToken, userDetails))
                .isInstanceOf(ExpiredJwtException.class)
                .hasMessageContaining("JWT expired");
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertThat(isValid).isTrue();
    }
}