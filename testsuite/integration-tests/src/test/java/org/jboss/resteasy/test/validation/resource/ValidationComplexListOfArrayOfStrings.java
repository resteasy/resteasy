package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class ValidationComplexListOfArrayOfStrings {
    @Valid
    List<ValidationComplexArrayOfStrings> list;

    public ValidationComplexListOfArrayOfStrings(final String s) {
        list = new ArrayList<>();
        list.add(new ValidationComplexArrayOfStrings(s));
    }
}
