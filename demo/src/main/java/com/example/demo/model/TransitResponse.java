package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class TransitResponse {
    private boolean error;
    private String message;
    private String details;
    private Summary summary;
    private List<Step> steps;

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
                return String.format("%s mesafede yürüyün. Yaklaşık %s sürecek.", distance, duration);
            } else if ("TRANSIT".equals(travelMode) && transitDetails != null) {
                return String.format("%s numaralı %s ile %s durağından %s durağına gidin. " +
                        "Kalkış %s, varış %s. Yaklaşık %s sürecek.",
                        transitDetails.getLine().getShortName(),
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

        public Line getLine() {
            return line;
        }

        public void setLine(Line line) {
            this.line = line;
        }

        public Stop getDepartureStop() {
            return departureStop;
        }

        public void setDepartureStop(Stop departureStop) {
            this.departureStop = departureStop;
        }

        public Stop getArrivalStop() {
            return arrivalStop;
        }

        public void setArrivalStop(Stop arrivalStop) {
            this.arrivalStop = arrivalStop;
        }

        public String getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(String departureTime) {
            this.departureTime = departureTime;
        }

        public String getArrivalTime() {
            return arrivalTime;
        }

        public void setArrivalTime(String arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public int getNumStops() {
            return numStops;
        }

        public void setNumStops(int numStops) {
            this.numStops = numStops;
        }

        public String getHeadsign() {
            return headsign;
        }

        public void setHeadsign(String headsign) {
            this.headsign = headsign;
        }
    }

    @Data
    public static class Line {
        private String name;

        @JsonProperty("short_name")
        private String shortName;

        @JsonProperty("vehicle_type")
        private String vehicleType;

        private String color;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    @Data
    public static class Stop {
        private String name;
        private Location location;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }

    @Data
    public static class Location {
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }
}