package com.otabi.jcodroneedu;

import java.security.InvalidParameterException;

public class InvalidMessageException extends InvalidParameterException {
    public InvalidMessageException(String message) {
        super(message);
    }
}
