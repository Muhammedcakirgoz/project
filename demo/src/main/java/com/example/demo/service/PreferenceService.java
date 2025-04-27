package com.example.demo.service;

import com.example.demo.dto.PreferenceRequest;
import com.example.demo.model.Preference;

public interface PreferenceService {
    Preference savePreference(PreferenceRequest request);
    Preference getLastPreference(); // örnek olarak en son kaydı döndürür
}