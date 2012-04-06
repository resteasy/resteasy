package org.jboss.resteasy.test.smoke;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.junit.Assert;
import java.net.URI;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Context;
import javax.ws.rs.POST;



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
   public String getBasic(@Context UriInfo uriInfo) throws Exception
   {
      URI uri = uriInfo.getBaseUriBuilder().path(SimpleResource.class, "getBasic").build();
      Assert.assertEquals(uri.getPath(), "/resteasy/rest/basic");
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
   public String getQueryParam(@QueryParam("param")String param)
   {
      return param;
   }

   @GET
   @Path("matrixParam")
   @Produces("text/plain")
   public String getMatrixParam(@MatrixParam("param")String param)
   {
      return param;
   }

   @GET
   @Path("uriParam/{param}")
   @Produces("text/plain")
   public int getUriParam(@PathParam("param")int param)
   {
      return param;
   }


   @POST
   @Path("formtestit")
   @Produces("text/plain")
   public String postForm(@FormParam("value") String value, @Context HttpHeaders headers)
   {
      System.out.println(headers.getRequestHeaders().getFirst("content-type"));
      System.out.println("HERE!!!");
      if (value == null) throw new RuntimeException("VALUE WAS NULL");
      System.out.println(value);
      return value;
   }
}