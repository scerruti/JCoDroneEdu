package com.otabi.jcodroneedu.protocol;

public class InvalidDataSizeException extends RuntimeException {
    public InvalidDataSizeException(int expected, int received) {
        super(
                String.format(
                        "Invalid message data size, expected: %d, received: %d",
                        expected,
                        received)
        );
    }
}
