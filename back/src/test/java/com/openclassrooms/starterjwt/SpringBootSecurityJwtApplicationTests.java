package com.openclassrooms.starterjwt;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringBootSecurityJwtApplicationTests {

    @Test
    void contextLoads() {}

    @Test
    void testMainMethod_runsWithoutException() {
        assertDoesNotThrow(() ->
            SpringBootSecurityJwtApplication.main(new String[] {})
        );
    }
}
