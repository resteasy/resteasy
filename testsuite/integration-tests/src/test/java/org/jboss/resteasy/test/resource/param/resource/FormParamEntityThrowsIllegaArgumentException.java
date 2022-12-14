package org.jboss.resteasy.test.resource.param.resource;

public class FormParamEntityThrowsIllegaArgumentException extends FormParamEntityPrototype {
    public static FormParamEntityThrowsIllegaArgumentException fromString(String arg) {
        throw new IllegalArgumentException("failed to parse");
    }
}
