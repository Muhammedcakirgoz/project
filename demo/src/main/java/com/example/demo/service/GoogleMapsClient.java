package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
public class GoogleMapsClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String directionsUrl;

    public GoogleMapsClient(RestTemplate restTemplate,
                            @Value("${google.maps.api.key}") String apiKey,
                            @Value("${google.maps.directions.url}") String directionsUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.directionsUrl = directionsUrl;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getDirections(double originLat, double originLng,
                                             double destLat, double destLng) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(directionsUrl)
                .queryParam("origin", originLat + "," + originLng)
                .queryParam("destination", destLat + "," + destLng)
                .queryParam("mode", "transit")
                .queryParam("key", apiKey)
                .build()
                .toUri();

        return restTemplate.getForObject(uri, Map.class);
    }
}
