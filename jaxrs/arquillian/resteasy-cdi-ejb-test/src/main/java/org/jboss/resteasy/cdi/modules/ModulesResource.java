package org.jboss.resteasy.cdi.modules;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 18, 2012
 */
@Stateless
@Path("modules")
public class ModulesResource implements ModulesResourceIntf
{  
   @Inject
   private Logger log; 
   
   @Inject
   @InjectableBinder
   private InjectableIntf injectable;

   @Override
   @GET
   @Path("test")
   public Response test()
   {
      log.info("entering TestResource.test()");
      return (injectable != null) ? Response.ok().build() : Response.serverError().build();
   }
}
