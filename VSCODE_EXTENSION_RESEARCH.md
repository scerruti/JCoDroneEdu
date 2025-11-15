# VS Code Extension for CoDrone EDU Control & Safety Override

**Issue**: #30  
**Date**: November 15, 2025  
**Status**: Research & Design Phase  

## Executive Summary

Develop a VS Code extension that provides:
1. **Drone monitoring UI** - Connect, view battery, sensors (no code needed)
2. **Safety override** - Emergency land/stop buttons available during code execution
3. **Debugging aid** - Real-time sensor visualization while code runs

**Problem**: If student code crashes while drone is flying, drone continues uncontrolled.  
**Solution**: Extension provides always-available emergency controls outside the program.

---

## Use Cases

### 1. Classroom Startup (No Code)
```
Student:
1. Clicks "Connect" in extension
2. Sees battery status, ready to fly
3. Runs their code
4. Sees real-time sensor readings
```

### 2. Program Crash (Emergency)
```
Student code crashes mid-flight
→ Drone still flying, uncontrolled
→ Student clicks "Emergency Land" in extension
→ Extension directly commands land() (bypasses crashed code)
→ Drone lands safely
```

### 3. Debugging (Real-Time Feedback)
```
Code running, outputting sensor data
→ Extension shows same data on sidebar
→ Student can correlate code output with live drone state
→ Helps understand sensor fusion, timing, etc.
```

---

## Architecture Options

### Option A: Separate Backend Process (Recommended)

```
┌─────────────────────────────────────────┐
│         VS Code Extension               │
│  ┌──────────────────────────────────┐  │
│  │  WebView UI (React/HTML)         │  │
│  │  - Connect button                │  │
│  │  - Battery, sensors              │  │
│  │  - Land/Stop buttons             │  │
│  └──────────────────────────────────┘  │
└────────┬────────────────────────────────┘
         │ WebSocket/HTTP
         ▼
┌─────────────────────────────────────────┐
│   Standalone Java Backend Server        │
│  ┌──────────────────────────────────┐  │
│  │  DroneControlServer              │  │
│  │  - Manages drone connections     │  │
│  │  - Exposes REST API              │  │
│  │  - Handles emergency commands    │  │
│  └──────────────────────────────────┘  │
└────────┬────────────────────────────────┘
         │ Bluetooth/Serial
         ▼
      Drone
```

**Pros:**
- ✅ Clean separation of concerns
- ✅ Java backend already mature
- ✅ Reusable by other tools (not just VS Code)
- ✅ Can run on different machine (networked)
- ✅ No JVM embedded in extension

**Cons:**
- ❌ Requires launching separate process
- ❌ One more thing to manage

### Option B: Extension Calls Student's Code Process

```
┌─────────────────────────────────────────┐
│         VS Code Extension               │
│  ┌──────────────────────────────────┐  │
│  │  WebView UI                      │  │
│  └──────────────────────────────────┘  │
└────────┬────────────────────────────────┘
         │ Hooks into Debug Adapter / stdio
         ▼
┌─────────────────────────────────────────┐
│    Student's Running Java Program       │
│  ┌──────────────────────────────────┐  │
│  │  public class StudentCode {      │  │
│  │    Drone drone = new Drone();    │  │
│  │    // their code here            │  │
│  │  }                               │  │
│  └──────────────────────────────────┘  │
│              │                          │
│              └── Drone instance         │
└────────┬────────────────────────────────┘
         │ Bluetooth/Serial
         ▼
      Drone
```

**Pros:**
- ✅ No separate process needed
- ✅ Can inspect student's Drone object
- ✅ Direct integration with their code

**Cons:**
- ❌ Fragile - depends on debug protocol
- ❌ Hard to implement emergency override (code might be hung)
- ❌ Complex debug adapter protocol

### Option C: Mock Data / Simulator First

```
Test extension UI without real drone:
- Extension connects to simulator
- Shows mock sensor data
- Buttons send commands to simulator
- Validate UX before real implementation
```

**Pros:**
- ✅ Fast prototyping
- ✅ Test without drone
- ✅ Classroom-safe testing

**Cons:**
- ❌ Doesn't solve real problem until connected to backend

---

## Recommended Approach: Option A + Option C

1. **Phase 1**: Build extension UI + mock backend
   - Prototype in 2-3 days
   - No real drone needed
   - Validate UX/design

2. **Phase 2**: Build real Java backend server
   - Handles drone connection pooling
   - REST API with emergency commands
   - Safety-first design

3. **Phase 3**: Connect extension to real backend
   - Test with actual drone
   - Classroom pilot
   - Refinement based on feedback

---

## Extension Architecture

### Technology Stack

```
Frontend (Extension):
- TypeScript (VS Code SDK)
- WebView with React/HTML/CSS
- WebSocket client for real-time updates

Backend (Java):
- Spring Boot (embedded server)
- REST API + WebSocket
- Drone connection management
- Thread-safe command dispatch
```

### API Design

