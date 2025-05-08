package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthTokenFilterUnitTests {

    private AuthTokenFilter filter;
    private JwtUtils jwtUtils;
    private UserDetailsServiceImpl userDetailsService;
    private FilterChain filterChain;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtils = mock(JwtUtils.class);
        userDetailsService = mock(UserDetailsServiceImpl.class);
        filter = new AuthTokenFilter();
        filterChain = mock(FilterChain.class);
        response = mock(HttpServletResponse.class);
    
        // Inject private fields using reflection
        Field jwtField = AuthTokenFilter.class.getDeclaredField("jwtUtils");
        jwtField.setAccessible(true);
        jwtField.set(filter, jwtUtils);
    
        Field udsField = AuthTokenFilter.class.getDeclaredField("userDetailsService");
        udsField.setAccessible(true);
        udsField.set(filter, userDetailsService);
    }

    @Test
    void testFilter_noToken_shouldPassThrough() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testFilter_validToken_shouldAuthenticate() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid.jwt.token");
    
        when(jwtUtils.validateJwtToken("valid.jwt.token")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("valid.jwt.token")).thenReturn("user@example.com");
    
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .admin(false)
                .password("hashed")
                .build();
    
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
    
        // Set empty context manually
        SecurityContextHolder.clearContext();
    
        filter.doFilterInternal(request, response, filterChain);
    
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("user@example.com",
            SecurityContextHolder.getContext().getAuthentication().getName());
    
        verify(filterChain).doFilter(request, response);
    }
    
    @Test
    void testFilter_invalidToken_shouldLogAndContinue() throws Exception {
        SecurityContextHolder.clearContext(); // 🧼 Ensure clean context

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid.token");

        when(jwtUtils.validateJwtToken("invalid.token")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        
        // No auth should be set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
