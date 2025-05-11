package com.example.demo.service.Impl;

import com.example.demo.dto.LocationRequest;
import com.example.demo.model.Location;
import com.example.demo.model.User;
import com.example.demo.repository.LocationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.LocationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final HttpServletRequest request;

    public LocationServiceImpl(LocationRepository locationRepository,
                               UserRepository userRepository,
                               JwtService jwtService,
                               HttpServletRequest request) {
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.request = request;
    }

    @Override
    public void saveLocation(LocationRequest locationRequest) {
        String token = jwtService.extractTokenFromHeader(request);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        Location location = new Location();
        location.setLatitude(locationRequest.getLatitude());
        location.setLongitude(locationRequest.getLongitude());
        location.setUser(user); // Konumu kullanıcıya bağla
        locationRepository.save(location);
    }
}
