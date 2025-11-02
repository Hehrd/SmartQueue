package com.alexander.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.style.TemplateExecutor;

import java.io.IOException;
import java.util.Scanner;

@Configuration
public class CommandConfig {
    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }

    @Bean
    public ComponentFlow.Builder componentFlowBuilder(@Autowired Terminal terminal,
                                                      @Autowired ResourceLoader resourceLoader,
                                                      @Autowired TemplateExecutor templateExecutor) {
        ComponentFlow.Builder builder = ComponentFlow.builder();
        builder.terminal(terminal);
        builder.resourceLoader(resourceLoader);
        builder.templateExecutor(templateExecutor);
        return builder;
    }

    @Bean
    @Primary
    public Terminal terminal() throws IOException {
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .streams(System.in, System.out)
                .jna(true)
                .build();
        return terminal;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
