package org.jboss.resteasy.cdi.asynch;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 22, 2012
 */
@Stateless
@Path("/")
public class AsynchronousResource
{
   @Inject private Logger log;
   @Inject private AsynchronousStatelessLocal stateless;
   
   @GET
   @Path("asynch/simple")
   public Response asynchSimple() throws ExecutionException, InterruptedException
   {
      log.info("entering asynch()");
      Future<Boolean> asyncResult = stateless.asynch();
      return asyncResult.get() ? Response.ok().build() : Response.serverError().build();
   }
   
   @GET
   @Path("asynch/asynch")
   public void asynchAsynch(@Suspended final AsyncResponse asyncResponse) throws ExecutionException, InterruptedException
   {
      log.info("entering asynchAsynch()");
      Future<Boolean> asyncResult = stateless.asynch();
      Response response = asyncResult.get() ? Response.ok().build() : Response.serverError().build();
      Thread.sleep(2000);
      asyncResponse.resume(response);
   }
   
   @Asynchronous
   @GET
   @Path("asynch/ejb")
   public void asynchEJB(@Suspended final AsyncResponse asyncResponse) throws ExecutionException, InterruptedException
   {
      log.info("entering asynchEJB()");
      Thread.sleep(2000);
      asyncResponse.resume(Response.ok().build());
   }
}

