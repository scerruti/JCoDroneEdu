package com.otabi.jcodroneedu.protocol;

import com.otabi.jcodroneedu.protocol.cardreader.*;
import com.otabi.jcodroneedu.protocol.control.Quad8;
import com.otabi.jcodroneedu.protocol.controllerinput.Button;
import com.otabi.jcodroneedu.protocol.controllerinput.Joystick;
import com.otabi.jcodroneedu.protocol.dronestatus.*;
import com.otabi.jcodroneedu.protocol.information.InformationAssembledForController;
import com.otabi.jcodroneedu.protocol.information.InformationAssembledForEntry;
import com.otabi.jcodroneedu.protocol.lightcontroller.LightDefault;
import com.otabi.jcodroneedu.protocol.lightcontroller.LightEvent;
import com.otabi.jcodroneedu.protocol.lightcontroller.LightManual;
import com.otabi.jcodroneedu.protocol.lightcontroller.LightMode;
import com.otabi.jcodroneedu.protocol.linkmanager.Error;
import com.otabi.jcodroneedu.protocol.linkmanager.*;
import com.otabi.jcodroneedu.protocol.settings.Bias;
import com.otabi.jcodroneedu.protocol.settings.Count;
import com.otabi.jcodroneedu.protocol.settings.Trim;
import com.otabi.jcodroneedu.protocol.settings.Weight;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public enum DataType {

    // --- Enum Constants with their Factories ---

    None_((byte) 0x00, null, null),

    // --- Link Manager (Data received from the drone) ---
    Ping((byte) 0x01, Ping.class, Ping::new),
    Ack((byte) 0x02, Ack.class, Ack::new),
    Error((byte) 0x03, Error.class, Error::new),
    Message((byte) 0x05, Message.class, Message::new),
    Address((byte) 0x06, Address.class, Address::new),
    Information((byte) 0x07, Information.class, Information::new),
    UpdateLocation((byte) 0x09, UpdateLocation.class, UpdateLocation::new),
    SystemInformation((byte) 0x0C, SystemInformation.class, SystemInformation::new),
    Registration((byte) 0x0D, Registration.class, Registration::new),
    Pairing((byte) 0x12, Pairing.class, Pairing::new),
    Rssi((byte) 0x13, Rssi.class, Rssi::new),

    // --- Commands (Data sent from the base - no factory needed for parsing) ---
    Request((byte) 0x04, Request.class, null),
    Control((byte) 0x10, Quad8.class, null),
    Command((byte) 0x11, Command.class, null),
    LightManual((byte) 0x20, LightManual.class, null),
    LightMode((byte) 0x21, LightMode.class, null),
    LightEvent((byte) 0x22, LightEvent.class, null),
    LightDefault((byte) 0x23, LightDefault.class, null),

    // --- Sensor RAW Data (Received) ---
    RawMotion((byte) 0x30, RawMotion.class, RawMotion::new),
    RawFlow((byte) 0x31, RawFlow.class, RawFlow::new),

    // --- Drone Status (Sensor - Received) ---
    State((byte) 0x40, State.class, State::new),
    Attitude((byte) 0x41, Attitude.class, Attitude::new),
    Position((byte) 0x42, Position.class, Position::new),
    Altitude((byte) 0x43, Altitude.class, Altitude::new),
    Motion((byte) 0x44, Motion.class, Motion::new),
    Range((byte) 0x45, Range.class, Range::new),
    Flow((byte) 0x46, Flow.class, Flow::new),

    // --- Settings (Received) ---
    Count((byte) 0x50, Count.class, null),
    Bias((byte) 0x51, Bias.class, null),
    Trim((byte) 0x52, Trim.class, null),
    Weight((byte) 0x53, Weight.class, null),

    // --- Controller Input (Received) ---
    Button((byte) 0x70, Button.class, Button::new),
    Joystick((byte) 0x71, Joystick.class, Joystick::new),

    // --- Buzzer Control (Sent) ---
    Buzzer((byte) 0x62, null, null),

    // --- Display Control (Sent) ---
    DisplayClear((byte) 0x80, null, null),
    DisplayInvert((byte) 0x81, null, null),
    DisplayDrawPoint((byte) 0x82, null, null),
    DisplayDrawLine((byte) 0x83, null, null),
    DisplayDrawRect((byte) 0x84, null, null),
    DisplayDrawCircle((byte) 0x85, null, null),
    DisplayDrawString((byte) 0x86, null, null),

    // --- Card Reader (Received) ---
    CardClassify((byte) 0x90, CardClassify.class, CardClassify::new),
    CardRange((byte) 0x91, CardRange.class, CardRange::new),
    CardRaw((byte) 0x92, CardRaw.class, CardRaw::new),
    CardColor((byte) 0x93, CardColor.class, CardColor::new),
    CardList((byte) 0x94, CardList.class, CardList::new),
    CardFunctionList((byte) 0x95, CardFunctionList.class, CardFunctionList::new),

    // --- Information Assembled (Received) ---
    InformationAssembledForController((byte) 0xA0, InformationAssembledForController.class, InformationAssembledForController::new),
    InformationAssembledForEntry((byte) 0xA1, InformationAssembledForEntry.class, InformationAssembledForEntry::new),

    EndOfType((byte) 0xDC, null, null);


    // --- Enum Implementation ---

    private final byte type;
    private final Class<? extends Serializable> messageClass;
    private final Supplier<? extends Serializable> factory;

    DataType(byte type, Class<? extends Serializable> messageClass, Supplier<? extends Serializable> factory) {
        this.type = type;
        this.messageClass = messageClass;
        this.factory = factory;
    }

    public byte value() {
        return type;
    }

    public Class<? extends Serializable> getMessageClass() {
        return messageClass;
    }

    /**
     * Creates an empty message object using the assigned factory.
     * @return A new, empty Serializable message object, or null if no factory is defined.
     */
    public Serializable createInstance() {
        if (factory != null) {
            return factory.get();
        }
        return null;
    }

    // --- Static Maps for Lookup ---

    private static final Map<Byte, DataType> BYTE_DATA_TYPE_MAP = new HashMap<>();
    private static final Map<Class<? extends Serializable>, DataType> CLASS_DATA_TYPE_MAP = new HashMap<>();

    static {
        for (DataType type : DataType.values()) {
            BYTE_DATA_TYPE_MAP.put(type.value(), type);
            if (type.getMessageClass() != null) {
                CLASS_DATA_TYPE_MAP.put(type.getMessageClass(), type);
            }
        }
    }

    public static DataType fromByte(byte b) {
        return BYTE_DATA_TYPE_MAP.get(b);
    }

    public static DataType fromClass(Class<? extends Serializable> c) {
        return CLASS_DATA_TYPE_MAP.get(c);
    }
}
