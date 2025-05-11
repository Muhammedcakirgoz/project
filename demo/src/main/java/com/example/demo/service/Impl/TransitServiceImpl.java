package com.example.demo.service.Impl;

import com.example.demo.dto.TransitRequest;
import com.example.demo.dto.TransitResponse;
import com.example.demo.model.TransitRecord;
import com.example.demo.repository.TransitRepository;
import com.example.demo.service.TransitService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Service
public class TransitServiceImpl implements TransitService {

    private final TransitRepository transitRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    public TransitServiceImpl(TransitRepository transitRepo) {
        this.transitRepo = transitRepo;
    }

    @Override
    public TransitResponse getAndSaveTransit(TransitRequest req) {
        try {
            // 1) Python’u çalıştır
            ProcessBuilder pb = new ProcessBuilder(
                    "python3", "python_scripts/get_transit_info.py",
                    req.getOrigin(), req.getDestination()
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();

            String output = new BufferedReader(
                    new InputStreamReader(p.getInputStream())
            ).lines().collect(Collectors.joining());

            int code = p.waitFor();
            if (code != 0) {
                throw new RuntimeException("Python error: " + output);
            }

            // 2) JSON parse et
            JsonNode root = mapper.readTree(output);
            if (root.has("error") && root.get("error").asBoolean()) {
                throw new RuntimeException(root.get("message").asText());
            }

            // 3) Veriyi kendi DTO’na çevir
            TransitResponse resp = mapper.treeToValue(root, TransitResponse.class);

            // 4) DB’ye kaydetmek için entity’ye dönüştür
            TransitRecord record = new TransitRecord();
            record.setOrigin(req.getOrigin());
            record.setDestination(req.getDestination());
            record.setPayload(output);
            transitRepo.save(record);

            return resp;

        } catch (Exception e) {
            throw new RuntimeException("TransitService failure: " + e.getMessage(), e);
        }
    }
}
