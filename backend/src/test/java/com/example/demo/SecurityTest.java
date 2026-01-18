//Einfacher Test, um sicherzustellen, dass der Kontext lädt
package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SecurityTest {
    @Test
    public void contextLoads() {
        // Wenn die App startet, ist dieser Test erfolgreich
    }
}