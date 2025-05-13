package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class TransitResponse {
    private boolean error;
    private String status;
    private String message;
    private String details;
    private Summary summary;

    @JsonProperty("steps")
    private List<Step> steps = new ArrayList<>();

    @JsonProperty("transfer_info")
    private TransferInfo transferInfo;

    @Data
    public static class Summary {
        @JsonProperty("total_distance")
        private String totalDistance;

        @JsonProperty("total_duration")
        private String totalDuration;

        @JsonProperty("start_address")
        private String startAddress;

        @JsonProperty("end_address")
        private String endAddress;

        private String fare;

        // Sesli navigasyon için özet bilgisi
        @JsonProperty("voice_summary")
        public String getVoiceSummary() {
            return String.format("Toplam mesafe %s, yaklaşık %s sürecek. Ücret %s.",
                    totalDistance, totalDuration, fare);
        }
    }

    @Data
    public static class Step {
        @JsonProperty("travel_mode")
        private String travelMode;

        private String distance;
        private String duration;
        private String instructions;

        @JsonProperty("maneuver")
        private String maneuver;

        @JsonProperty("walking_details")
        private WalkingDetails walkingDetails;

        @JsonProperty("transit_details")
        private TransitDetails transitDetails;

        @JsonProperty("direction")
        private String direction;

        private String polyline;

        @JsonProperty("sub_steps")
        private List<Step> subSteps;

        @JsonProperty("polyline_path")
        private List<Location> polylinePath;

        // Sesli navigasyon için adım talimatı
        @JsonProperty("voice_instruction")
        public String getVoiceInstruction() {
            if ("WALKING".equals(travelMode)) {
                if (maneuver != null) {
                    switch (maneuver) {
                        case "turn-right":
                            return String.format("%s sonra sağa dönün.", distance);
                        case "turn-left":
                            return String.format("%s sonra sola dönün.", distance);
                        case "straight":
                        case "continue-straight":
                            return String.format("%s boyunca düz devam edin.", distance);
                        case "depart":
                            return "Yürümeye başlayın.";
                        case "arrive":
                            return "Varış noktasına ulaştınız.";
                        default:
                            return String.format("%s mesafede yürüyün. Yaklaşık %s sürecek.", distance, duration);
                    }
                }
                if (direction != null) {
                    return String.format("%s boyunca %s yürüyün. Yaklaşık %s sürecek.", distance, direction, duration);
                }
                return String.format("%s mesafede yürüyün. Yaklaşık %s sürecek.", distance, duration);
            } else if ("TRANSIT".equals(travelMode) && transitDetails != null) {
                return String.format("%s numaralı %s ile %s durağından %s durağına gidin. " +
                        "Kalkış %s, varış %s. Yaklaşık %s sürecek.",
                        getBusNumber(),
                        transitDetails.getLine().getVehicleType().toLowerCase(),
                        transitDetails.getDepartureStop().getName(),
                        transitDetails.getArrivalStop().getName(),
                        transitDetails.getDepartureTime(),
                        transitDetails.getArrivalTime(),
                        duration);
            }
            return instructions;
        }

        // Adım tipi için sesli açıklama
        @JsonProperty("step_type")
        public String getStepType() {
            return "WALKING".equals(travelMode) ? "yürüyüş" : "toplu taşıma";
        }

        @JsonProperty("bus_number")
        public String getBusNumber() {
            if (transitDetails != null && transitDetails.getLine() != null) {
                return transitDetails.getLine().getShortName();
            }
            return null;
        }

        public String getPolyline() {
            return polyline;
        }

        public void setPolyline(String polyline) {
            this.polyline = polyline;
        }

        public List<Location> getPolylinePath() {
            return polylinePath;
        }

        public void setPolylinePath(List<Location> polylinePath) {
            this.polylinePath = polylinePath;
        }
    }

    @Data
    public static class WalkingDetails {
        @JsonProperty("start_location")
        private Location startLocation;

        @JsonProperty("end_location")
        private Location endLocation;
    }

    @Data
    public static class TransitDetails {
        private Line line;

        @JsonProperty("departure_stop")
        private Stop departureStop;

        @JsonProperty("arrival_stop")
        private Stop arrivalStop;

        @JsonProperty("departure_time")
        private String departureTime;

        @JsonProperty("arrival_time")
        private String arrivalTime;

        @JsonProperty("num_stops")
        private int numStops;

        private String headsign;
    }

    @Data
    public static class Line {
        private String name;

        @JsonProperty("short_name")
        private String shortName;

        @JsonProperty("vehicle_type")
        private String vehicleType;

        private String color;
    }

    @Data
    public static class Stop {
        private String name;
        private Location location;
    }

    @Data
    public static class Location {
        private double lat;
        private double lng;
    }
}