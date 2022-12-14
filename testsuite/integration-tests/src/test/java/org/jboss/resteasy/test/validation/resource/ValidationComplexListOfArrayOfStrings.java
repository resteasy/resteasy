package org.jboss.resteasy.test.validation.resource;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

public class ValidationComplexListOfArrayOfStrings {
    @Valid
    List<ValidationComplexArrayOfStrings> list;

    public ValidationComplexListOfArrayOfStrings(final String s) {
        list = new ArrayList<>();
        list.add(new ValidationComplexArrayOfStrings(s));
    }
}
