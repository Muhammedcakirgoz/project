package com.example.demo.repository;

import com.example.demo.model.Location;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByUserAndName(User user, String name);
    void deleteByUserAndName(User user, String name);
    List<Location> findAllByUser(User user);
}