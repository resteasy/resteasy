package org.jboss.resteasy.test.smoke;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.QueryParam;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public interface SimpleClient
{
   @GET
   @Path("basic")
   @ProduceMime("text/plain")
   String getBasic();

   @PUT
   @Path("basic")
   @ConsumeMime("text/plain")
   void putBasic(String body);

   @GET
   @Path("queryParam")
   @ProduceMime("text/plain")
   String getQueryParam(@QueryParam("param")String param);

   @GET
   @Path("matrixParam")
   @ProduceMime("text/plain")
   String getMatrixParam(@MatrixParam("param")String param);

   @GET
   @Path("uriParam/{param}")
   @ProduceMime("text/plain")
   int getUriParam(@PathParam("param")int param);
}
