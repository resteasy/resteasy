package org.jboss.resteasy.resteasy1008;

import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.validation.hibernate.DoNotValidateRequest;
import org.jboss.resteasy.plugins.validation.hibernate.ValidateRequest;

/**
 * RESTEASY-1008
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jan 21, 2014
 */
@Path("/")
@SumConstraint(min = 9)
@ValidateRequest
public class TestResource
{
   @Min(3)
   @PathParam("field")
   protected int field;
   
   private int property;

   @GET
   @Produces(MediaType.TEXT_PLAIN)
   @Min(11)
   @Path("input/{field}/{property}/{param}")
   public int inputs(@Min(7) @PathParam("param") int param)
   {
      System.out.println("param: " + param);
      return param;
   }

   @Min(5)
   public int getProperty()
   {
      return property;
   }
   
   @PathParam("property") 
   public void setProperty(int property)
   {
      this.property = property;
      System.out.println("property: " + property);
   }
   
   @POST
   @Path("setter/{field}/{property}/{param}")
   public void setTest(@Min(7) int param)
   {
      System.out.println("setTest(): param: " + param);
   }
   
   @Path("locator/{field}/{property}/{param}")
   @Produces(MediaType.TEXT_PLAIN)
   public Object locator(@Min(11) @PathParam("param") int param)
   {
      System.out.println("TestResource.this: " + this);
      return new TestSubResource();
   }
   
   @GET
   @Path("none/{field}/{property}/{param}")
   @Produces(MediaType.TEXT_PLAIN)
   @DoNotValidateRequest
   public Object none(@Min(11) @PathParam("param") int param)
   {
      return param;
   }
   
   @GET
   @Path("noParams/{field}/{property}")
   @Produces(MediaType.TEXT_PLAIN)
   public Object noParams()
   {
      return "noParams";
   }
}