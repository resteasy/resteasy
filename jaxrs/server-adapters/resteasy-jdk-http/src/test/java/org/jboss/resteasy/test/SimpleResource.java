package org.jboss.resteasy.test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class SimpleResource
{
   @GET
   @Path("basic")
   @Produces("text/plain")
   public String getBasic()
   {
      System.out.println("getBasic()");
      return "basic";
   }

   @PUT
   @Path("basic")
   @Consumes("text/plain")
   public void putBasic(String body)
   {
      System.out.println(body);
   }

   @GET
   @Path("queryParam")
   @Produces("text/plain")
   public String getQueryParam(@QueryParam("param") String param)
   {
      System.out.println("query param: " + param);
      return param;
   }

   @GET
   @Path("matrixParam")
   @Produces("text/plain")
   public String getMatrixParam(@MatrixParam("param") String param)
   {
      return param;
   }

   @GET
   @Path("uriParam/{param}")
   @Produces("text/plain")
   public int getUriParam(@PathParam("param") int param)
   {
      return param;
   }

   @GET
   @Path("header")
   public Response getHeader()
   {
      return Response.ok().header("header", "headervalue").build();
   }
}
