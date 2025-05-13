package com.example.demo.service;

import com.example.demo.dto.LocationRequest;
import com.example.demo.model.Location;

import java.util.List;

public interface LocationService {
    /**
     * Yeni bir konum ekler.
     * 
     * @param req       Latitude/longitude ve name bilgilerini içeren DTO
     * @param userEmail Authenticated kullanıcının e-posta adresi
     * @return Kaydedilen Location nesnesi
     */
    Location addLocation(LocationRequest req, String userEmail);

    /**
     * Kullanıcının tüm favori konumlarını getirir.
     * 
     * @param userEmail Authenticated kullanıcının e-posta adresi
     * @return Kullanıcının kaydettiği tüm Location listesi
     */
    List<Location> getLocations(String userEmail);

    /**
     * Kullanıcının favori konumlarından belirtilen ada sahip olanı siler.
     * 
     * @param locationName Silinecek konumun "name" alanı
     * @param userEmail    Authenticated kullanıcının e-posta adresi
     */
    void deleteLocationByName(String locationName, String userEmail);

    /**
     * Konumu günceller.
     * 
     * @param id        Konumun ID'si
     * @param req       Latitude/longitude ve name bilgilerini içeren DTO
     * @param userEmail Authenticated kullanıcının e-posta adresi
     * @return Güncellenen Location nesnesi
     */
    Location updateLocation(Long id, LocationRequest req, String userEmail);
}
