# Sprint 3: Protocols, Communication, and Utilities

**Sprint Goal:** Achieve comprehensive test coverage for communication protocol implementation and utility classes.

**Priority:** 🟡 Medium-High  
**Duration:** 2 weeks  
**Target Coverage:** 70%+ for protocol and utility classes

---

## Sprint Backlog

### Issue #21: Add unit tests for protocol.Header packet structure
**Component:** `protocol/Header.java`  
**Priority:** High  
**Labels:** `sprint-3`, `priority-high`, `test`, `coverage`, `unit-test`, `component-protocol`

**Methods to Cover:**
- Header construction
- Byte serialization
- Byte deserialization
- Field getters/setters
- Header validation
- Length calculation

**Test Scenarios:**
1. Test header construction with all fields
2. Test byte array serialization
3. Test byte array deserialization
4. Test field validation rules
5. Test length calculation accuracy
6. Test invalid header detection
7. Test header equality comparison
8. Test endianness handling

**Acceptance Criteria:**
- [ ] Header serialization tested
- [ ] Deserialization verified
- [ ] Validation rules tested
- [ ] Length calculations correct
- [ ] Endianness handled properly
- [ ] Edge cases covered

---

### Issue #22: Add unit tests for protocol.linkmanager.Address management
**Component:** `protocol/linkmanager/Address.java`  
**Priority:** High  
**Labels:** `sprint-3`, `priority-high`, `test`, `coverage`, `unit-test`, `component-protocol`

**Methods to Cover:**
- Address packet creation
- Address encoding/decoding
- Device address storage
- Address validation
- Address comparison

**Test Scenarios:**
1. Test address packet creation
2. Test encoding to byte array
3. Test decoding from byte array
4. Test device address validation
5. Test address format verification
6. Test broadcast vs unicast addresses
7. Test address equality/comparison
8. Test invalid address handling

**Acceptance Criteria:**
- [ ] Packet creation tested
- [ ] Encoding/decoding verified
- [ ] Validation implemented
- [ ] Comparison logic tested
- [ ] Special addresses handled
- [ ] Format compliance verified

---

### Issue #23: Add unit tests for protocol.linkmanager.Ack acknowledgment handling
**Component:** `protocol/linkmanager/Ack.java`  
**Priority:** High  
**Labels:** `sprint-3`, `priority-high`, `test`, `coverage`, `unit-test`, `component-protocol`

**Methods to Cover:**
- Ack packet creation
- Ack parsing
- Sequence number handling
- Success/failure indication
- Timeout detection

**Test Scenarios:**
1. Test ack packet creation
2. Test ack parsing from bytes
3. Test sequence number matching
4. Test success acknowledgment
5. Test failure acknowledgment
6. Test timeout handling
7. Test duplicate ack detection
8. Test out-of-order acks

**Acceptance Criteria:**
- [ ] Ack creation tested
- [ ] Parsing logic verified
- [ ] Sequence matching validated
- [ ] Status indication tested
- [ ] Timeout handling verified
- [ ] Edge cases covered

---

### Issue #24: Add unit tests for protocol.linkmanager.Message encoding/decoding
**Component:** `protocol/linkmanager/Message.java`  
**Priority:** High  
**Labels:** `sprint-3`, `priority-high`, `test`, `coverage`, `unit-test`, `component-protocol`

**Methods to Cover:**
- Message creation
- Payload encoding
- Message parsing
- Type identification
- CRC validation
- Message fragmentation

**Test Scenarios:**
1. Test message creation with payload
2. Test encoding to wire format
3. Test parsing from byte stream
4. Test message type identification
5. Test CRC calculation and validation
6. Test fragmentation for large messages
7. Test reassembly of fragments
8. Test corrupted message detection

**Acceptance Criteria:**
- [ ] Message encoding tested
- [ ] Decoding/parsing verified
- [ ] Type identification validated
- [ ] CRC correctly implemented
- [ ] Fragmentation handled
- [ ] Error detection working

---

### Issue #25: Add unit tests for protocol.linkmanager.Registration device pairing
**Component:** `protocol/linkmanager/Registration.java`  
**Priority:** High  
**Labels:** `sprint-3`, `priority-high`, `test`, `coverage`, `unit-test`, `component-protocol`

**Methods to Cover:**
- Registration request creation
- Registration response parsing
- Device pairing process
- Registration state management
- Timeout handling

