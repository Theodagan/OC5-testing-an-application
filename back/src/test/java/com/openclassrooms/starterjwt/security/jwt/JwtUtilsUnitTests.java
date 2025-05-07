package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsUnitTests {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtils = new JwtUtils();

        // Inject values for @Value fields using reflection
        Field secretField = JwtUtils.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtUtils, "test-secret-key");

        Field expField = JwtUtils.class.getDeclaredField("jwtExpirationMs");
        expField.setAccessible(true);
        expField.set(jwtUtils, 6000); // 6 second
    }

    @Test
    void testGenerateAndParseJwtToken() throws InterruptedException {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@example.com")
                .firstName("First")
                .lastName("Last")
                .admin(false)
                .password("secret")
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());

        String token = jwtUtils.generateJwtToken(auth);
        assertNotNull(token);

        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals("user@example.com", username);

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void testValidateJwtToken_shouldFailOnMalformed() {
        assertFalse(jwtUtils.validateJwtToken("this.is.not.a.jwt"));
    }

    @Test
    void testValidateJwtToken_shouldFailOnEmpty() {
        assertFalse(jwtUtils.validateJwtToken(""));
    }

    @Test
    void testValidateJwtToken_shouldFailOnExpired() throws Exception {
        // override expiration to 1 second for this test
        Field expField = JwtUtils.class.getDeclaredField("jwtExpirationMs");
        expField.setAccessible(true);
        expField.set(jwtUtils, 1000); // 1 second

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@example.com")
                .firstName("First")
                .lastName("Last")
                .admin(false)
                .password("secret")
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());

        String token = jwtUtils.generateJwtToken(auth);

        Thread.sleep(1100); // give it enough time to expire

        assertFalse(jwtUtils.validateJwtToken(token));
    }

}
