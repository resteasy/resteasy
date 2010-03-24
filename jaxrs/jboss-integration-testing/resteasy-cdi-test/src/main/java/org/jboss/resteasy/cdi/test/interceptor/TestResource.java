package org.jboss.resteasy.cdi.test.interceptor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * This test verifies that a JAX-RS resource method invocation
 * can be intercepted by Interceptor bound using CDI interceptor
 * binding.
 * 
 * @author Jozef Hartinger
 *
 */

@Path("/interceptor")
@Produces("text/plain")
@TestInterceptorBinding
public class TestResource
{
   @GET
   public boolean getValue()
   {
      return false;
   }
}
