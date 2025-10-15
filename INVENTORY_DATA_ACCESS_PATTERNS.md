# Inventory Data Access Patterns

## Overview

The JCoDroneEdu library provides **three different ways** to access inventory data, giving developers flexibility to choose the style that best fits their needs.

## Access Patterns

### 1. Python-Compatible Arrays (`Object[]`)

Matches the Python CoDrone EDU API exactly for easy code translation.

```java
Object[] countData = drone.getCountData();
int flightTime = (int) countData[1];  // Requires casting
int takeoffs = (int) countData[2];
int landings = (int) countData[3];
```

**Pros:**
- ✅ Exact Python API compatibility
- ✅ Students can translate Python examples directly
- ✅ Single method call returns all data

**Cons:**
- ❌ Requires casting from Object
- ❌ No type safety
- ❌ Must remember array indices

**When to use:** When teaching students who are familiar with Python, or when translating Python examples.

---

### 2. Individual Typed Getters

Java methods that return specific values with proper types.

```java
int flightTime = drone.getFlightTime();
int takeoffs = drone.getTakeoffCount();
int landings = drone.getLandingCount();
int accidents = drone.getAccidentCount();
```

**Pros:**
- ✅ Type-safe (no casting needed)
- ✅ Clear method names (self-documenting)
- ✅ Best for getting single values
- ✅ Good IDE autocomplete

**Cons:**
- ❌ Multiple method calls for multiple values
- ❌ Each call requests data from drone (overhead)

**When to use:** When you only need one or two specific values.

---

### 3. Java Composite Objects (RECOMMENDED ⭐)

Type-safe objects with all related data together.

```java
CountData countData = drone.getCountDataObject();
System.out.println(countData);  // Nice toString() output
int flightTime = countData.getFlightTime();
int takeoffs = countData.getTakeoffCount();
double timestamp = countData.getTimestamp();
```

**Pros:**
- ✅ Type-safe with proper Java types
- ✅ Single method call retrieves all data
- ✅ Descriptive field names via getters
- ✅ Includes timestamp
- ✅ Has toString() for easy debugging
- ✅ Most Java-idiomatic
- ✅ Best IDE support

**Cons:**
- ❌ New classes to learn (but well-documented)
- ❌ Not available in Python version

**When to use:** Recommended for Java developers and production code.

---

## Available Composite Classes

### CountData
Flight statistics from the drone:
- `getTimestamp()` - When data was retrieved
- `getFlightTime()` - Total flight time in seconds
- `getTakeoffCount()` - Total takeoffs
- `getLandingCount()` - Total landings
- `getAccidentCount()` - Total accidents/crashes

```java
CountData data = drone.getCountDataObject();
System.out.println("Flight time: " + data.getFlightTime() + " seconds");
```

### InformationData
Device model and firmware information:
- `getTimestamp()` - When data was retrieved
- `getDroneModel()` - Drone model number
- `getDroneFirmware()` - Drone firmware version (e.g., "1.2.3")
- `getControllerModel()` - Controller model number
- `getControllerFirmware()` - Controller firmware version

```java
InformationData info = drone.getInformationDataObject();
System.out.println("Drone: " + info.getDroneModel() + " v" + info.getDroneFirmware());
```

### CpuIdData
Unique CPU identifiers:
- `getTimestamp()` - When data was retrieved
- `getDroneCpuId()` - Drone's 96-bit CPU ID (hex string)
- `getControllerCpuId()` - Controller's 96-bit CPU ID (hex string)

```java
CpuIdData cpuIds = drone.getCpuIdDataObject();
System.out.println("Drone ID: " + cpuIds.getDroneCpuId());
```

### AddressData
Bluetooth addresses:
- `getTimestamp()` - When data was retrieved
- `getDroneAddress()` - Drone's Bluetooth address
- `getControllerAddress()` - Controller's Bluetooth address

```java
AddressData addresses = drone.getAddressDataObject();
System.out.println("Drone: " + addresses.getDroneAddress());
```

---

## Method Reference

### Count Data Methods
| Method | Return Type | Description |
|--------|-------------|-------------|
| `getCountData()` | `Object[]` | Python-style array |
| `getCountData(double delay)` | `Object[]` | Python-style with custom delay |
| `getCountDataObject()` | `CountData` | Java composite object ⭐ |
| `getCountDataObject(double delay)` | `CountData` | Java composite with custom delay ⭐ |
| `getFlightTime()` | `int` | Individual getter |
| `getTakeoffCount()` | `int` | Individual getter |
| `getLandingCount()` | `int` | Individual getter |
| `getAccidentCount()` | `int` | Individual getter |

### Information Data Methods
| Method | Return Type | Description |
|--------|-------------|-------------|
| `getInformationData()` | `Object[]` | Python-style array |
| `getInformationData(double delay)` | `Object[]` | Python-style with custom delay |
| `getInformationDataObject()` | `InformationData` | Java composite object ⭐ |
| `getInformationDataObject(double delay)` | `InformationData` | Java composite with custom delay ⭐ |

### CPU ID Methods
| Method | Return Type | Description |
|--------|-------------|-------------|
| `getCpuIdData()` | `Object[]` | Python-style array |
| `getCpuIdData(double delay)` | `Object[]` | Python-style with custom delay |
| `getCpuIdDataObject()` | `CpuIdData` | Java composite object ⭐ |
| `getCpuIdDataObject(double delay)` | `CpuIdData` | Java composite with custom delay ⭐ |

### Address Methods
| Method | Return Type | Description |
|--------|-------------|-------------|
| `getAddressData()` | `Object[]` | Python-style array |
| `getAddressData(double delay)` | `Object[]` | Python-style with custom delay |
| `getAddressDataObject()` | `AddressData` | Java composite object ⭐ |
| `getAddressDataObject(double delay)` | `AddressData` | Java composite with custom delay ⭐ |

---

## Teaching Recommendations

### For Students Coming from Python
Start with the array-based methods (`getCountData()`) since they match Python syntax. This makes the transition easier.

```python
# Python
count_data = drone.get_count_data()
flight_time = count_data[1]
```

```java
// Java (Python-style)
Object[] countData = drone.getCountData();
int flightTime = (int) countData[1];
```

### For Students Learning Java
Use the composite objects (`getCountDataObject()`) from the start. They teach proper Java patterns and type safety.

```java
CountData data = drone.getCountDataObject();
int flightTime = data.getFlightTime();
System.out.println(data);  // Nice formatted output
```

### For Quick Queries
Use individual getters when you only need one value:

```java
System.out.println("Flight time: " + drone.getFlightTime());
```

---

## Example Code

See `src/main/java/com/otabi/jcodroneedu/examples/InventoryDataExample.java` for a complete working example demonstrating all three access patterns.

---

## Summary

| Pattern | Best For | Type Safety | Python Compatible |
|---------|----------|-------------|-------------------|
| Arrays | Python transition | ❌ | ✅ |
| Individual Getters | Single values | ✅ | Partial |
| Composite Objects ⭐ | Java developers | ✅ | ❌ |

**Recommendation:** Use composite objects (`getCountDataObject()`, etc.) for production Java code. They're the most Java-idiomatic and provide the best developer experience.
