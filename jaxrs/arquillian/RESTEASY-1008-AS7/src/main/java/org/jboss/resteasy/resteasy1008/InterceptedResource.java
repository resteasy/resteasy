package org.jboss.resteasy.resteasy1008;

import javax.interceptor.Interceptors;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.validation.hibernate.ValidateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 26, 2014
 */
@Path("")
@Interceptors(TestInterceptor.class)
@ValidateRequest
public class InterceptedResource
{
   private static final Logger log = LoggerFactory.getLogger(InterceptedResource.class);

   @GET
   @Path("intercept/{param}")
   @Produces(MediaType.TEXT_PLAIN)
   public int test(@Min(7) @PathParam("param") int param)
   {
      log.info("entering InterceptedResource.test()");
      return param;
   }
}