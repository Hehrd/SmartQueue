package com.alexander.error;

import com.alexander.error.exception.CancelCommandException;
import org.springframework.shell.command.annotation.ExceptionResolver;
import org.springframework.stereotype.Component;

@Component
public class GlobalExceptionHandler {

    @ExceptionResolver(CancelCommandException.class)
    public String handleCancelCommandException(CancelCommandException ex) {
        return String.format("Cancelling command %s", ex.getCommandName());
    }

}
