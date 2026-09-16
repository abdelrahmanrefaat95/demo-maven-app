package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationIT {
    @LocalServerPort int port;
    @Test void infoHasDefaultEnvironment() throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/info")).build();
        var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(response.body().contains("\"environment\":\"local\""));
    }
}
