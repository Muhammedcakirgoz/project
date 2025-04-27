package com.example.demo.service.Impl;

import com.example.demo.dto.PreferenceRequest;
import com.example.demo.model.Preference;
import com.example.demo.repository.PreferenceRepository;
import com.example.demo.service.PreferenceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreferenceServiceImpl implements PreferenceService {

    private final PreferenceRepository preferenceRepository;

    public PreferenceServiceImpl(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    public Preference savePreference(PreferenceRequest request) {
        Preference preference = new Preference();
        preference.setLanguage(request.getLanguage());
        preference.setSpeechSpeed(request.getSpeechSpeed());
        preference.setVibrationEnabled(request.isVibrationEnabled());
        return preferenceRepository.save(preference);
    }

    @Override
    public Preference getLastPreference() {
        List<Preference> all = preferenceRepository.findAll();
        return all.isEmpty() ? null : all.get(all.size() - 1); // sadece örnek amaçlı
    }
}