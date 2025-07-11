package com.otabi.jcodroneedu;

import java.util.HashMap;
import java.util.Map;

public class DroneSystem
{
    /**
     * Represents the model number of various devices.
     * <p>
     * Model numbers follow the format AAAABBCC, where:
     * <ul>
     * <li>AAAA: Project Number</li>
     * <li>BB: Device Type</li>
     * <li>CC: Revision</li>
     * </ul>
     */
    public enum ModelNumber {

        NONE_(0x00000000),

        // Drone 3 Series
        DRONE_3_DRONE_P1(0x00031001),   // Drone_3_Drone_P1 (Lightrone / GD65 / HW2181 / Keil / 3.7v / barometer / RGB LED / Shaking binding)
        DRONE_3_DRONE_P2(0x00031002),   // Drone_3_Drone_P2 (Soccer Drone / HW2181 / Keil / 7.4v / barometer / RGB LED / Shaking binding)
        DRONE_3_DRONE_P3(0x00031003),   // Drone_3_Drone_P3 (GD240 / HW2181 / Keil / power button / u30 flow / 3.7v / geared motor / barometer)
        DRONE_3_DRONE_P4(0x00031004),   // Drone_3_Drone_P4 (GD50N / HW2181 / Keil / power button / 3.7v / barometer)
        DRONE_3_DRONE_P5(0x00031005),   // Drone_3_Drone_P5 (GD30 / HW2181 / Keil / 3.7v / normal binding)
        DRONE_3_DRONE_P6(0x00031006),   // Drone_3_Drone_P6 (Soccer Drone 2 / HW2181 / Keil / 7.4v / barometer / RGB LED / Shaking binding)
        DRONE_3_DRONE_P7(0x00031007),   // Drone_3_Drone_P7 (SKYKICKV2 / SPI / HW2181 / Keil / 7.4v / barometer / RGB LED / Shaking binding)
        DRONE_3_DRONE_P8(0x00031008),   // Drone_3_Drone_P8 (GD65 / SPI / HW2181 / Keil / 3.7v / barometer / RGB LED / Shaking binding)
        DRONE_3_DRONE_P9(0x00031009),   // Drone_3_Drone_P9 (GD65 / SPI / HW2181 / Keil / 3.7v / barometer / RGB LED / Shaking binding / BladeType Power Connector)
        DRONE_3_DRONE_P10(0x0003100A),  // Drone_3_Drone_P10 (Battle Drone / SPI / HW2181 / Keil / 3.7v / barometer / RGB LED / Shaking binding)

        DRONE_3_CONTROLLER_P1(0x00032001),   // Drone_3_Controller_P1 / GD65 Controller / small size
        DRONE_3_CONTROLLER_P2(0x00032002),   // Drone_3_Controller_P2 / Skykick Controller / large size
        DRONE_3_CONTROLLER_P3(0x00032003),   // Drone_3_Controller_P3 / GD65 Controller USB / small size + USB
        DRONE_3_CONTROLLER_P4(0x00032004),   // Drone_3_Controller_P4 / Battle Drone Controller USB / small size + usb
        DRONE_3_CONTROLLER_P5(0x00032005),   // Drone_3_Controller_P5 / E-Drone 4m Controller / USB / HW2181B / Keil

        DRONE_3_LINK_P0(0x00033000),   // Drone_3_Link_P0

        DRONE_3_TESTER_P4(0x0003A004),   // Drone_3_Tester_P4 (obsolete)
        DRONE_3_TESTER_P6(0x0003A006),   // Drone_3_Tester_P6 - Battle Drone Tester

        // Drone 4 Series
        DRONE_4_DRONE_P4(0x00041004),   // Drone_4_Drone_P4 (obsolete)
        DRONE_4_DRONE_P5(0x00041005),   // Drone_4_Drone_P5 (HW2000, 2m range sensor)
        DRONE_4_DRONE_P6(0x00041006),   // Drone_4_Drone_P6 (HW2000B, 4m range sensor)
        DRONE_4_DRONE_P7(0x00041007),   // Drone_4_Drone_P7 (HW2000B, 4m range sensor, BLDC Motor)

