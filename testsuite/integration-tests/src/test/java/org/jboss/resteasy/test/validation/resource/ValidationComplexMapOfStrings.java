package org.jboss.resteasy.test.validation.resource;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.Valid;

public class ValidationComplexMapOfStrings {
    @Valid
    Map<String, ValidationComplexOneString> strings;

    public ValidationComplexMapOfStrings(final String s) {
        strings = new HashMap<>();
        strings.put(s, new ValidationComplexOneString(s));
    }
}
