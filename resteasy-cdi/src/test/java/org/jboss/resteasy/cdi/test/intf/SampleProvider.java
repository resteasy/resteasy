package org.jboss.resteasy.cdi.test.intf;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SampleProvider implements ExceptionMapper<NullPointerException>
{
   public Response toResponse(NullPointerException exception)
   {
      return null;
   }
}