        DRONE_4_CONTROLLER_P1(0x00042001),   // Drone_4_Controller_P1 (obsolete)
        DRONE_4_CONTROLLER_P2(0x00042002),   // Drone_4_Controller_P2 (HW2000)
        DRONE_4_CONTROLLER_P3(0x00042003),   // Drone_4_Controller_P3 (HW2000B)
        DRONE_4_CONTROLLER_P4(0x00042004),   // Drone_4_Controller_P4 (HW2000B, Encrypt)

        DRONE_4_LINK_P0(0x00043000),   // Drone_4_Link_P0

        DRONE_4_TESTER_P4(0x0004A004),   // Drone_4_Tester_P4 (obsolete)
        DRONE_4_TESTER_P6(0x0004A006),   // Drone_4_Tester_P6
        DRONE_4_TESTER_P7(0x0004A007),   // Drone_4_Tester_P7

        DRONE_4_MONITOR_P4(0x0004A104),   // Drone_4_Monitor_P4 (obsolete)

        // Drone 7 Series
        DRONE_7_DRONE_P1(0x00071001),   // Drone_7_Drone_P1
        DRONE_7_DRONE_P2(0x00071002),   // Drone_7_Drone_P2 / Coding Car

        DRONE_7_BLECLIENT_P0(0x00073200),   // Drone_7_BleClient_P0 / Coding Car Link
        DRONE_7_BLECLIENT_P5(0x00073205),   // Drone_7_BleClient_P5 / Coding Car Tester BLE

        DRONE_7_BLESERVER_P2(0x00073302),   // Drone_7_BleServer_P2 / Coding Car Ble Module

        DRONE_7_TESTER_P4(0x0003A004),   // Drone_7_Tester_P4 (obsolete)  <- Note: Value collision with Drone 3 Tester
        DRONE_7_TESTER_P5(0x0003A005),   // Drone_7_Tester_P5 (obsolete)  <- Note: Value collision, possible error
        DRONE_7_TESTER_P6(0x0003A006),   // Drone_7_Tester_P6  <- Note: Value collision with Drone 3 Tester

        DRONE_7_MONITOR_P4(0x0003A104),   // Drone_7_Monitor_P4 (obsolete) <- Note: Value collision
        DRONE_7_MONITOR_P5(0x0003A105),   // Drone_7_Monitor_P5

        // Drone 8 Series
        DRONE_8_DRONE_P0(0x00081000),   // Drone_8_Drone_P0 (obsolete)
        DRONE_8_DRONE_P1(0x00081001),   // Drone_8_Drone_P1 / Coding Drone

        DRONE_8_TESTER_P4(0x0008A004),   // Drone_8_Tester_P4 (obsolete)
        DRONE_8_TESTER_P6(0x0008A006),   // Drone_8_Tester_P6  <- Note: Value collision with Drone 3 and 7

        DRONE_8_MONITOR_P6(0x0008A106),   // Drone_8_Monitor_P6

        // Drone 9 Series
        DRONE_9_DRONE_P0(0x00091000),   // Drone_9_Drone_P0
        DRONE_9_DRONE_P1(0x00091001),   // Drone_9_Drone_P1
        DRONE_9_DRONE_P2(0x00091002),   // Drone_9_Drone_P2

        DRONE_9_TESTER_P6(0x0009A006),   // Drone_9_Tester_P6  <- Note: Value collision with Drone 3, 7 and 8

        // Drone 12 Series
        DRONE_12_CONTROLLER_P1(0x000C2001), // Drone_12_Controller_P1 (CDE JROTC ed.)
        DRONE_12_DRONE_P1(0x000C1002);  // Drone_12_Drone_P1 (CDE JROTC ed.)

        private final int value;

