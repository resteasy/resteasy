package org.jboss.resteasy.resteasy1043;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.resteasy1043.HTTPCLIENT1340Test.RequestExecutor;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 5, 2013
 */
@Path("/")
public class TestResource
{
   @GET
   @Path("test")
   public Response test() throws Exception
   {
      String result = new RequestExecutor().executeRequest(RequestExecutor.url);
      return Response.ok(result).build();
   }
}