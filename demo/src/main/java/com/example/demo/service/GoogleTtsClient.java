package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
public class GoogleTtsClient {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String ttsUrl;

    public GoogleTtsClient(RestTemplate restTemplate,
                           @Value("${google.tts.api.key}") String apiKey,
                           @Value("${google.tts.url}") String ttsUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.ttsUrl = ttsUrl;
    }

    /**
     * text -> base64 ses içeriği
     */
    @SuppressWarnings("unchecked")
    public String synthesizeSpeech(String text, String languageCode, String voiceName, float speakingRate) {
        // 1. HTTP Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. Request body
        String body = """
        {
          "input": { "text": "%s" },
          "voice": {
            "languageCode": "%s",
            "name": "%s"
          },
          "audioConfig": {
            "audioEncoding": "MP3",
            "speakingRate": %s
          }
        }
        """.formatted(text, languageCode, voiceName, speakingRate);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // 3. GET parametreli URL
        String url = ttsUrl + "?key=" + apiKey;

        ResponseEntity<Map> resp = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String,Object> json = resp.getBody();
        if (json == null || !json.containsKey("audioContent")) {
            throw new RuntimeException("TTS yanıtı hatalı");
        }

        // 4. Base64 içeriği döndür
        return (String) json.get("audioContent");
    }
}
