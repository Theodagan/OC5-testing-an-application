package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    @Test
    void testFieldsAndBuilder() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .admin(true)
                .password("secret")
                .build();

        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getUsername());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("secret", user.getPassword());
        assertTrue(user.getAdmin());
    }

    @Test
    void testAccountPropertiesAreTrue() {
        UserDetailsImpl user = UserDetailsImpl.builder().build();

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void testAuthoritiesIsEmpty() {
        UserDetailsImpl user = UserDetailsImpl.builder().build();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertTrue(authorities instanceof HashSet);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void testEquals() {
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl user3 = UserDetailsImpl.builder().id(2L).build();

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertNotEquals(user1, null);
        assertNotEquals(user1, new Object());
    }

    @Test
    void testEqualsSameInstance() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).build();
        assertEquals(user, user); // triggers: (this == o) → true
    }
}
