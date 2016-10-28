package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

public class ValidationComplexMapOfStrings {
    @Valid
    Map<String, ValidationComplexOneString> strings;

    public ValidationComplexMapOfStrings(final String s) {
        strings = new HashMap<>();
        strings.put(s, new ValidationComplexOneString(s));
    }
}