**Test Scenarios:**
1. Test registration request creation
2. Test response parsing
3. Test pairing handshake sequence
4. Test successful registration
5. Test registration rejection
6. Test timeout during registration
7. Test re-registration handling
8. Test device capability exchange

**Acceptance Criteria:**
- [ ] Registration flow tested
- [ ] Request/response verified
- [ ] State management validated
- [ ] Timeout handling tested
- [ ] Pairing sequence verified
- [ ] Edge cases covered

---

### Issue #26: Add unit tests for protocol.control flight command packets
**Component:** `protocol/control/` package  
**Priority:** High  
**Labels:** `sprint-3`, `priority-high`, `test`, `coverage`, `unit-test`, `component-protocol`

**Methods to Cover:**
- Control packet structure
- Flight command encoding
- Parameter serialization
- Command validation
- Range checking

**Test Scenarios:**
1. Test control packet creation
2. Test flight command encoding (pitch, roll, yaw, throttle)
3. Test parameter serialization
4. Test value range validation
5. Test command priority handling
6. Test emergency command encoding
7. Test invalid command detection
8. Test command sequence ordering

**Acceptance Criteria:**
- [ ] Packet structure tested
- [ ] Encoding verified
- [ ] Validation implemented
- [ ] Range checking tested
- [ ] Priority handling verified
- [ ] Sequence ordering tested

---

### Issue #27: Add unit tests for protocol.display display command encoding
**Component:** `protocol/display/` package  
**Priority:** Medium  
**Labels:** `sprint-3`, `priority-medium`, `test`, `coverage`, `unit-test`, `component-protocol`

**Methods to Cover:**
- Display packet structure
- Drawing command encoding
- Color encoding
- Text encoding
- Buffer management

**Test Scenarios:**
1. Test display packet creation
2. Test drawing command encoding (line, rect, circle)
3. Test text command encoding
4. Test color value encoding
5. Test coordinate encoding
6. Test clear/show commands
7. Test batch command optimization
8. Test buffer overflow handling

**Acceptance Criteria:**
- [ ] Packet structure tested
- [ ] Drawing commands encoded
- [ ] Text encoding verified
- [ ] Color handling tested
- [ ] Buffer management validated
- [ ] Optimization verified

---

### Issue #28: Add unit tests for protocol.buzzer sound command packets
**Component:** `protocol/buzzer/` package  
**Priority:** Medium  
**Labels:** `sprint-3`, `priority-medium`, `test`, `coverage`, `unit-test`, `component-protocol`

**Methods to Cover:**
- Buzzer packet structure
- Note encoding
- Duration encoding
- Frequency calculations
- Melody encoding

**Test Scenarios:**
1. Test buzzer packet creation
2. Test note frequency encoding
3. Test duration encoding
4. Test melody sequence encoding
5. Test rest/pause encoding
6. Test volume control encoding
7. Test invalid frequency handling
8. Test melody buffer management

**Acceptance Criteria:**
- [ ] Packet structure tested
- [ ] Note encoding verified
- [ ] Duration handling tested
- [ ] Frequency calculations correct
- [ ] Melody encoding validated
- [ ] Buffer management tested

---

### Issue #29: Add unit tests for protocol.dronestatus status packet parsing
**Component:** `protocol/dronestatus/` package  
**Priority:** High  
**Labels:** `sprint-3`, `priority-high`, `test`, `coverage`, `unit-test`, `component-protocol`

**Methods to Cover:**
- Status packet parsing
- Battery level extraction
- Flight state parsing
- Sensor data extraction
- Timestamp parsing

**Test Scenarios:**
1. Test status packet parsing
2. Test battery level extraction
3. Test flight state detection
4. Test sensor data parsing
5. Test timestamp extraction
6. Test error flag detection
7. Test partial packet handling
8. Test status change detection

**Acceptance Criteria:**
- [ ] Parsing logic tested
- [ ] Data extraction verified
- [ ] State detection validated
- [ ] Error handling tested
- [ ] Timestamp parsing correct
- [ ] Change detection working

---

### Issue #30: Add unit tests for CRC16 checksum calculation
**Component:** `CRC16.java`  
**Priority:** High  
**Labels:** `sprint-3`, `priority-high`, `test`, `coverage`, `unit-test`, `component-protocol`

**Methods to Cover:**
- CRC calculation
- Checksum verification
- Byte array processing
- Polynomial handling

**Test Scenarios:**
1. Test CRC calculation with known values
2. Test checksum verification
3. Test empty data handling
4. Test single byte CRC
5. Test large data blocks
6. Test known test vectors
7. Test bit error detection
8. Test performance benchmarks

