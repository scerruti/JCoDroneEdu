# API Comparison Report

**Java Version:** 1.5.0-SNAPSHOT
**Python API Version:** 2.6.0

## Summary

- **Python Methods:** 127
- **Java Methods:** 212
- **Matched Methods:** 136
  - Documented (@pythonEquivalent): 94
  - Inferred (by name): 42
- **In Python, Not Java:** 0
- **In Java, Not Python:** 76

**Note:** Java methods use @pythonEquivalent annotations to document their Python API mapping.

## Methods in Python but NOT in Java

✅ All Python methods have Java equivalents!

## Methods in Java but NOT in Python

ℹ️ Java-specific methods (expected):

- `autoConnect) throws DroneNotFoundException()`
- `autoConnect, String portName) throws DroneNotFoundException()`
- `changeSpeed()`
- `circleTurn()`
- `clearBias()`
- `clearCounter()`
- `connect()`
- `controllerBuzzerSequence()`
- `controllerClearArea()`
- `controllerDrawCircle()`
- `controllerInvertArea()`
- `droneBuzzerSequence()`
- `getAccel()`
- `getAccidentCount()`
- `getAddressData()`
- `getAddressDataObject()`
- `getAltitude()`
- `getAltitudeData()`
- `getAngle()`
- `getButtonDataObject()`
- `getCalculatedAltitude()`
- `getCalibratedTemperature()`
- `getCorrectedElevation()`
- `getCountData()`
- `getCountDataObject()`
- `getCpuIdData()`
- `getCpuIdDataObject()`
- `getDroneStatus()`
- `getElevation()`
- `getErrors()`
- `getFlightController()`
- `getFlightTime()`
- `getFlowData()`
- `getGyro()`
- `getInformationData()`
- `getInformationDataObject()`
- `getJoystickDataObject()`
- `getLandingCount()`
- `getLinkController()`
- `getLinkManager()`
- `getPositionX()`
- `getPositionY()`
- `getPositionZ()`
- `getReceiver()`
- `getSettingsController()`
- `getTakeoffCount()`
- `getTelemetryService()`
- `getUncalibratedTemperature()`
- `getUncorrectedElevation()`
- `go()`
- `isConnected()`
- `isOpen()`
- `loadColorClassifier()`
- `ping()`
- `registerBuzzerSequence()`
- `sendControl()`
- `sendControlPosition()`
- `sendControlWhile()`
- `sendRequest()`
- `sendRequestWait()`
- `setBacklight()`
- `setControllerLEDMode()`
- `setControllerMode()`
- `setDefault()`
- `setDroneLEDMode()`
- `setHeadlessMode()`
- `setLinkMode()`
- `square()`
- `triangleTurn()`
- `triggerFlightEvent()`
- `unloadColorClassifier()`
- `updateButtonData()`
- `updateJoystickData()`
- `useCalibratedTemperature()`
- `useCorrectedElevation()`
- `{()`

