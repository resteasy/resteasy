package org.jboss.resteasy.tests.encoding.sample;

import junit.framework.Assert;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * @author Edgar Silva
 */
@Resource
@Path(value = "/sayhello")
public class Hello
{

   private static final String QUERY = "select p from VirtualMachineEntity p where guest.guestId = :id";

   @Context
   UriInfo info;

   @GET
   @Path("/en/{in}")
   @Produces("text/plain")
   public String echo(@PathParam(value = "in") String in)
   {

//		List<String> params =  info.getPathParameters(true).get("in");
//		System.out.println("DECODE" + params.get(0));
//		
//		params =  info.getPathParameters(false).get("in");
//		System.out.println("ENCODE" + params.get(0));

      Assert.assertEquals("something something", in);

      return in;
   }


   @POST
   @Path("/compile")
   public String compile(@QueryParam("query") String queryText)
   {
      System.out.println(queryText);
      Assert.assertEquals(queryText, QUERY);
      return queryText;
   }

   @Path("/widget/{date}")
   @GET
   @Produces("text/plain")
   public String get(@PathParam("date") String date)
   {
      return date;
   }

   @Path("/plus/{plus}")
   @GET
   @Produces("text/plain")
   public String getPlus(@PathParam("plus") String p)
   {
      System.out.println("GET PLUS: " + p);
      System.out.println("request URL: " + info.getRequestUri());
      Assert.assertEquals("foo+bar", p);
      return p;
   }


}
