package com.alexander.error.exception;

public class CommandException extends Exception {
    protected String commandName;

    public CommandException(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
