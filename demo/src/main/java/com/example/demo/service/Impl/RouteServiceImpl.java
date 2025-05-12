package com.example.demo.service.Impl;

import com.example.demo.dto.RouteRequest;
import com.example.demo.dto.RouteResponse;
import com.example.demo.model.Route;
import com.example.demo.repository.RouteRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.GoogleMapsClient;
import com.example.demo.service.RouteService;
import com.example.demo.service.SseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final GoogleMapsClient mapsClient;
    private final SseService sseService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final HttpServletRequest request;

    public RouteServiceImpl(RouteRepository routeRepository,
                            GoogleMapsClient mapsClient,
                            SseService sseService,
                            JwtService jwtService,
                            UserRepository userRepository,
                            HttpServletRequest request) {
        this.routeRepository = routeRepository;
        this.mapsClient      = mapsClient;
        this.sseService      = sseService;
        this.jwtService      = jwtService;
        this.userRepository  = userRepository;
        this.request         = request;
    }


    @Override
    public RouteResponse generateRoute(RouteRequest req) {
        // 1) Google Maps Directions API’den rota bilgisi al
        @SuppressWarnings("unchecked")
        Map<String,Object> apiResp = mapsClient.getDirections(
                req.getOriginLat(), req.getOriginLng(),
                req.getDestLat(),   req.getDestLng()
        );

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> routes = (List<Map<String,Object>>) apiResp.get("routes");
        if (routes.isEmpty()) {
            throw new RuntimeException("Rota bulunamadı");
        }
        Map<String,Object> firstRoute = routes.get(0);

        // 2) Rota özeti (summary) ve süre (duration) bilgilerini çıkar
        String summary = (String) firstRoute.get("summary");
        @SuppressWarnings("unchecked")
        Map<String,Object> leg = ((List<Map<String,Object>>) firstRoute.get("legs")).get(0);
        @SuppressWarnings("unchecked")
        Map<String,Object> duration = (Map<String,Object>) leg.get("duration");
        int estimatedTime = ((Number) duration.get("value")).intValue() / 60; // saniyeyi dakikaya çevir

        // 3) Veritabanına kaydet
        Route route = new Route(
                null,
                req.getOriginLat(),
                req.getOriginLng(),
                req.getDestLat(),
                req.getDestLng(),
                summary,
                estimatedTime
        );
        routeRepository.save(route);

        // 4) JWT’den kullanıcıyı çöz, userId elde et
        String token = jwtService.extractTokenFromHeader(request);
        String email = jwtService.extractUsername(token);
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"))
                .getId();

        // 5) SSE ile anlık güncelleme gönder
        RouteResponse resp = new RouteResponse(summary, estimatedTime);
        sseService.sendEvent(userId, "route", resp);

        return resp;
    }

}