        /**
         * Constructor for the ModelNumber enum.
         *
         * @param value The integer value representing the model number.
         */
        ModelNumber(int value) {
            this.value = value;
        }

        /**
         * Gets the integer value of the model number.
         *
         * @return The integer value.
         */
        public int getValue() {
            return value;
        }

        // Static map for reverse lookup
        private static final Map<Integer, ModelNumber> VALUE_MAP = new HashMap<>();

        // Static initializer to populate the map
        static {
            for (ModelNumber model : ModelNumber.values()) {
                VALUE_MAP.put(model.getValue(), model);
            }
        }

        /**
         * Returns the ModelNumber enum constant associated with the given integer value.
         *
         * @param value The integer value to look up.
         * @return The corresponding ModelNumber enum constant, or null if not found.
         * @throws IllegalArgumentException if the value is not a valid model number.
         */
        public static ModelNumber fromValue(int value) {
            ModelNumber modelNumber = VALUE_MAP.get(value);
            if (modelNumber == null) {
                throw new IllegalArgumentException("Invalid ModelNumber value: " + value);
            }
            return modelNumber;
        }
    }

    public enum DeviceType
    {

        NONE(0x00),
        DRONE(0x10),         // Drone (Server)
        CONTROLLER(0x20),    // Controller (Client)
        LINK(0x30),          // Link Module (Client)
        LINK_SERVER(0x31),   // Link Module (Server, temporarily changes communication type only when the link module operates as a server)
        BLE_CLIENT(0x32),    // BLE Client
        BLE_SERVER(0x33),    // BLE Server
        RANGE(0x40),         // Range Sensor Module
        BASE(0x70),          // Base
        BY_SCRATCH(0x80),    // By Scratch
        SCRATCH(0x81),       // Scratch
        ENTRY(0x82),         // Naver Entry
        TESTER(0xA0),        // Tester
        MONITOR(0xA1),       // Monitor
        UPDATER(0xA2),       // Firmware Update Tool
        ENCRYPTER(0xA3),     // Encryption Tool
        WHISPERING(0xFE),    // Transmit only to immediately adjacent devices (received device processes as if sent to itself and does not forward to other devices)
        BROADCASTING(0xFF);

        private final int value;

        DeviceType(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }


    /**
     * Represents the system modes.
     */
    public enum ModeSystem {

        NONE((byte) 0x00),                // No system mode.
        BOOT((byte) 0x10),                // System is booting.
        START((byte) 0x11),               // System is starting.
        RUNNING((byte) 0x12),             // System is running.
        READY_TO_RESET((byte) 0x13),       // System is ready to reset.
        ERROR((byte) 0xA0),                 // System is in an error state.
        END_OF_TYPE((byte) 0x06);           // Marks the end of the enum type.

        private final byte value;       // The integer value associated with each mode.

        /**
         * Constructor for the ModeSystem enum.
         *
         * @param value The integer value representing the system mode.
         */
        ModeSystem(byte value) {
            this.value = value;
        }

        /**
         * Gets the integer value of the system mode.
         *
         * @return The integer value.
         */
        public byte getValue() {
            return value;
        }

        // Static map for reverse lookup
        private static final Map<Byte, ModeSystem> VALUE_MAP = new HashMap<>();

        // Static initializer to populate the map
        static {
            for (ModeSystem mode : ModeSystem.values()) {
                VALUE_MAP.put(mode.getValue(), mode);
            }
        }

        /**
         * Returns the ModeSystem enum constant associated with the given integer value.
         *
         * @param value The integer value to look up.
         * @return The corresponding ModeSystem enum constant, or null if not found.
         */
        public static ModeSystem fromValue(byte value) {
            return VALUE_MAP.get(value);
        }
    }

    /**
     * Represents the flight control modes.
     */
    public enum ModeControlFlight {

