Smoke Test and Safety Guide

Quick safety checklist and instructions for running the smoke test included in the repo.

Overview
--------
This repository contains an example smoke test to verify the Java API can connect to your CoDrone EDU controller and read basic information (battery, model). It intentionally does not perform automatic flights.

Files added:
- `src/main/java/com/otabi/jcodroneedu/examples/SmokeTest.java` - minimal runnable example that pairs/connects and prints battery.

Safety checklist (before any flight)
-----------------------------------
1. Clear flight area: no people or pets in the immediate area.
2. Remove loose objects that could be hit by propellers.
3. Use propeller guards or secure the drone if possible.
4. Charge battery to a safe level (>= 30% recommended for test flights).
5. Have a person ready to call emergency stop (or be ready to kill the program/process).
6. Test connections and battery via `SmokeTest` before attempting any takeoff.

How to run the smoke test
-------------------------
From your IDE:
- Run `com.otabi.jcodroneedu.examples.SmokeTest` as a Java application. Optionally pass a serial port as the first program argument (macOS example: `/dev/tty.usbserial-XXXX` or `/dev/cu.usbserial-XXXX`).

From the command line (Gradle):

```bash
# Build the project
./gradlew assemble

# Run SmokeTest via the Gradle task added to the build (runSmokeTest)
./gradlew runSmokeTest

# If you need to pass a port name
./gradlew runSmokeTest --args='/dev/cu.usbserial-XXXX'
```

Troubleshooting
---------------
- If auto-detection fails, locate your serial device (macOS):
  - `ls /dev/cu.*` or `ls /dev/tty.*` and look for a USB serial device.
  - Pass that path as the first program argument.
- If you see permission errors, ensure your macOS user has access to the serial device.
- Check logs (Log4j output) in the console for helpful messages from `SerialPortManager` and `Receiver`.

Next steps after smoke test
--------------------------
1. If `SmokeTest` shows the drone connected and reports battery/model: proceed to a supervised short flight test (takeoff -> hover -> land) using the guidelines in the repository README and the small example provided earlier.
2. If connection does not work, post the console logs here and I will help interpret them.

Safety reminder: always prefer running first with the `MockDrone` unit tests before using hardware.