```
REST Endpoints:

POST   /api/drone/connect
       { "port": "/dev/ttyUSB0" }
       → { "connected": true, "battery": 85 }

GET    /api/drone/status
       → { "battery": 85, "altitude": 2.5, "sensors": {...} }

POST   /api/drone/command/land
       → { "status": "landing" }

POST   /api/drone/command/emergency-stop
       → { "status": "stopped" }

POST   /api/drone/command/takeoff
       → { "status": "taking_off" }

WebSocket: /ws/drone/telemetry
- Sends real-time sensor updates every 100ms
- Subscribable by extension
```

### UI Components

```
┌──────────────────────────────────────┐
│  CoDrone Control Panel               │
├──────────────────────────────────────┤
│                                      │
│  Status: ● Connected                 │
│  Battery: ████████░░ 85%             │
│  Altitude: 2.5m                      │
│                                      │
├──────────────────────────────────────┤
│  [Connect]  [Disconnect]             │
│  [Takeoff]  [Land]                   │
│  [🚨 EMERGENCY STOP]                 │
├──────────────────────────────────────┤
│  Sensors:                            │
│  ├─ Front Range: 45cm                │
│  ├─ Battery: 85%                     │
│  ├─ Temperature: 28°C                │
│  ├─ Acceleration X: 0.1g             │
│  └─ ...more                          │
│                                      │
└──────────────────────────────────────┘
```

---

## Implementation Roadmap

### Phase 1: Mock UI Prototype (1-2 days)

**Goal**: Validate UX with fake data

```
Extension:
✓ WebView panel
✓ Mock battery/sensor values
✓ Connect/Land/Stop buttons (no-ops)
✓ Real-time sensor display mock

Backend:
✓ Mock HTTP server on localhost:3000
✓ Returns static sensor data
✓ No actual drone connection
```

**Deliverable**: Screenshot of UI, user feedback

### Phase 2: Real Backend (2-3 days)

**Goal**: Build Java backend that manages real drone

```
Backend:
✓ Spring Boot server
✓ Drone connection management
✓ REST API (POST /connect, etc.)
✓ WebSocket for telemetry
✓ Emergency command thread (separate from main)
✓ Graceful error handling

Testing:
✓ Unit tests for API
✓ Integration tests with mock drone
✓ Manual testing with real hardware
```

**Deliverable**: Running server, API documentation

### Phase 3: Integration (1-2 days)

**Goal**: Connect extension to backend

```
Extension:
✓ Connect to real backend URL
✓ Use real API endpoints
✓ Handle connection errors
✓ Show real sensor data

Testing:
✓ End-to-end testing
✓ Crash recovery testing
✓ Emergency land verification
✓ Classroom pilot (5-10 students)
```

**Deliverable**: Production-ready extension + backend

---

## Safety Considerations

### Emergency Command Priority

```
Normal command queue:
  [takeoff] → [move_forward] → [move_backward] → [land]
  
Emergency command (INTERRUPTS):
  [EMERGENCY_STOP] → (clears queue, lands immediately)
```

**Implementation**:
```java
class DroneCommandDispatcher {
  // Normal commands
  Queue<Command> commandQueue = new LinkedList<>();
  
  // Emergency commands bypass queue
  void emergencyLand() {
    commandQueue.clear();
    drone.land();  // Direct, no wait
  }
}
```

### Failsafe Mechanisms

1. **Watchdog Timer**: If no heartbeat from extension → auto-land
2. **Connection Loss**: If drone loses Bluetooth → auto-land
3. **Command Timeout**: If command hangs → force stop
4. **Rate Limiting**: Prevent command flooding

---

## Questions for Clarification

1. **Scope**: Should this work with:
   - Solo drone running (no code)?
   - Student code + extension simultaneously?
   - Both?

2. **Deployment**: 
   - Student installs extension locally?
   - Backend runs on student machine or shared server?
   - School-wide deployment?

3. **Safety Certification**:
   - Does emergency override need to be verified/tested?
   - Any liability concerns?
   - Test automation requirements?

4. **Integration with Course**:
   - When would students use this?
   - Mandatory or optional?
   - Teacher-only feature or student-accessible?

---

## Files to Reference

- `src/main/java/com/otabi/jcodroneedu/gui/SensorMonitor.java` - Existing Swing UI
- `src/main/java/com/otabi/jcodroneedu/gui/ControllerMonitor.java` - Controller display
- `src/main/java/com/otabi/jcodroneedu/examples/SimpleSensorMonitor.java` - Simple example
- `src/main/java/com/otabi/jcodroneedu/examples/EasySensorMonitor.java` - Easy example

---

## Next Steps

1. **User Feedback**: Review use cases with teachers/students
2. **Architecture Decision**: Approve Option A approach?
3. **Phase 1 Start**: Begin mock UI prototype?
4. **Backend Design**: Design REST API schema in detail?

---

**Status**: Ready for discussion and feedback

---

## GitHub Codespaces Integration

### Emscripten Insights (Python Web Version)

The Python CoDrone library runs in Emscripten (WASM) + browser. Key learnings:

