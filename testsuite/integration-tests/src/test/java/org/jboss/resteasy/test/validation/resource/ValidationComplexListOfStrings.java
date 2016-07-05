package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class ValidationComplexListOfStrings {
    @Valid
    List<ValidationComplexOneString> strings;

    public ValidationComplexListOfStrings(final String s) {
        strings = new ArrayList<ValidationComplexOneString>();
        strings.add(new ValidationComplexOneString(s));
    }
}
