package com.alexander.error.exception;

public class CancelCommandException extends CommandException {
    public CancelCommandException(String commandName) {
        super(commandName);
    }
}
