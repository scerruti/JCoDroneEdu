package com.otabi.jcodroneedu.protocol.linkmanager;

import com.otabi.jcodroneedu.protocol.DataType;
import com.otabi.jcodroneedu.protocol.InvalidDataSizeException;
import com.otabi.jcodroneedu.protocol.Serializable;

import java.nio.ByteBuffer;

/**
 * Represents a request message sent to the drone to ask for a specific
 * type of data. The payload is a single byte representing the DataType
 * being requested.
 */
public class Request implements Serializable {
    public static final byte REQUEST_SIZE = 1;

    private DataType requestedDataType;

    /**
     * Default constructor for the factory pattern.
     */
    public Request() {
    }

    /**
     * Creates a new Request for a specific DataType.
     * @param requestedDataType The type of data being requested.
     */
    public Request(DataType requestedDataType) {
        this.requestedDataType = requestedDataType;
    }

    @Override
    public byte getSize() {
        return REQUEST_SIZE;
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.put(requestedDataType.value());
    }

    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException {
        if (buffer.remaining() < REQUEST_SIZE) {
            // Corrected to pass both the expected size and the actual size.
            throw new InvalidDataSizeException(REQUEST_SIZE, buffer.remaining());
        }
        this.requestedDataType = DataType.fromByte(buffer.get());
    }

    public DataType getRequestedDataType() {
        return requestedDataType;
    }
}
