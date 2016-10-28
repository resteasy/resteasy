package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

public class ValidationComplexMapOfListOfArrayOfStrings {
    @Valid
    Map<String, ValidationComplexListOfArrayOfStrings> map;

    public ValidationComplexMapOfListOfArrayOfStrings(final String s) {
        map = new HashMap<String, ValidationComplexListOfArrayOfStrings>();
        map.put(s, new ValidationComplexListOfArrayOfStrings(s));
    }
}
