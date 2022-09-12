package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.Path;

@Path("/somewhere")
public class AnnotationInheritanceSomeOtherResource implements AnnotationInheritanceSomeOtherInterface {
   public AnnotationInheritanceSuperInt getSuperInt() {
      return new AnnotationInheritanceSuperIntAbstract() {
         @Override
         protected String getName() {
            return "Fred";
         }
      };
   }

   public AnnotationInheritanceNotAResource getFailure() {
      return new AnnotationInheritanceNotAResource() {
         @Override
         public String blah() {
            return "Nothing";
         }
      };
   }
}
