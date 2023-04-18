package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;

public class ValidationComplexOneString {
    @Size(min = 5)
    String s;

    public ValidationComplexOneString(final String s) {
        this.s = s;
    }

    public String getS() {
        return s;
    }

    public void setString(String s) {
        this.s = s;
    }
}
