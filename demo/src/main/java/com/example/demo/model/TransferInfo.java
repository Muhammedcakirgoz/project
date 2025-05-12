package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class TransferInfo {
    @JsonProperty("has_transfer")
    private boolean hasTransfer;

    @JsonProperty("transfer_count")
    private int transferCount;

    @JsonProperty("transfer_points")
    private List<TransferPoint> transferPoints = new ArrayList<>();

    @Data
    public static class TransferPoint {
        private String location;

        @JsonProperty("from_bus")
        private String fromBus;

        @JsonProperty("to_bus")
        private String toBus;

        @JsonProperty("arrival_time")
        private String arrivalTime;

        @JsonProperty("departure_time")
        private String departureTime;

        @JsonProperty("waiting_time")
        private String waitingTime;
    }
}