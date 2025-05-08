package com.openclassrooms.starterjwt.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;

class AuthEntryPointJwtUnitTests {

    private AuthEntryPointJwt authEntryPointJwt;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        authEntryPointJwt = new AuthEntryPointJwt();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCommence_shouldReturnUnauthorizedJson() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = mock(AuthenticationException.class);

        when(request.getServletPath()).thenReturn("/api/test");
        when(exception.getMessage()).thenReturn("Unauthorized");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out, true);

        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(out));
        when(response.getWriter()).thenReturn(writer);

        doNothing().when(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        doNothing().when(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        authEntryPointJwt.commence(request, response, exception);

        String json = new String(out.toByteArray());
        Map<?, ?> result = objectMapper.readValue(json, Map.class);

        assertEquals(401, result.get("status"));
        assertEquals("Unauthorized", result.get("error"));
        assertEquals("Unauthorized", result.get("message"));
        assertEquals("/api/test", result.get("path"));
    }

    // Helper class to simulate ServletOutputStream
    static class DelegatingServletOutputStream extends javax.servlet.ServletOutputStream {
        private final ByteArrayOutputStream stream;

        public DelegatingServletOutputStream(ByteArrayOutputStream stream) {
            this.stream = stream;
        }

        @Override
        public void write(int b) {
            stream.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(javax.servlet.WriteListener writeListener) {}
    }
}
