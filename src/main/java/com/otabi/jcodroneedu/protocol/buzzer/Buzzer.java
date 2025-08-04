package com.otabi.jcodroneedu.protocol.buzzer;

import com.otabi.jcodroneedu.protocol.Serializable;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import java.nio.ByteBuffer;

/**
 * Buzzer protocol class for sending buzzer commands to drone or controller.
 * Matches the Python Buzzer class structure and behavior.
 */
public class Buzzer implements Serializable {
    private BuzzerMode mode;
    private int value;
    private int time;

    /**
     * Default constructor - creates a stopped buzzer
     */
    public Buzzer() {
        this.mode = BuzzerMode.STOP;
        this.value = Note.MUTE.getValue();
        this.time = 1;
    }

    /**
     * Constructor with parameters
     */
    public Buzzer(BuzzerMode mode, int value, int time) {
        this.mode = mode;
        this.value = value;
        this.time = time;
    }

    // Getters
    public BuzzerMode getMode() { return mode; }
    public int getValue() { return value; }
    public int getTime() { return time; }

    // Setters
    public void setMode(BuzzerMode mode) { this.mode = mode; }
    public void setValue(int value) { this.value = value; }
    public void setTime(int time) { this.time = time; }

    @Override
    public byte getSize() {
        return 5; // mode(1) + value(2) + time(2)
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.put((byte) mode.getValue());
        buffer.putShort((short) value);
        buffer.putShort((short) time);
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException {
        if (buffer.remaining() < getSize()) {
            throw new InvalidDataSizeException(getSize(), buffer.remaining());
        }
        
        int modeValue = buffer.get() & 0xFF;
        this.mode = BuzzerMode.fromValue(modeValue);
        this.value = buffer.getShort() & 0xFFFF;
        this.time = buffer.getShort() & 0xFFFF;
    }

    // Static factory methods for common operations
    public static Buzzer stop() {
        return new Buzzer(BuzzerMode.STOP, Note.MUTE.getValue(), 1);
    }

    public static Buzzer mute(int duration) {
        return new Buzzer(BuzzerMode.MUTE, Note.MUTE.getValue(), duration);
    }

    public static Buzzer note(Note note, int duration) {
        return new Buzzer(BuzzerMode.SCALE, note.getValue(), duration);
    }

    public static Buzzer frequency(int hz, int duration) {
        return new Buzzer(BuzzerMode.HZ, hz, duration);
    }

    @Override
    public String toString() {
        return String.format("Buzzer(mode=%s, value=%d, time=%d)", mode, value, time);
    }
}
