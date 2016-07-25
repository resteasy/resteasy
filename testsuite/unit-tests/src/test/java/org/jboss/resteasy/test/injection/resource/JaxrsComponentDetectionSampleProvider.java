package org.jboss.resteasy.test.injection.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class JaxrsComponentDetectionSampleProvider implements ExceptionMapper<NullPointerException> {
    public Response toResponse(NullPointerException exception) {
        return null;
    }
}
