package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.SseService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/stream")
public class StreamController {

    private final SseService sseService;
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final HttpServletRequest request;

    public StreamController(SseService sseService,
                            JwtService jwtService,
                            UserRepository userRepo,
                            HttpServletRequest request) {
        this.sseService = sseService;
        this.jwtService  = jwtService;
        this.userRepo    = userRepo;
        this.request     = request;
    }


    @GetMapping("/routes")
    public SseEmitter streamRoutes() {
        Long userId = resolveUserId();
        return sseService.createEmitter(userId);
    }

    private Long resolveUserId() {
        String token = jwtService.extractTokenFromHeader(request);
        String email = jwtService.extractUsername(token);
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return user.getId();
    }
}
