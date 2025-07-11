package com.otabi.jcodroneedu.protocol;

import java.nio.ByteBuffer;

public class Header implements Serializable
{
    private static final byte HEADER_SIZE = 4; // A clear constant name

    private DataType dataType;
    private byte length;
    private DeviceType from;
    private DeviceType to;

    public Header() {
    }

    public Header(DataType dataType, byte length, DeviceType from, DeviceType to) {
        this.dataType = dataType;
        this.length = length;
        this.from = from;
        this.to = to;
    }

    @Override
    public byte getSize() {
        return HEADER_SIZE;
    }

    @Override
    public void pack(ByteBuffer buffer)
    {
        buffer.put(dataType.value())
            .put(length)
            .put(from.getValue())
            .put(to.getValue());
    }



    @Override
    public void unpack(ByteBuffer buffer) throws InvalidDataSizeException
    {
        this.dataType = DataType.fromByte(buffer.get());
        this.length = buffer.get();
        this.from = DeviceType.fromByte(buffer.get());
        this.to = DeviceType.fromByte(buffer.get());
    }

    public DataType getDataType() {
        return dataType;
    }

    public byte getLength() {
        return length;
    }

    public DeviceType getFrom() {
        return from;
    }

    public DeviceType getTo() {
        return to;
    }

    public void setDataType(DataType dataType)
    {
        this.dataType = dataType;
    }

    public void setLength(byte length)
    {
        this.length = length;
    }

    public void setFrom(DeviceType from)
    {
        this.from = from;
    }

    public void setTo(DeviceType to)
    {
        this.to = to;
    }

}
