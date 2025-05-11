import sys
import requests
import json
import os
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

API_KEY = "AIzaSyCGObqinZJ7fug_noyIqFpQswEV6qizCjo"

def get_safe_value(data, key, default=None):
    """Safely get a value from a dictionary with a default if not present."""
    return data.get(key, default)

def main(origin, destination):
    try:
        if not API_KEY:
            raise ValueError("Google Maps API key not found in environment variables")
            
        url = f"https://maps.googleapis.com/maps/api/directions/json?origin={origin}&destination={destination}&mode=transit&language=tr&key={API_KEY}"
        response = requests.get(url)
        data = response.json()
        
        if data['status'] != 'OK':
            error_response = {
                "error": True,
                "message": f"Google Maps API Error: {data['status']}",
                "details": data.get('error_message', 'No details available')
            }
            print(json.dumps(error_response, ensure_ascii=False))
            return

        result = {
            "error": False,
            "summary": {
                "total_distance": data['routes'][0]['legs'][0]['distance']['text'],
                "total_duration": data['routes'][0]['legs'][0]['duration']['text'],
                "start_address": data['routes'][0]['legs'][0]['start_address'],
                "end_address": data['routes'][0]['legs'][0]['end_address'],
                "fare": get_safe_value(data['routes'][0], 'fare', {}).get('text')
            },
            "steps": []
        }

        steps = data['routes'][0]['legs'][0]['steps']
        for step in steps:
            step_info = {
                "travel_mode": step['travel_mode'],
                "distance": step['distance']['text'],
                "duration": step['duration']['text'],
                "instructions": step['html_instructions'],
                "maneuver": step.get('maneuver', None)
            }

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
                step_info["transit_details"] = {
                    "line": {
                        "name": get_safe_value(line, 'name', 'Unknown'),
                        "short_name": get_safe_value(line, 'short_name', 'Unknown'),
                        "vehicle_type": get_safe_value(line, 'vehicle', {}).get('type', 'Unknown'),
                        "color": get_safe_value(line, 'color', '#000000')  # Default black if no color
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
                    "num_stops": transit['num_stops'],
                    "headsign": transit['headsign']
                }

            result["steps"].append(step_info)

        print(json.dumps(result, ensure_ascii=False))

    except requests.exceptions.RequestException as e:
        error_response = {
            "error": True,
            "message": "Failed to connect to Google Maps API",
            "details": str(e)
        }
        print(json.dumps(error_response, ensure_ascii=False))
    except (KeyError, IndexError) as e:
        error_response = {
            "error": True,
            "message": "Invalid response format from Google Maps API",
            "details": str(e)
        }
        print(json.dumps(error_response, ensure_ascii=False))
    except Exception as e:
        error_response = {
            "error": True,
            "message": "An unexpected error occurred",
            "details": str(e)
        }
        print(json.dumps(error_response, ensure_ascii=False))

if __name__ == "__main__":
    if len(sys.argv) != 3:
        error_response = {
            "error": True,
            "message": "Invalid number of arguments",
            "details": "Expected origin and destination coordinates"
        }
        print(json.dumps(error_response, ensure_ascii=False))
        sys.exit(1)
    
    origin = sys.argv[1]
    destination = sys.argv[2]
    main(origin, destination) 