        NONE((byte) 0x00),
        ATTITUDE((byte) 0x10),    // X,Y are input as angles (deg), Z,Yaw are input as speed (m/s)
        POSITION((byte) 0x11),    // X,Y,Z,Yaw are input as speed (m/s)
        MANUAL((byte) 0x12),      // Manually control altitude
        RATE((byte) 0x13),        // X,Y are input as angular velocity (deg/s), Z,Yaw are input as speed (m/s)
        FUNCTION((byte) 0x14),
        END_OF_TYPE((byte) 0x15);

        private final byte value;       // The integer value associated with each mode.

        /**
         * Constructor for the ModeControlFlight enum.
         *
         * @param value The integer value representing the control mode.
         */
        ModeControlFlight(byte value) {
            this.value = value;
        }

        /**
         * Gets the integer value of the control mode.
         *
         * @return The integer value.
         */
        public byte getValue() {
            return value;
        }

        // Static map for reverse lookup
        private static final Map<Byte, ModeControlFlight> VALUE_MAP = new HashMap<>();

        // Static initializer to populate the map
        static {
            for (ModeControlFlight mode : ModeControlFlight.values()) {
                VALUE_MAP.put(mode.getValue(), mode);
            }
        }

        /**
         * Returns the ModeControlFlight enum constant associated with the given integer value.
         *
         * @param value The integer value to look up.
         * @return The corresponding ModeControlFlight enum constant, or null if not found.
         */
        public static ModeControlFlight fromValue(byte value) {
            return VALUE_MAP.get(value);
        }
    }

    /**
     * Represents the flight modes.
     */
    public enum ModeFlight {

        NONE((byte) 0x00),
        READY((byte) 0x10),
        START((byte) 0x11),
        TAKE_OFF((byte) 0x12),
        FLIGHT((byte) 0x13),
        LANDING((byte) 0x14),
        FLIP((byte) 0x15),
        REVERSE((byte) 0x16),
        STOP((byte) 0x20),
        ACCIDENT((byte) 0x30),
        ERROR((byte) 0x31),
        TEST((byte) 0x40),
        END_OF_TYPE((byte) 0x41);

        private final byte value;       // The integer value associated with each mode.

        /**
         * Constructor for the ModeFlight enum.
         *
         * @param value The integer value representing the flight mode.
         */
        ModeFlight(byte value) {
            this.value = value;
        }

        /**
         * Gets the integer value of the flight mode.
         *
         * @return The integer value.
         */
        public byte getValue() {
            return value;
        }

        // Static map for reverse lookup
        private static final Map<Byte, ModeFlight> VALUE_MAP = new HashMap<>();

        // Static initializer to populate the map
        static {
            for (ModeFlight mode : ModeFlight.values()) {
                VALUE_MAP.put(mode.getValue(), mode);
            }
        }

        /**
         * Returns the ModeFlight enum constant associated with the given integer value.
         *
         * @param value The integer value to look up.
         * @return The corresponding ModeFlight enum constant, or null if not found.
         */
        public static ModeFlight fromValue(byte value) {
            return VALUE_MAP.get(value);
        }
    }

    /**
     * Represents the update modes for a system or device.
     */
    public enum ModeUpdate {

        NONE((byte) 0x00),                  // No update mode.
        READY((byte) 0x01),                 // Ready to begin the update process.
        UPDATING((byte) 0x02),              // Currently performing the update.
        COMPLETE((byte) 0x03),              // Update process completed successfully.
        FAILED((byte) 0x04),                  // Update process failed (e.g., CRC16 checksum mismatch).
        NOT_AVAILABLE((byte) 0x05),         // Update functionality is not available (e.g., in debug mode).
        RUN_APPLICATION((byte) 0x06),        // The application is currently running.
        NOT_REGISTERED((byte) 0x07),        // The device or system is not registered.
        END_OF_TYPE((byte) 0x08);             //Marks the end of the enum type.

        private final byte value;       // The integer value associated with each mode.

        /**
         * Constructor for the ModeUpdate enum.
         *
         * @param value The integer value representing the update mode.
         */
        ModeUpdate(byte value) {
            this.value = value;
        }

