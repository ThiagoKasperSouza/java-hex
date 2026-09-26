package com.dev.news.main.infrastructure.security;

import com.dev.news.main.domain.user.model.User;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

/**
 * Gera e valida tokens JWT usando HMAC-SHA512 (secret de 512 bits) via
 * spring-security-oauth2-jose. O segredo (base64) vem de {@code app.jwt.secret}.
 */
@Component
public class JwtService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final long expirationHours;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-hours}") long expirationHours) {
        this.expirationHours = expirationHours;

        byte[] keyBytes = Base64.getUrlDecoder().decode(secret);
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA512");

        JWKSource<SecurityContext> jwkSource = new ImmutableSecret<>(key);
        this.encoder = new NimbusJwtEncoder(jwkSource);
        this.decoder = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    public long expirationHours() {
        return expirationHours;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("news-api")
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofHours(expirationHours)))
                .subject(user.username())
                .claim("role", user.role().name())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS512).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String extractUsername(String token) {
        return decode(token).getSubject();
    }

    public String extractRole(String token) {
        return decode(token).getClaimAsString("role");
    }

    private Jwt decode(String token) {
        return decoder.decode(token);
    }
}
