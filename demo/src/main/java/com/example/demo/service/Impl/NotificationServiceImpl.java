package com.example.demo.service.Impl;

import com.example.demo.dto.NotificationRequest;
import com.example.demo.dto.NotificationResponse;
import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.NotificationService;
import com.example.demo.service.SseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final HttpServletRequest request;
    private final SseService sseService;

    public NotificationServiceImpl(NotificationRepository notificationRepo,
                                   UserRepository userRepo,
                                   JwtService jwtService,
                                   HttpServletRequest request,
                                   SseService sseService) {
        this.notificationRepo = notificationRepo;
        this.userRepo         = userRepo;
        this.jwtService       = jwtService;
        this.request          = request;
        this.sseService       = sseService;
    }

    @Override
    public NotificationResponse sendNotification(NotificationRequest req) {
        // 1) Token’dan email’i al
        String token = jwtService.extractTokenFromHeader(request);
        String email = jwtService.extractUsername(token);

        // 2) User’ı bul
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Kullanıcı bulunamadı"));

        // 3) Notification nesnesi oluştur ve kaydet
        Notification n = new Notification();
        n.setUser(user);
        n.setType(req.getType());
        n.setTitle(req.getTitle());
        n.setMessage(req.getMessage());
        notificationRepo.save(n);

        // 4) SSE ile anlık gönderim
        NotificationResponse resp = new NotificationResponse(
                n.getId(), n.getType(), n.getTitle(), n.getMessage(), n.getTimestamp()
        );
        sseService.sendEvent(user.getId(), "notification", resp);

        return resp;
    }

    @Override
    public List<NotificationResponse> getUserNotifications() {
        String token = jwtService.extractTokenFromHeader(request);
        String email = jwtService.extractUsername(token);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Kullanıcı bulunamadı"));

        return notificationRepo
                .findByUserIdOrderByTimestampDesc(user.getId())
                .stream()
                .map(n -> new NotificationResponse(
                        n.getId(), n.getType(), n.getTitle(), n.getMessage(), n.getTimestamp()))
                .collect(Collectors.toList());
    }
}
