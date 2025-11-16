package com.otabi.jcodroneedu.protocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Defines the contract for all protocol message objects.
 * Any object that can be sent or received must implement this interface.
 */
public interface Serializable {

    /**
     * Gets the size of the object's payload when packed.
     * @return The size of the payload in bytes.
     */
    byte getSize();

    /**
     * Packs the object's fields into a ByteBuffer for transmission.
     * @param buffer The ByteBuffer to pack data into.
     */
    void pack(ByteBuffer buffer);

    /**
     * Unpacks data from a ByteBuffer to populate the object's fields.
     * @param buffer The ByteBuffer to unpack data from.
     * @throws InvalidDataSizeException if the buffer has insufficient data.
     */
    void unpack(ByteBuffer buffer) throws InvalidDataSizeException;

    /**
     * A default helper method that provides a standard entry point for parsing.
     * This allows the Receiver to be completely decoupled from the unpack implementation.
     * @param buffer The ByteBuffer containing the payload to parse.
     * @throws InvalidDataSizeException if the buffer has insufficient data.
     */
    default void parse(ByteBuffer buffer) throws InvalidDataSizeException {
        unpack(buffer);
    }

    default byte[] toArray()
    {
        // Convert signed byte to unsigned int for allocate()
        // Since getSize() returns a signed byte, we need to mask to get the unsigned value
        int size = getSize() & 0xFF;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.order(ByteOrder.LITTLE_ENDIAN);  // Set byte order to match drone protocol
        pack(buffer);
        return buffer.array();
    }
}
