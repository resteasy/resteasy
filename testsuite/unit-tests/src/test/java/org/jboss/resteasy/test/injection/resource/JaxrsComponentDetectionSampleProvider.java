package org.jboss.resteasy.test.injection.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class JaxrsComponentDetectionSampleProvider implements ExceptionMapper<NullPointerException> {
   public Response toResponse(NullPointerException exception) {
      return null;
   }
}