        /**
         * Gets the integer value of the update mode.
         *
         * @return The integer value.
         */
        public byte getValue() {
            return value;
        }

        // Static map for reverse lookup (optional, but often useful)
        private static final Map<Byte, ModeUpdate> VALUE_MAP = new HashMap<>();

        // Static initializer to populate the map
        static {
            for (ModeUpdate mode : ModeUpdate.values()) {
                VALUE_MAP.put(mode.getValue(), mode);
            }
        }

        /**
         * Returns the ModeUpdate enum constant associated with the given integer value.
         *
         * @param value The integer value to look up.
         * @return The corresponding ModeUpdate enum constant, or null if not found.
         */
        public static ModeUpdate fromValue(int value) {
            return VALUE_MAP.get(value);
        }
    }


    public enum ErrorFlagsForSensor
    {

        NONE(0x00000000),
        MOTION_NO_ANSWER(0x00000001),        // No response from Motion sensor
        MOTION_WRONG_VALUE(0x00000002),       // Incorrect value from Motion sensor
        MOTION_NOT_CALIBRATED(0x00000004),    // Gyro Bias calibration not completed
        MOTION_CALIBRATING(0x00000008),       // Gyro Bias calibration in progress
        PRESSURE_NO_ANSWER(0x00000010),      // No response from Pressure sensor
        PRESSURE_WRONG_VALUE(0x00000020),     // Incorrect value from Pressure sensor
        RANGE_GROUND_NO_ANSWER(0x00000100),  // No response from Ground distance sensor
        RANGE_GROUND_WRONG_VALUE(0x00000200), // Incorrect value from Ground distance sensor
        FLOW_NO_ANSWER(0x00001000),          // No response from Flow sensor
        FLOW_WRONG_VALUE(0x00002000),         // Incorrect value from Flow sensor
        FLOW_CANNOT_RECOGNIZE_GROUND_IMAGE(0x00004000); // Cannot recognize ground image

        private final int value;

