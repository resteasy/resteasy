package org.jboss.resteasy.test.validation.resource;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.Valid;

public class ValidationComplexMapOfListOfArrayOfStrings {
    @Valid
    Map<String, ValidationComplexListOfArrayOfStrings> map;

    public ValidationComplexMapOfListOfArrayOfStrings(final String s) {
        map = new HashMap<String, ValidationComplexListOfArrayOfStrings>();
        map.put(s, new ValidationComplexListOfArrayOfStrings(s));
    }
}
