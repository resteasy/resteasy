package org.resteasy.test.ejb;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public interface SimpleResource
{
   @GET
   @Path("basic")
   @Produces("text/plain")
   String getBasic();

   @PUT
   @Path("basic")
   @Consumes("text/plain")
   void putBasic(String body);

   @GET
   @Path("queryParam")
   @Produces("text/plain")
   String getQueryParam(@QueryParam("param")String param);

   @GET
   @Path("matrixParam")
   @Produces("text/plain")
   String getMatrixParam(@MatrixParam("param")String param);

   @GET
   @Path("uriParam/{param}")
   @Produces("text/plain")
   int getUriParam(@PathParam("param")int param);
}
