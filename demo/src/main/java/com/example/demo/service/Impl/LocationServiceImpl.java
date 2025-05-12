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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    /** Header'daki Bearer JWT'den şu anki kullanıcıyı alır */
    private User currentUser() {
        String token = jwtService.extractTokenFromHeader(request);
        String email = jwtService.extractUsername(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));
    }

    @Override
    public Location addLocation(LocationRequest req, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        Location loc;
        if (req.getId() != null) {
            // Eğer id varsa, önce veritabanında var mı bak
            loc = locationRepository.findById(req.getId()).orElse(new Location());
        } else {
            loc = new Location();
        }

        loc.setName(req.getName());
        loc.setLatitude(req.getLatitude());
        loc.setLongitude(req.getLongitude());

        // Yeni alanları ekle
        if (req.getAddress() != null) {
            loc.setAddress(req.getAddress());
        } else {
            loc.setAddress("");
        }

        if (req.getNotes() != null) {
            loc.setNotes(req.getNotes());
        }

        // Timestamp ile ilgili kısım
        if (req.getTimestamp() != null && !req.getTimestamp().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                Date timestamp = sdf.parse(req.getTimestamp());
                loc.setTimestamp(timestamp);
            } catch (Exception e) {
                System.out.println("Timestamp parse edilemedi: " + e.getMessage());
            }
        }

        loc.setUser(user);
        return locationRepository.save(loc);
    }

    @Override
    public List<Location> getLocations(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));
        return locationRepository.findAllByUser(user);
    }

    @Override
    public void deleteLocationByName(String locationName, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        Location loc = locationRepository
                .findByUserAndName(user, locationName)
                .orElseThrow(() -> new RuntimeException("Konum bulunamadı: " + locationName));

        locationRepository.delete(loc);
    }
}