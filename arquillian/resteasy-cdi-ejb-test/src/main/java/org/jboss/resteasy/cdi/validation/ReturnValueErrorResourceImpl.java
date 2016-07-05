package org.jboss.resteasy.cdi.validation;

import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.validation.hibernate.ValidateRequest;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 25, 2012
 */
@Path("return")
@Stateful
@ValidateRequest
@Interceptors({TestInterceptor.class})
public class ReturnValueErrorResourceImpl implements ReturnValueErrorResource
{
   @Inject
   private Logger log;

   @GET
   @Path("test")
   @Produces(MediaType.TEXT_PLAIN)
   public int test()
   {
      log.info("entering ReturnValueErrorResourceImpl.test()");
      return 13;
   }
}
