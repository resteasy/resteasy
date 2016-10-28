package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;

public class ValidationComplexArrayOfStrings {
    @Valid
    ValidationComplexOneString[] strings;

    public ValidationComplexArrayOfStrings(final String s) {
        strings = new ValidationComplexOneString[]{new ValidationComplexOneString(s)};
    }
}
