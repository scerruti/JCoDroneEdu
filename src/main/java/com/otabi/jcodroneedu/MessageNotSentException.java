package com.otabi.jcodroneedu;

@SuppressWarnings("WeakerAccess")
public class MessageNotSentException extends Exception {

    public MessageNotSentException(String message) {
        super(message);
    }

    /**
     * New constructor that accepts a "cause".
     * This allows you to wrap another exception, like InterruptedException.
     * @param message A descriptive message for this exception.
     * @param cause The original exception that caused this one.
     */
    public MessageNotSentException(String message, Throwable cause) {
        super(message, cause);
    }
}