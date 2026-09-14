package com.visualartifact.backend.guardrail;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PiiDetector {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("\\b(?:\\+?\\d{1,3}[-.\\s]?)?(?:\\d{10}|\\d{3}[-.\\s]\\d{3}[-.\\s]\\d{4})\\b");

    private static final Pattern LONG_ID_PATTERN =
            Pattern.compile("\\b\\d{12,16}\\b");

    public boolean containsPii(String text) {
        if(text == null || text.isBlank()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(text).find() ||
                PHONE_PATTERN.matcher(text).find() ||
                LONG_ID_PATTERN.matcher(text).find();
    }
}