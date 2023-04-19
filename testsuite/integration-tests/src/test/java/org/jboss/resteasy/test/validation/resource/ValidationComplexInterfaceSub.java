package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.Pattern;
import javax.ws.rs.Path;

@Path("/")
@ValidationComplexClassInheritanceSubConstraint("[a-c]+")
public class ValidationComplexInterfaceSub extends ValidationComplexInterfaceSuper {
    public static String u;

    @Pattern(regexp = "[a-c]+")
    public String postOverride(String s) {
        return s;
    }
}
