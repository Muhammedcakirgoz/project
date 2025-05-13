import sys
import requests
import json
import os
import urllib.parse
import polyline

# API anahtarı
API_KEY = "AIzaSyCGObqinZJ7fug_noyIqFpQswEV6qizCjo"

def get_safe_value(data, key, default=None):
    """Safely get a value from a dictionary with a default if not present."""
    return data.get(key, default)

def main(origin, destination):
    try:
        if not API_KEY:
            raise ValueError("Google Maps API key not found in environment variables")
        
        # Koordinat formatını kontrol et, metin ise geocoding yap
        if not ',' in destination or not any(c.isdigit() for c in destination):
            # Destination bir adres olabilir, geocoding yap
            geocode_url = f"https://maps.googleapis.com/maps/api/geocode/json?address={urllib.parse.quote(destination)}&key={API_KEY}"
            geocode_response = requests.get(geocode_url)
            geocode_data = geocode_response.json()
            
            if geocode_data['status'] == 'OK':
                lat = geocode_data['results'][0]['geometry']['location']['lat']
                lng = geocode_data['results'][0]['geometry']['location']['lng']
                destination = f"{lat},{lng}"
                print(f"INFO: Adres çözümlendi: {destination}", file=sys.stderr)
            else:
                error_response = {
                    "error": True,
                    "status": "ERROR",
                    "message": f"Geocoding hatası: {geocode_data['status']}",
                    "details": geocode_data.get('error_message', 'Adres çözümlenemedi'),
                    "steps": []  # Boş array ekledik
                }
                print(json.dumps(error_response, ensure_ascii=False))
                return
            
        url = f"https://maps.googleapis.com/maps/api/directions/json?origin={origin}&destination={destination}&mode=transit&language=tr&key={API_KEY}"
        print(f"INFO: Directions API URL: {url}", file=sys.stderr)
        
        response = requests.get(url)
        data = response.json()
        
        if data['status'] != 'OK':
            error_response = {
                "error": True,
                "status": "ERROR",
                "message": f"Google Maps API Error: {data['status']}",
                "details": data.get('error_message', 'No details available'),
                "steps": []  # Boş array ekledik
            }
            print(json.dumps(error_response, ensure_ascii=False))
            return
        
        if len(data['routes']) == 0 or len(data['routes'][0]['legs']) == 0:
            error_response = {
                "error": True,
                "status": "ERROR",
                "message": "No routes found",
                "details": "Google Maps API returned empty routes array",
                "steps": []  # Boş array ekledik
            }
            print(json.dumps(error_response, ensure_ascii=False))
            return

        # Fare bilgisini güvenli şekilde al, yoksa varsayılan değer kullan
        fare_text = "₺0,00"  # Varsayılan fare değeri
        try:
            if 'fare' in data['routes'][0]:
                fare_text = data['routes'][0]['fare']['text']
        except (KeyError, IndexError, TypeError):
            pass

        result = {
            "error": False,
            "status": "OK",  # Transit işlemi durumu
            "message": "",  # Başarı durumunda boş mesaj
            "details": "",  # Başarı durumunda boş detay
            "summary": {
                "total_distance": data['routes'][0]['legs'][0]['distance']['text'],
                "total_duration": data['routes'][0]['legs'][0]['duration']['text'],
                "start_address": data['routes'][0]['legs'][0]['start_address'],
                "end_address": data['routes'][0]['legs'][0]['end_address'],
                "fare": fare_text
            },
            "steps": []  # Boş liste ile başlat, asla None/null olmamalı
        }

        steps = data['routes'][0]['legs'][0]['steps']
        def extract_step_info(step):
            step_info = {
                "travel_mode": step['travel_mode'],
                "distance": step['distance']['text'],
                "duration": step['duration']['text'],
                "instructions": step.get('html_instructions', ''),
                "polyline": step['polyline']['points'] if 'polyline' in step else "",
                "polyline_path": []
            }
            # Polyline decode
            if 'polyline' in step:
                try:
                    coords = polyline.decode(step['polyline']['points'])
                    step_info["polyline_path"] = [{"lat": lat, "lng": lng} for lat, lng in coords]
                except Exception:
                    step_info["polyline_path"] = []
            # Handle sub-steps recursively
            if 'steps' in step:
                step_info["sub_steps"] = [extract_step_info(sub_step) for sub_step in step['steps']]
            if step['travel_mode'] == 'WALKING':
                step_info["walking_details"] = {
                    "start_location": {
                        "lat": step['start_location']['lat'],
                        "lng": step['start_location']['lng']
                    },
                    "end_location": {
                        "lat": step['end_location']['lat'],
                        "lng": step['end_location']['lng']
                    }
                }
            elif step['travel_mode'] == 'TRANSIT':
                transit = step['transit_details']
                line = transit['line']
                vehicle_type = "BUS"
                try:
                    vehicle_type = line.get('vehicle', {}).get('type', 'BUS').upper()
                except (KeyError, AttributeError):
                    pass
                step_info["transit_details"] = {
                    "line": {
                        "name": get_safe_value(line, 'name', 'Unknown Route'),
                        "short_name": get_safe_value(line, 'short_name', 'N/A'),
                        "vehicle_type": vehicle_type,
                        "color": get_safe_value(line, 'color', '#000000')
                    },
                    "departure_stop": {
                        "name": transit['departure_stop']['name'],
                        "location": {
                            "lat": transit['departure_stop']['location']['lat'],
                            "lng": transit['departure_stop']['location']['lng']
                        }
                    },
                    "arrival_stop": {
                        "name": transit['arrival_stop']['name'],
                        "location": {
                            "lat": transit['arrival_stop']['location']['lat'],
                            "lng": transit['arrival_stop']['location']['lng']
                        }
                    },
                    "departure_time": transit['departure_time']['text'],
                    "arrival_time": transit['arrival_time']['text'],
                    "num_stops": transit.get('num_stops', 0),
                    "headsign": transit.get('headsign', '')
                }
            return step_info
        result["steps"] = [extract_step_info(step) for step in steps]

        # Tüm değerleri kontrol et, null yerine boş string veya varsayılan değerler kullan
        for step in result["steps"]:
            for key, value in list(step.items()):
                if value is None:
                    if key == "instructions":
                        step[key] = "Yol tarifi bulunmuyor"
            
            # Transit detayları null kontrolü
            if "transit_details" in step:
                td = step["transit_details"]
                if td.get("headsign") is None:
                    td["headsign"] = ""
                line = td["line"]
                if line.get("name") is None:
                    line["name"] = "Unknown Route"
                if line.get("short_name") is None:
                    line["short_name"] = "N/A"

        # JSON formatında çıktı ver, Türkçe karakterleri düzgün destekleyen şekilde
        print(json.dumps(result, ensure_ascii=False))
        print(f"DEBUG: Final JSON output: {json.dumps(result, ensure_ascii=False)}", file=sys.stderr)

    except requests.exceptions.RequestException as e:
        error_response = {
            "error": True,
            "status": "ERROR",
            "message": "Failed to connect to Google Maps API",
            "details": str(e),
            "steps": []  # Boş array ekledik
        }
        print(json.dumps(error_response, ensure_ascii=False))
    except (KeyError, IndexError) as e:
        error_response = {
            "error": True,
            "status": "ERROR",
            "message": "Invalid response format from Google Maps API",
            "details": str(e),
            "steps": []  # Boş array ekledik
        }
        print(json.dumps(error_response, ensure_ascii=False))
    except Exception as e:
        error_response = {
            "error": True,
            "status": "ERROR",
            "message": "An unexpected error occurred",
            "details": str(e),
            "steps": []  # Boş array ekledik
        }
        print(json.dumps(error_response, ensure_ascii=False))

if __name__ == "__main__":
    if len(sys.argv) != 3:
        error_response = {
            "error": True,
            "status": "ERROR",
            "message": "Invalid number of arguments",
            "details": "Expected origin and destination coordinates",
            "steps": []  # Boş array ekledik
        }
        print(json.dumps(error_response, ensure_ascii=False))
        sys.exit(1)
    
    origin = sys.argv[1]
    destination = sys.argv[2]
    main(origin, destination)