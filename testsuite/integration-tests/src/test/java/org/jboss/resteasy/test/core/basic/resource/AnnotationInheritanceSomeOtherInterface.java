package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.Path;

public interface AnnotationInheritanceSomeOtherInterface {
   @Path("superint")
   AnnotationInheritanceSuperInt getSuperInt();

   @Path("failure")
   AnnotationInheritanceNotAResource getFailure();
}
