package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Size;

public class ValidationComplexA {
    @Size(min = 4)
    String s1;
    @Size(min = 5)
    String s2;

    public ValidationComplexA(final String s1, final String s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    public void setS2(String s) {
        this.s2 = s;
    }

    public String getS2() {
        return s2;
    }
}
