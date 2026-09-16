package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationIT {
    @Autowired TestRestTemplate rest;
    @LocalServerPort int port;
    @Test void infoHasDefaultEnvironment() {
        var response = rest.getForEntity("http://localhost:" + port + "/api/info", java.util.Map.class);
        assertEquals("local", response.getBody().get("environment"));
    }
}
