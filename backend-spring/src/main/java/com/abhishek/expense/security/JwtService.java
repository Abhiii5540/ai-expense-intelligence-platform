package com.abhishek.expense.security;

import com.abhishek.expense.domain.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();
    private static final Pattern SIMPLE_DURATION = Pattern.compile("^(\\d+)([smhd])$", Pattern.CASE_INSENSITIVE);
    private static final byte[] HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8);

    private final ObjectMapper objectMapper;
    private final byte[] secret;
    private final Duration lifetime;

    public JwtService(ObjectMapper objectMapper, JwtProperties properties) {
        this.objectMapper = objectMapper;
        this.secret = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        this.lifetime = parseDuration(properties.getExpiresIn());
    }

    public String createToken(User user) {
        Instant now = Instant.now();
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", user.getId());
        claims.put("email", user.getEmail());
        claims.put("iat", now.getEpochSecond());
        claims.put("exp", now.plus(lifetime).getEpochSecond());

        try {
            String header = URL_ENCODER.encodeToString(HEADER);
            String payload = URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(claims));
            String signingInput = header + "." + payload;
            String signature = URL_ENCODER.encodeToString(sign(signingInput));
            return signingInput + "." + signature;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to create access token", exception);
        }
    }

    public JwtClaims verify(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new InvalidTokenException();
            }

            JsonNode header = objectMapper.readTree(URL_DECODER.decode(parts[0]));
            if (!"HS256".equals(header.path("alg").asText())) {
                throw new InvalidTokenException();
            }

            byte[] expected = sign(parts[0] + "." + parts[1]);
            byte[] actual = URL_DECODER.decode(parts[2]);
            if (!MessageDigest.isEqual(expected, actual)) {
                throw new InvalidTokenException();
            }

            JsonNode payload = objectMapper.readTree(URL_DECODER.decode(parts[1]));
            String subject = payload.path("sub").asText("");
            long expiresAt = payload.path("exp").asLong(0);
            if (subject.isBlank() || expiresAt <= Instant.now().getEpochSecond()) {
                throw new InvalidTokenException();
            }

            return new JwtClaims(subject, payload.path("email").asText(null));
        } catch (InvalidTokenException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new InvalidTokenException(exception);
        }
    }

    private byte[] sign(String signingInput) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(signingInput.getBytes(StandardCharsets.US_ASCII));
    }

    static Duration parseDuration(String value) {
        Matcher matcher = SIMPLE_DURATION.matcher(value == null ? "" : value.trim());
        if (matcher.matches()) {
            long amount = Long.parseLong(matcher.group(1));
            return switch (matcher.group(2).toLowerCase()) {
                case "s" -> Duration.ofSeconds(amount);
                case "m" -> Duration.ofMinutes(amount);
                case "h" -> Duration.ofHours(amount);
                case "d" -> Duration.ofDays(amount);
                default -> throw new IllegalArgumentException("Unsupported JWT duration");
            };
        }
        return Duration.parse(value);
    }
}
