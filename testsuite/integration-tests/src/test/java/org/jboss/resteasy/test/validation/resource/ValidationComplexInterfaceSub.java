package org.jboss.resteasy.test.validation.resource;

import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.Path;

@Path("/")
@ValidationComplexClassInheritanceSubConstraint("[a-c]+")
public class ValidationComplexInterfaceSub extends ValidationComplexInterfaceSuper {
   public static String u;

   @Pattern(regexp = "[a-c]+")
   public String postOverride(String s) {
      return s;
   }
}
