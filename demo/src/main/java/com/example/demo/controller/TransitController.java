package com.example.demo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/transit")
public class TransitController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public ResponseEntity<?> getTransitInfo(@RequestParam String origin, @RequestParam String destination) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", "python_scripts/get_transit_info.py", origin,
                    destination);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return ResponseEntity.internalServerError()
                        .body(Map.of(
                                "error", true,
                                "message", "Python script execution failed",
                                "details", output.toString()));
            }

            // Parse the JSON response
            JsonNode jsonResponse = objectMapper.readTree(output.toString());

            // Check if the response indicates an error
            if (jsonResponse.has("error") && jsonResponse.get("error").asBoolean()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error", true,
                                "message", jsonResponse.get("message").asText(),
                                "details", jsonResponse.get("details").asText()));
            }

            return ResponseEntity.ok(jsonResponse);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", true,
                            "message", "Failed to execute Python script",
                            "details", e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", true,
                            "message", "Script execution was interrupted",
                            "details", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", true,
                            "message", "An unexpected error occurred",
                            "details", e.getMessage()));
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Add your frontend URL
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}