package org.jboss.resteasy.cdi.extension.scope;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.cdi.util.Utilities;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 7, 2012
 */
@Path("/extension")
@RequestScoped
public class ExtensionResource
{  
   private static int lastSecret2;
   private static int lastSecret3;
   
   @Inject private Logger log;
   @Inject private Utilities utilities;
   @Inject private ObsolescentAfterTwoUses oo2;
   @Inject private ObsolescentAfterThreeUses oo3;
   
   @POST
   @Path("setup")
   public Response setup()
   {
      log.info("ObsolescentObject scope: " + utilities.testScope(ObsolescentAfterTwoUses.class, PlannedObsolescenceScope.class));
      if (utilities.testScope(ObsolescentAfterTwoUses.class, PlannedObsolescenceScope.class))
      {
         lastSecret2 = oo2.getSecret();
         lastSecret3 = oo3.getSecret();
         log.info("current secret2: " + lastSecret2);
         log.info("current secret3: " + lastSecret3);
         return Response.ok().build();
      }
      else
      {
         return Response.serverError().build();
      }
   }
   
   @POST
   @Path("test1")
   public Response test1()
   {
      int currentSecret2 = oo2.getSecret();
      int currentSecret3 = oo3.getSecret();
      log.info("last secret2:    " + lastSecret2);
      log.info("last secret3:    " + lastSecret3);
      log.info("current secret2: " + currentSecret2);
      log.info("current secret3: " + currentSecret3);
      if (currentSecret2 == lastSecret2 && currentSecret3 == lastSecret3)
      {
         lastSecret2 = currentSecret2;
         lastSecret3 = currentSecret3;
         return Response.ok().build();
      }
      else
      {
         return Response.serverError().build();
      }
   }
   
   @POST
   @Path("test2")
   public Response test2()
   {
      int currentSecret2 = oo2.getSecret();
      int currentSecret3 = oo3.getSecret();
      log.info("last secret2:    " + lastSecret2);
      log.info("last secret3:    " + lastSecret3);
      log.info("current secret2: " + currentSecret2);
      log.info("current secret3: " + currentSecret3);
      if (currentSecret2 != lastSecret2 && currentSecret3 == lastSecret3)
      {
         lastSecret3 = currentSecret3;
         return Response.ok().build();
      }
      else
      {
         return Response.serverError().build();
      }
   }
   
   @POST
   @Path("test3")
   public Response test3()
   {
      int currentSecret3 = oo3.getSecret();
      log.info("last secret3:    " + lastSecret3);
      log.info("current secret3: " + currentSecret3);
      if (currentSecret3 != lastSecret3)
      {
         return Response.ok().build();
      }
      else
      {
         return Response.serverError().build();
      }
   }
}
