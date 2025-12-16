// File: src/main/java/com/quizapp/config/WebConfig.java
package com.quizapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private StringToListConverter stringToListConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToListConverter);
    }
}