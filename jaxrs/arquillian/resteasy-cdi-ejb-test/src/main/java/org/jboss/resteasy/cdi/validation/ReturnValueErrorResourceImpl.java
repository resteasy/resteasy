package org.jboss.resteasy.cdi.validation;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.validation.hibernate.ValidateRequest;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 25, 2012
 */
@Path("return")
@Stateless
@ValidateRequest
public class ReturnValueErrorResourceImpl implements ReturnValueErrorResource
{
   @Inject
   private Logger log;

   @GET
   @Path("test")
   public int test()
   {
      log.info("entering ErroneousResourceImpl.test()");
      return 13;
   }
}
