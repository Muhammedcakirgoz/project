package com.example.demo.controller;

import com.example.demo.service.GoogleTtsClient;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/api/tts")
public class TtsController {

    private final GoogleTtsClient ttsClient;

    public TtsController(GoogleTtsClient ttsClient) {
        this.ttsClient = ttsClient;
    }

    @PostMapping
    public ResponseEntity<byte[]> synthesize(
            @RequestBody TtsRequest req) {

        // 1. Metni sese çevir
        String base64Audio = ttsClient.synthesizeSpeech(
                req.getText(),
                req.getLanguageCode(),
                req.getVoiceName(),
                req.getSpeakingRate()
        );

        // 2. Base64’ü decode ederek byte[] elde et
        byte[] audioBytes = Base64.getDecoder().decode(base64Audio);

        // 3. MP3 dosyası olarak döndür
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("audio/mpeg"));
        headers.setContentLength(audioBytes.length);

        return new ResponseEntity<>(audioBytes, headers, HttpStatus.OK);
    }

    // İç DTO
    public static class TtsRequest {
        private String text;
        private String languageCode = "en-US";    // default
        private String voiceName   = "en-US-Wavenet-D";
        private float speakingRate = 1.0f;

        public String getLanguageCode() {
            return languageCode;
        }

        public void setLanguageCode(String languageCode) {
            this.languageCode = languageCode;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getVoiceName() {
            return voiceName;
        }

        public void setVoiceName(String voiceName) {
            this.voiceName = voiceName;
        }

        public float getSpeakingRate() {
            return speakingRate;
        }

        public void setSpeakingRate(float speakingRate) {
            this.speakingRate = speakingRate;
        }

    }
}
