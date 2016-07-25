package org.jboss.resteasy.test.validation.resource;

import javax.ws.rs.Path;

@Path("/")
@ValidationComplexClassInheritanceSuperConstraint(3)
public class ValidationComplexInterfaceSuper implements ValidationComplexInterface {
    public static String t;

    public String postInherit(String s) {
        return s;
    }

    public String postOverride(String s) {
        return s;
    }

    public String concat() {
        return t + t;
    }
}
