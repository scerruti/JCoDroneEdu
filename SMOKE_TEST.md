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

# If you need to pass a port name (macOS example)
./gradlew runSmokeTest --args='/dev/cu.usbserial-XXXX'
```

## macOS-specific permissions

On newer macOS versions the first time you access a USB serial device you may need to grant Terminal (or your IDE) access to "Files and Folders" or "Full Disk Access" in System Settings → Privacy & Security. If you get permission denied errors, try the following:

- Check the device list:
  - `ls /dev/cu.*` and `ls /dev/tty.*`
- Temporarily relax device permissions (use with caution):
  - `sudo chmod 666 /dev/cu.usbserial-XXXX`
- If the device driver is missing, install the appropriate driver (e.g., FTDI or Prolific) or use Homebrew casks where available.

Note: macOS sometimes shows a different device name for USB serial adapters (look for `usbserial` or `usbmodem` patterns).

## Troubleshooting and advanced checks

- If auto-detection fails, run:
  - `ls /dev/cu.*` or `ls /dev/tty.*` to locate possible serial devices.
  - `ioreg -p IOUSB -l -w 0 | grep -i -E "usb|serial"` (advanced) to list USB devices and their properties.
- Verify the device is not claimed by another program (e.g., `screen`, `minicom`, or an IDE serial monitor). Close other apps that might hold the port.
- Check that your user has sufficient rights to open the device. On Linux you might need to add your user to `dialout`/`uucp` groups; on macOS use the `chmod` trick above or grant Terminal/IDE access in System Preferences.
- Use `screen` to test connectivity (replace device path):
  - `screen /dev/cu.usbserial-XXXX 115200` (Ctrl-A then `\` to exit)
- Capture logs: set environment variable to enable debug logging for Log4j or pass `-Dorg.apache.logging.log4j.simplelog.StatusLogger.level=TRACE` to the JVM to increase logging verbosity.

If you get a stacktrace from `SerialPortManager` or `Receiver` please paste the console output (include the first 50-100 lines) into an issue and I'll help interpret it.

Next steps after smoke test
--------------------------
1. If `SmokeTest` shows the drone connected and reports battery/model: proceed to a supervised short flight test (takeoff -> hover -> land) using the guidelines in the repository README and the small example provided earlier.
2. If connection does not work, post the console logs here and I will help interpret them.

Safety reminder: always prefer running first with the `MockDrone` unit tests before using hardware.
