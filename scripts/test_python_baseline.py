#!/usr/bin/env python3
"""
Python Baseline Test - Post Firmware Update 25.2.1
Tests the Python library behavior with updated firmware
"""

import sys
import os

# Add the reference codrone_edu to path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'reference'))

from codrone_edu.drone import Drone

def main():
    print("=" * 60)
    print("Python CoDrone EDU Baseline Test")
    print("Testing with Firmware 25.2.1")
    print("=" * 60)
    print()
    
    drone = Drone()
    
    try:
        # Test 1: Connection
        print("📡 Test 1: Connection")
        print("-" * 60)
        drone.pair()
        print("✅ Connected successfully")
        print()
        
        # Test 2: Get Information Data (NEW METHOD TO TEST)
        print("📋 Test 2: Device Information")
        print("-" * 60)
        try:
            info = drone.get_information_data()
            print(f"Information Data: {info}")
            if hasattr(info, 'drone_version'):
                print(f"  Drone Version: {info.drone_version}")
            if hasattr(info, 'controller_version'):
                print(f"  Controller Version: {info.controller_version}")
        except Exception as e:
            print(f"⚠️  get_information_data() failed: {e}")
        print()
        
        # Test 3: CPU ID Data (NEW METHOD TO TEST)
        print("🔑 Test 3: Device CPU ID")
        print("-" * 60)
        try:
            cpu_id = drone.get_cpu_id_data()
            print(f"CPU ID Data: {cpu_id}")
        except Exception as e:
            print(f"⚠️  get_cpu_id_data() failed: {e}")
        print()
        
        # Test 4: Address Data (NEW METHOD TO TEST)
        print("📍 Test 4: Bluetooth Address")
        print("-" * 60)
        try:
            address = drone.get_address_data()
            print(f"Address Data: {address}")
        except Exception as e:
            print(f"⚠️  get_address_data() failed: {e}")
        print()
        
        # Test 5: Count Data (NEW METHOD TO TEST)
        print("📊 Test 5: Flight Statistics")
        print("-" * 60)
        try:
            count_data = drone.get_count_data()
            print(f"Count Data: {count_data}")
            
            # Try individual methods
            try:
                flight_time = drone.get_flight_time()
                print(f"  Flight Time: {flight_time} seconds")
            except:
                pass
            
            try:
                takeoffs = drone.get_takeoff_count()
                print(f"  Takeoff Count: {takeoffs}")
            except:
                pass
            
            try:
                landings = drone.get_landing_count()
                print(f"  Landing Count: {landings}")
            except:
                pass
            
            try:
                accidents = drone.get_accident_count()
                print(f"  Accident Count: {accidents}")
            except:
                pass
        except Exception as e:
            print(f"⚠️  get_count_data() failed: {e}")
        print()
        
        # Test 6: Altitude Reading
        print("📏 Test 6: Altitude Reading")
        print("-" * 60)
        try:
            # Get state data which includes altitude
            drone.get_pressure()  # Request pressure data
            altitude = drone.get_height()  # Get altitude from state
            pressure = drone.get_pressure()
            temperature = drone.get_temperature()
            
            print(f"  Altitude: {altitude} m")
            print(f"  Pressure: {pressure} hPa")
            print(f"  Temperature: {temperature} °C")
        except Exception as e:
            print(f"⚠️  Altitude test failed: {e}")
        print()
        
        # Test 7: Battery
        print("🔋 Test 7: Battery Level")
        print("-" * 60)
        try:
            battery = drone.get_battery()
            print(f"  Battery: {battery}%")
        except Exception as e:
            print(f"⚠️  Battery test failed: {e}")
        print()
        
        print("=" * 60)
        print("✅ Python baseline test complete!")
        print("=" * 60)
        
    except Exception as e:
        print(f"\n❌ Error during testing: {e}")
        import traceback
        traceback.print_exc()
    finally:
        print("\nDisconnecting...")
        drone.close()
        print("Disconnected.")

if __name__ == "__main__":
    main()