        ErrorFlagsForSensor(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    public enum ErrorFlagsForState
    {

        NONE(0x00000000),
        NOT_REGISTERED(0x00000001),              // Device not registered
        FLASH_READ_LOCK_UNLOCKED(0x00000002),     // Flash memory read lock not engaged
        BOOTLOADER_WRITE_LOCK_UNLOCKED(0x00000004), // Bootloader write lock not engaged
        LOW_BATTERY(0x00000008),                 // Low battery
        TAKEOFF_FAILURE_CHECK_PROPELLER_AND_MOTOR(0x00000010), // Takeoff failure
        CHECK_PROPELLER_VIBRATION(0x00000020),    // Propeller vibration detected
        ATTITUDE_NOT_STABLE(0x00000040),         // Attitude is too tilted or inverted
        CANNOT_FLIP_LOW_BATTERY(0x00000100),      // Battery below 50%
        CANNOT_FLIP_TOO_HEAVY(0x00000200);        // Device is too heavy

        private final int value;

        ErrorFlagsForState(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    public enum Direction
    {

        NONE(0x00),
        LEFT(0x01),
        FRONT(0x02),
        RIGHT(0x03),
        REAR(0x04),
        TOP(0x05),
        BOTTOM(0x06),
        CENTER(0x07),
        END_OF_TYPE(0x08);

        private final int value;

        Direction(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    public enum Rotation
    {

        NONE(0x00),
        CLOCKWISE(0x01),
        COUNTER_CLOCKWISE(0x02),
        END_OF_TYPE(0x03);

        private final int value;

        Rotation(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    /**
     * Represents the sensor orientation modes.
     */
    public enum SensorOrientation {

        NONE((byte) 0x00),
        NORMAL((byte) 0x01),
        REVERSE_START((byte) 0x02),
        REVERSED((byte) 0x03),
        END_OF_TYPE((byte) 0x04);

        private final byte value;

        /**
         * Constructor for the SensorOrientation enum.
         *
         * @param value The byte value representing the sensor orientation mode.
         */
        SensorOrientation(byte value) {
            this.value = value;
        }

        /**
         * Gets the byte value of the sensor orientation mode.
         *
         * @return The byte value.
         */
        public byte getValue() {
            return value;
        }

        // Static map for reverse lookup
        private static final Map<Byte, SensorOrientation> VALUE_MAP = new HashMap<>();

        // Static initializer to populate the map
        static {
            for (SensorOrientation mode : SensorOrientation.values()) {
                VALUE_MAP.put(mode.getValue(), mode);
            }
        }

        /**
         * Returns the SensorOrientation enum constant associated with the given byte value.
         *
         * @param value The byte value to look up.
         * @return The corresponding SensorOrientation enum constant, or null if not found.
         */
        public static SensorOrientation fromValue(byte value) {
            return VALUE_MAP.get(value);
        }
    }

    /**
     * Represents the headless modes.
     */
    public enum Headless {

        NONE((byte) 0x00),
        HEADLESS_MODE((byte) 0x01), // Headless
        NORMAL_MODE((byte) 0x02),   // Normal
        END_OF_TYPE((byte) 0x03);

        private final byte value;

        /**
         * Constructor for the Headless enum.
         *
         * @param value The byte value representing the headless mode.
         */
        Headless(byte value) {
            this.value = value;
        }

        /**
         * Gets the byte value of the headless mode.
         *
         * @return The byte value.
         */
        public byte getValue() {
            return value;
        }

        // Static map for reverse lookup
        private static final Map<Byte, Headless> VALUE_MAP = new HashMap<>();

        // Static initializer to populate the map
        static {
            for (Headless mode : Headless.values()) {
                VALUE_MAP.put(mode.getValue(), mode);
            }
        }

        /**
         * Returns the Headless enum constant associated with the given byte value.
         *
         * @param value The byte value to look up.
         * @return The corresponding Headless enum constant, or null if not found.
         */
        public static Headless fromValue(byte value) {
            return VALUE_MAP.get(value);
        }
    }

    public enum TrimDirection
    {

        NONE(0x00),             // None
        ROLL_INCREASE(0x01),     // Increase Roll
        ROLL_DECREASE(0x02),     // Decrease Roll
        PITCH_INCREASE(0x03),    // Increase Pitch
        PITCH_DECREASE(0x04),    // Decrease Pitch
        YAW_INCREASE(0x05),      // Increase Yaw
        YAW_DECREASE(0x06),      // Decrease Yaw
        THROTTLE_INCREASE(0x07), // Increase Throttle
        THROTTLE_DECREASE(0x08), // Decrease Throttle
        RESET(0x09),            // Reset all trims
        END_OF_TYPE(0x0A);

        private final int value;

        TrimDirection(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    /**
     * Represents the movement modes.
     */
    public enum ModeMovement {

        NONE((byte) 0x00),
        READY((byte) 0x01),        // Ready
        HOVERING((byte) 0x02),     // Hovering
        MOVING((byte) 0x03),       // Moving
        RETURN_HOME((byte) 0x04),  // Return Home
        END_OF_TYPE((byte) 0x05);

        private final byte value;

        /**
         * Constructor for the ModeMovement enum.
         *
         * @param value The byte value representing the movement mode.
         */
        ModeMovement(byte value) {
            this.value = value;
        }

        /**
         * Gets the byte value of the movement mode.
         *
         * @return The byte value.
         */
        public byte getValue() {
            return value;
        }

        // Static map for reverse lookup
        private static final Map<Byte, ModeMovement> VALUE_MAP = new HashMap<>();

        // Static initializer to populate the map
        static {
            for (ModeMovement mode : ModeMovement.values()) {
                VALUE_MAP.put(mode.getValue(), mode);
            }
        }

        /**
         * Returns the ModeMovement enum constant associated with the given byte value.
         *
         * @param value The byte value to look up.
         * @return The corresponding ModeMovement enum constant, or null if not found.
         */
        public static ModeMovement fromValue(byte value) {
            return VALUE_MAP.get(value);
        }
    }

    public enum CardColorIndex
    {

        UNKNOWN(0x00),
        WHITE(0x01),
        RED(0x02),
        YELLOW(0x03),
        GREEN(0x04),
        CYAN(0x05),
        BLUE(0x06),
        MAGENTA(0x07),
        BLACK(0x08),
        END_OF_TYPE(0x09);

        private final int value;

        CardColorIndex(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    public enum Card
    {

        NONE(0x00),
        WHITE_WHITE(0x11),
        WHITE_RED(0x12),
        WHITE_YELLOW(0x13),
        WHITE_GREEN(0x14),
        WHITE_CYAN(0x15),
        WHITE_BLUE(0x16),
        WHITE_MAGENTA(0x17),
        WHITE_BLACK(0x18),
        RED_WHITE(0x21),
        RED_RED(0x22),
        RED_YELLOW(0x23),
        RED_GREEN(0x24),
        RED_CYAN(0x25),
        RED_BLUE(0x26),
        RED_MAGENTA(0x27),
        RED_BLACK(0x28),
        YELLOW_WHITE(0x31),
        YELLOW_RED(0x32),
        YELLOW_YELLOW(0x33),
        YELLOW_GREEN(0x34),
        YELLOW_CYAN(0x35),
        YELLOW_BLUE(0x36),
        YELLOW_MAGENTA(0x37),
        YELLOW_BLACK(0x38),
        GREEN_WHITE(0x41),
        GREEN_RED(0x42),
        GREEN_YELLOW(0x43),
        GREEN_GREEN(0x44),
        GREEN_CYAN(0x45),
        GREEN_BLUE(0x46),
        GREEN_MAGENTA(0x47),
        GREEN_BLACK(0x48),
        CYAN_WHITE(0x51),
        CYAN_RED(0x52),
        CYAN_YELLOW(0x53),
        CYAN_GREEN(0x54),
        CYAN_CYAN(0x55),
        CYAN_BLUE(0x56),
        CYAN_MAGENTA(0x57),
        CYAN_BLACK(0x58),
        BLUE_WHITE(0x61),
        BLUE_RED(0x62),
        BLUE_YELLOW(0x63),
        BLUE_GREEN(0x64),
        BLUE_CYAN(0x65),
        BLUE_BLUE(0x66),
        BLUE_MAGENTA(0x67),
        BLUE_BLACK(0x68),
        MAGENTA_WHITE(0x71),
        MAGENTA_RED(0x72),
        MAGENTA_YELLOW(0x73),
        MAGENTA_GREEN(0x74),
        MAGENTA_CYAN(0x75),
        MAGENTA_BLUE(0x76),
        MAGENTA_MAGENTA(0x77),
        MAGENTA_BLACK(0x78),
        BLACK_WHITE(0x81),
        BLACK_RED(0x82),
        BLACK_YELLOW(0x83),
        BLACK_GREEN(0x84),
        BLACK_CYAN(0x85),
        BLACK_BLUE(0x86),
        BLACK_MAGENTA(0x87),
        BLACK_BLACK(0x88);

        private final int value;

        Card(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }


    /**
     * Represents button event types.
     */
    public enum ButtonEvent {

        None_((byte) 0x00),            // 없음
        Down((byte) 0x01),             // 누르기 시작
        Press((byte) 0x02),            // 누르는 중
        Up((byte) 0x03),               // 뗌
        EndContinuePress((byte) 0x04); // 연속 입력 종료

        private final byte value;

        /**
         * Constructor for the ButtonEvent enum.
         *
         * @param value The byte value representing the button event type.
         */
        ButtonEvent(byte value) {
            this.value = value;
        }

        /**
         * Gets the byte value of the button event type.
         *
         * @return The byte value.
         */
        public byte getValue() {
            return value;
        }

        // Static map for reverse lookup
        private static final Map<Byte, ButtonEvent> VALUE_MAP = new HashMap<>();

        // Static initializer to populate the map
        static {
            for (ButtonEvent event : ButtonEvent.values()) {
                VALUE_MAP.put(event.getValue(), event);
            }
        }

        /**
         * Returns the ButtonEvent enum constant associated with the given byte value.
         *
         * @param value The byte value to look up.
         * @return The corresponding ButtonEvent enum constant, or null if not found.
         */
        public static ButtonEvent fromValue(byte value) {
            return VALUE_MAP.get(value);
        }
    }

    /**
     * Represents joystick directions.
     */
    public enum JoystickDirection {
        None_((byte) 0x00),       // 정의하지 않은 영역(무시함) - Undefined area (ignored)

        VT((byte) 0x10),          // 위(세로) - Top (vertical)
        VM((byte) 0x20),          // 중앙(세로) - Center (vertical)
        VB((byte) 0x40),          // 아래(세로) - Bottom (vertical)

        HL((byte) 0x01),          // 왼쪽(가로) - Left (horizontal)
        HM((byte) 0x02),          // 중앙(가로) - Center (horizontal)
        HR((byte) 0x04),          // 오른쪽(가로) - Right (horizontal)

        TL((byte) 0x11),
        TM((byte) 0x12),
        TR((byte) 0x14),
        ML((byte) 0x21),
        CN((byte) 0x22),
        MR((byte) 0x24),
        BL((byte) 0x41),
        BM((byte) 0x42),
        BR((byte) 0x44);

        private final byte value;

        /**
         * Constructor for the JoystickDirection enum.
         *
         * @param value The byte value representing the joystick direction.
         */
        JoystickDirection(byte value) {
            this.value = value;
        }

        /**
         * Gets the byte value of the joystick direction.
         *
         * @return The byte value.
         */
        public byte getValue() {
            return value;
        }

        // Static map for reverse lookup
        private static final Map<Byte, JoystickDirection> VALUE_MAP = new HashMap<>();

        // Static initializer to populate the map
        static {
            for (JoystickDirection direction : JoystickDirection.values()) {
                VALUE_MAP.put(direction.getValue(), direction);
            }
        }

        /**
         * Returns the JoystickDirection enum constant associated with the given byte value.
         *
         * @param value The byte value to look up.
         * @return The corresponding JoystickDirection enum constant, or null if not found.
         */
        public static JoystickDirection fromValue(byte value) {
            return VALUE_MAP.get(value);
        }
    }

    /**
     * Represents joystick events.
     */
    public enum JoystickEvent {
        None_((byte) 0x00),    // 이벤트 없음 - No event
        In((byte) 0x01),      // 특정 영역에 진입 - Enter specific area
        Stay((byte) 0x02),    // 특정 영역에서 상태 유지 - Stay in specific area
        Out((byte) 0x03),     // 특정 영역에서 벗어남 - Exit specific area
        EndOfType((byte) 0x04);

        private final byte value;

        /**
         * Constructor for the JoystickEvent enum.
         *
         * @param value The byte value representing the joystick event.
         */
        JoystickEvent(byte value) {
            this.value = value;
        }

        /**
         * Gets the byte value of the joystick event.
         *
         * @return The byte value.
         */
        public byte getValue() {
            return value;
        }

        // Static map for reverse lookup
        private static final Map<Byte, JoystickEvent> VALUE_MAP = new HashMap<>();

        // Static initializer to populate the map
        static {
            for (JoystickEvent event : JoystickEvent.values()) {
                VALUE_MAP.put(event.getValue(), event);
            }
        }

        /**
         * Returns the JoystickEvent enum constant associated with the given byte value.
         *
         * @param value The byte value to look up.
         * @return The corresponding JoystickEvent enum constant, or null if not found.
         */
        public static JoystickEvent fromValue(byte value) {
            return VALUE_MAP.get(value);
        }
    }
}
