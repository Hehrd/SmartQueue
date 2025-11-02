package com.alexander.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SecurityConfig {
    @Bean("jasyptStringEncryptor")
    @Primary
    public StringEncryptor jasyptEncryptor(@Value("${jasypt.encryptor.algorithm}") String algorithm,
                                           @Value("${jasypt.encryptor.password}") String password) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm(algorithm);
        encryptor.setIvGenerator(new RandomIvGenerator());
        encryptor.setPassword(password);
        return encryptor;
    }
}
