package org.jboss.resteasy.tests;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class Resource
{
   @Path("map")
   @POST
   public MultivaluedMap<String, String> map(MultivaluedMap<String, String> map) {
      System.out.println("********** MAP SIZE: " + map.size());
      return map;
   }

   @Path("entity")
   @GET
   public Response entity() {
      return Response.ok().build();
   }
}