**Acceptance Criteria:**
- [ ] Calculation tested with vectors
- [ ] Verification logic correct
- [ ] Edge cases handled
- [ ] Test vectors pass
- [ ] Error detection validated
- [ ] Performance acceptable

---

### Issue #31: Add unit tests for LinkManager connection management
**Component:** `LinkManager.java`  
**Priority:** High  
**Labels:** `sprint-3`, `priority-high`, `test`, `coverage`, `unit-test`, `component-protocol`

**Methods to Cover:**
- Connection establishment
- Connection maintenance
- Disconnection handling
- Reconnection logic
- Connection state tracking

**Test Scenarios:**
1. Test connection initialization
2. Test successful connection establishment
3. Test connection failure handling
4. Test disconnection process
5. Test automatic reconnection
6. Test connection timeout
7. Test multiple connection attempts
8. Test connection state transitions

**Acceptance Criteria:**
- [ ] Connection flow tested
- [ ] State management verified
- [ ] Timeout handling tested
- [ ] Reconnection logic validated
- [ ] Error recovery tested
- [ ] State transitions verified

---

### Issue #32: Add unit tests for LinkController protocol orchestration
**Component:** `LinkController.java`  
**Priority:** High  
**Labels:** `sprint-3`, `priority-high`, `test`, `coverage`, `unit-test`, `component-protocol`

**Methods to Cover:**
- Message sending
- Message receiving
- Protocol state machine
- Command queuing
- Response handling

**Test Scenarios:**
1. Test message sending pipeline
2. Test message receiving processing
3. Test protocol state transitions
4. Test command queue management
5. Test response matching to requests
6. Test concurrent operations
7. Test error propagation
8. Test protocol recovery

**Acceptance Criteria:**
- [ ] Send/receive tested
- [ ] State machine verified
- [ ] Queue management validated
- [ ] Response matching tested
- [ ] Concurrency handled
- [ ] Error recovery tested

---

### Issue #33: Add unit tests for SerialPortManager port discovery and management
**Component:** `SerialPortManager.java`  
**Priority:** Medium  
**Labels:** `sprint-3`, `priority-medium`, `test`, `coverage`, `unit-test`, `component-system`

**Methods to Cover:**
- Port discovery
- Port opening/closing
- Read/write operations
- Port configuration
- Error handling

**Test Scenarios:**
1. Test port discovery/enumeration
2. Test port opening
3. Test port closing
4. Test read operations
5. Test write operations
6. Test port configuration settings
7. Test port busy handling
8. Test port disconnect detection

**Acceptance Criteria:**
- [ ] Discovery logic tested
- [ ] Open/close operations verified
- [ ] Read/write tested
- [ ] Configuration validated
- [ ] Error handling tested
- [ ] Disconnect detection working

---

### Issue #34: Add unit tests for InvalidMessageException error handling
**Component:** `InvalidMessageException.java`  
**Priority:** Medium  
**Labels:** `sprint-3`, `priority-medium`, `test`, `coverage`, `unit-test`, `component-protocol`

**Methods to Cover:**
- Exception construction
- Message formatting
- Context preservation
- Error reporting

**Test Scenarios:**
1. Test exception creation with message
2. Test exception with cause
3. Test message formatting
4. Test context data preservation
5. Test exception hierarchy
6. Test error message clarity

**Acceptance Criteria:**
- [ ] Exception construction tested
- [ ] Message formatting verified
- [ ] Context preservation validated
- [ ] Hierarchy correct
- [ ] Educational messages clear

---

## Sprint Success Metrics

- ✅ All 14 issues completed
- ✅ 70%+ code coverage on protocol classes
- ✅ All protocol encoders/decoders tested
- ✅ CRC validation verified
- ✅ Communication layer fully tested
- ✅ Integration with Sprint 1 APIs validated

## Sprint Dependencies

- Requires protocol specification documentation
- Needs test vectors for CRC and encoding
- May need protocol analyzer tools
- Depends on Sprint 1 for API integration

## Sprint Review Agenda

1. Demo protocol test coverage
2. Review encoding/decoding tests
3. Discuss CRC validation results
4. Review communication reliability tests
5. Plan Sprint 4 integration focus

---

**Sprint Start:** [To be scheduled]  
**Sprint End:** [To be scheduled]  
**Sprint Owner:** [To be assigned]  
**Team Members:** [To be assigned]
