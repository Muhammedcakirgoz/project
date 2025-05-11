package com.example.demo.repository;

import com.example.demo.model.Location;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findAllByUser(User user);
}