```
Python Architecture (Web):
┌─────────────────────────────────────────┐
│  Browser (Codespaces)                   │
│  ├─ Web UI (HTML/Canvas)                │
│  ├─ WebBluetooth API (if available)     │
│  └─ Emscripten Python (WASM)            │
│      └─ CoDrone EDU API (WASM)          │
└─────────────────────────────────────────┘
      │ Browser Bluetooth
      ▼ (sandboxed, limited)
    Drone
```

**What Python does well**:
- ✅ Works in any browser (instant deployment)
- ✅ Zero installation in Codespaces
- ✅ Real-time UI updates (WASM loop)
- ✅ WebBluetooth for drone connection

**Limitations**:
- ❌ Bluetooth only works in capable browsers (limited in Codespaces)
- ❌ Browser sandbox limits safety overrides
- ❌ No native access to system resources
- ❌ Performance limited by WASM

### Java Extension in Codespaces (Better Approach)

```
Codespaces Container:
┌─────────────────────────────────────────────────┐
│  VS Code in Browser                             │
│  ┌────────────────────────────────────────────┐ │
│  │  VS Code Extension (TypeScript)            │ │
│  │  - WebView UI                              │ │
│  │  - WebSocket client                        │ │
│  └────────────┬───────────────────────────────┘ │
│               │ WebSocket (localhost:8765)     │
│               ▼                                 │
│  ┌────────────────────────────────────────────┐ │
│  │  Java Backend (Docker container)           │ │
│  │  - Full CoDrone EDU API                    │ │
│  │  - Bluetooth access (if USB passthrough)   │ │
│  │  - REST/WebSocket server                  │ │
│  └────────────┬───────────────────────────────┘ │
│               │ Bluetooth/Serial               │
└───────────────┼──────────────────────────────────┘
                ▼
             Drone
```

**Why this is better for Codespaces**:
- ✅ Full Java runtime (same as local)
- ✅ No browser sandbox limitations
- ✅ Native Bluetooth support (via host passthrough)
- ✅ Same code works locally + in cloud
- ✅ Safety overrides guaranteed (not limited by browser)

### Multi-Environment Support Matrix

| Environment | UI | Backend | Bluetooth | Status |
|-------------|----|---------|-----------| -------|
| Local VS Code | TypeScript | Java (local) | Native | ✅ Full support |
| Codespaces | TypeScript | Java (container) | USB passthrough* | ✅ With setup |
| Codespaces | TypeScript | Java (SSH tunnel) | Local machine | ✅ Hybrid mode |
| Codespaces | TypeScript | Mock | Simulated | ✅ Development |

*USB passthrough requires admin configuration, not default

### Handling Bluetooth Access

**Challenge**: Codespaces containers run in cloud, no hardware access by default

**Solutions**:

1. **Local + Cloud Hybrid**
   ```
   Student runs local backend: java -jar codrone-backend.jar
   Codespaces UI connects via SSH tunnel/public URL
   Real drone connects to local backend
   ```

2. **Mock Data During Development**
   ```java
   if (isDevelopment) {
     drone = new MockDrone();  // Simulated
   } else {
     drone = new Drone();      // Real
   }
   ```

3. **Shared School Backend**
   ```
   School server: Central Java backend
   Multiple student UIs connect remotely
   All drones managed by central server
   ```

**Recommendation**: Support local-first with optional cloud sync

---

## Comparison: Python Web vs Java Extension

| Feature | Python (Emscripten) | Java Extension |
|---------|-------------------|-----------------|
| **Browser Access** | ✅ Works in browser | ❌ Needs extension |
| **Bluetooth** | Limited (WebBluetooth) | ✅ Full support |
| **Performance** | Limited (WASM) | ✅ Full Java speed |
| **Safety Override** | Limited (sandboxed) | ✅ Guaranteed |
| **IDE Integration** | Separate | ✅ Native VS Code |
| **Codespaces** | Partial | ✅ Full (with setup) |
| **Development Speed** | Fast | Medium |
| **Production Ready** | Yes | In progress |

**Takeaway**: Java Extension trades some convenience for much better safety, performance, and control.

---

## Key Insights from Emscripten Research

1. **Message-based Communication** ← Adopt pattern from Emscripten
   - WASM sends/receives via network API
   - Extension should use WebSocket similarly

2. **Real-time Sensor Updates** ← Learn from WASM loop
   - Emscripten continuously updates UI
   - Backend should send sensor data at ~10Hz

3. **Cross-Platform UI** ← Use similar HTML/CSS approach
   - Same UI works on desktop, tablet, web
   - Use responsive design

4. **Graceful Degradation** ← Python web shows graceful fallbacks
   - Works with/without Bluetooth
   - Mock data when not connected

5. **Browser Integration** ← NOT a pattern to follow
   - Don't limit Java extension to browser sandbox
   - Use native capabilities instead

---

**Status**: Ready for discussion and feedback

**Key Insight**: Java Extension + Codespaces can exceed Python's web approach by combining:
- Full Java runtime capabilities
- WebSocket communication (inspired by Emscripten)
- Native safety guarantees
- IDE integration benefits

