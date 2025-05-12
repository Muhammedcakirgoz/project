package com.example.demo.service;

import com.example.demo.model.TransitRecord;
import com.example.demo.model.TransitResponse;
import com.example.demo.model.TransitResponse.Step;
import com.example.demo.model.TransferInfo;
import com.example.demo.repository.TransitRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransitServiceImpl implements TransitService {
    private static final Logger logger = LoggerFactory.getLogger(TransitServiceImpl.class);
    private final TransitRepository transitRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    public TransitServiceImpl(TransitRepository transitRepo) {
        this.transitRepo = transitRepo;
    }

    @Override
    public TransitResponse getTransitInfo(String origin, String destination) {
        logger.info("Transit bilgisi istendi: origin={}, destination={}", origin, destination);

        try {
            String scriptPath = "../demo/python_scripts/get_transit_info.py";
            File scriptFile = new File(scriptPath);

            if (!scriptFile.exists()) {
                logger.error("Python script bulunamadı: {}", scriptPath);
                throw new RuntimeException("Python scripti bulunamadı: " + scriptPath);
            }

            // Python içindeki requests modülünü kontrol et ve yükle
            try {
                ProcessBuilder checkModuleBuilder = new ProcessBuilder(
                        "python", // Generic python command
                        "-c", "import requests; print('Requests module OK')");

                Process checkProcess = checkModuleBuilder.start();
                String checkOutput = readProcessOutput(checkProcess);

                if (!checkOutput.contains("Requests module OK")) {
                    logger.warn("Requests modülü bulunamadı, yükleniyor...");

                    ProcessBuilder installBuilder = new ProcessBuilder(
                            "python", // Generic python command
                            "-m", "pip", "install", "requests");

                    Process installProcess = installBuilder.start();
                    String installOutput = readProcessOutput(installProcess);
                    logger.info("Pip install çıktısı: {}", installOutput);

                    if (installProcess.waitFor() != 0) {
                        logger.error("Requests modülü yüklenemedi: {}", installOutput);
                    } else {
                        logger.info("Requests modülü başarıyla yüklendi");
                    }
                } else {
                    logger.info("Requests modülü zaten yüklü");
                }
            } catch (Exception e) {
                logger.warn("Modül kontrolü sırasında hata: {}", e.getMessage());
            }

            // Transit bilgilerini getir
            ProcessBuilder pb = new ProcessBuilder(
                    "python", // Generic python command
                    scriptPath,
                    origin,
                    destination);

            // UTF-8 kodlaması ve environment değişkenlerini ayarla
            pb.environment().put("PYTHONIOENCODING", "utf-8");
            pb.redirectErrorStream(true);

            logger.info("Process çalıştırılıyor: {}", pb.command());

            Process process = pb.start();
            boolean completed = process.waitFor(30, TimeUnit.SECONDS);

            if (!completed) {
                process.destroy();
                throw new RuntimeException("Python script zaman aşımına uğradı (30 saniye)");
            }

            int exitCode = process.exitValue();

            // UTF-8 ile çıktıyı oku
            String output = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            // Hata çıktısını da oku ve logla
            String errorOutput = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            if (!errorOutput.isEmpty()) {
                logger.warn("Python script hata çıktısı: {}", errorOutput);
            }

            logger.info("Python çıkış kodu: {}", exitCode);
            logger.info("Python çıktısı: {}", output);

            if (exitCode != 0) {
                throw new RuntimeException("Python script hata verdi (çıkış kodu: " + exitCode + "): " + errorOutput);
            }

            if (output == null || output.trim().isEmpty()) {
                throw new RuntimeException("Python script boş çıktı verdi");
            }

            // JSON parse et
            try {
                // Json çıktısını sonundaki gereksiz boşluklardan temizle
                output = output.trim();

                // Satır başındaki çöp karakterleri temizle eğer varsa
                if (output.indexOf("{") > 0) {
                    output = output.substring(output.indexOf("{"));
                }

                // Json'ın } ile bittiğinden emin ol
                if (!output.endsWith("}") && output.lastIndexOf("}") > 0) {
                    output = output.substring(0, output.lastIndexOf("}") + 1);
                }

                logger.info("İşlenmiş JSON: {}", output);

                // JSON parse et
                JsonNode root = mapper.readTree(output);

                // JSON'da steps alanını kontrol et
                if (!root.has("steps") || root.get("steps").isNull() || root.get("steps").isEmpty()) {
                    logger.warn("JSON çıktısında steps alanı yok, boş veya null!");
                }

                if (root.has("error") && root.get("error").asBoolean()) {
                    String message = root.has("message") ? root.get("message").asText() : "Bilinmeyen hata";
                    throw new RuntimeException("Transit API hatası: " + message);
                }

                // Modele dönüştür
                TransitResponse resp = mapper.treeToValue(root, TransitResponse.class);

                // Null kontrolü
                if (resp.getSteps() == null) {
                    logger.warn("Dönüştürülen modelde steps null!");
                    resp.setSteps(new ArrayList<>());
                }

                // Aktarma bilgilerini analiz et ve ekle
                resp.setTransferInfo(analyzeTransfers(resp));

                // DB'ye kaydet
                TransitRecord record = new TransitRecord();
                record.setOrigin(origin);
                record.setDestination(destination);
                record.setPayload(output);
                transitRepo.save(record);

                return resp;
            } catch (Exception e) {
                logger.error("JSON işleme hatası: {}", e.getMessage(), e);
                logger.error("JSON içeriği: {}", output);
                throw new RuntimeException("JSON işleme hatası: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Transit isteği işlenirken hata: {}", e.getMessage(), e);
            throw new RuntimeException("TransitService failure: " + e.getMessage(), e);
        }
    }

    private String readProcessOutput(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        String line;
        StringBuilder output = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // Hata çıktısını da oku
        BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
        StringBuilder errorOutput = new StringBuilder();

        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line).append("\n");
        }

        if (errorOutput.length() > 0) {
            logger.warn("Process hata çıktısı: {}", errorOutput.toString());
            output.append("\nERROR: ").append(errorOutput);
        }

        return output.toString().trim();
    }

    private TransferInfo analyzeTransfers(TransitResponse response) {
        TransferInfo transferInfo = new TransferInfo();
        List<Step> steps = response.getSteps();

        // Transit adımlarını filtrele
        List<Step> transitSteps = steps.stream()
                .filter(step -> "TRANSIT".equals(step.getTravelMode()))
                .collect(Collectors.toList());

        transferInfo.setHasTransfer(transitSteps.size() > 1);
        transferInfo.setTransferCount(transitSteps.size() - 1);

        // Aktarma noktalarını bul
        for (int i = 0; i < transitSteps.size() - 1; i++) {
            Step currentTransit = transitSteps.get(i);
            Step nextTransit = transitSteps.get(i + 1);

            TransferInfo.TransferPoint transfer = new TransferInfo.TransferPoint();
            transfer.setLocation(currentTransit.getTransitDetails().getArrivalStop().getName());
            transfer.setFromBus(currentTransit.getTransitDetails().getLine().getShortName());
            transfer.setToBus(nextTransit.getTransitDetails().getLine().getShortName());
            transfer.setArrivalTime(currentTransit.getTransitDetails().getArrivalTime());
            transfer.setDepartureTime(nextTransit.getTransitDetails().getDepartureTime());

            // Bekleme süresini hesapla
            try {
                LocalTime arrival = LocalTime.parse(currentTransit.getTransitDetails().getArrivalTime());
                LocalTime departure = LocalTime.parse(nextTransit.getTransitDetails().getDepartureTime());
                Duration waitingTime = Duration.between(arrival, departure);
                transfer.setWaitingTime(String.format("%d dakika", waitingTime.toMinutes()));
            } catch (Exception e) {
                transfer.setWaitingTime("Bilinmiyor");
            }

            transferInfo.getTransferPoints().add(transfer);
        }

        return transferInfo;
    }
}