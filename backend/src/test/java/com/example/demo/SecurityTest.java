//Einfacher Test, um sicherzustellen, dass der Kontext lädt
package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Aktiviert die application-test.properties
public class SecurityTest {
    @Test
    public void contextLoads() {
        // Wenn die App startet, ist dieser Test erfolgreich
    }
}