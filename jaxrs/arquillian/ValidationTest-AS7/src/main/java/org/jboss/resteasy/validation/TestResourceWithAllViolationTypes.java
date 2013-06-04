package org.jboss.resteasy.validation;

import javax.validation.constraints.Size;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jboss.resteasy.spi.validation.ValidateRequest;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 18, 2013
 */
@Path("all")
@TestClassConstraint(5)
@ValidateRequest
public class TestResourceWithAllViolationTypes
{
   @Size(min=2, max=4)
   @PathParam("s")
   String s;

   private String t;

   @Size(min=3, max=5)  
   public String getT()
   {
      return t;
   }
   
   public String retrieveS()
   {
      return s;
   }

   @PathParam("t") 
   public void setT(String t)
   {
      System.out.println(this + " t: " + t);
      this.t = t;
   }

   @POST
   @Path("{s}/{t}")
   @FooConstraint(min=4,max=5)
   public Foo post(@FooConstraint(min=3,max=5) Foo foo, @PathParam("s") String s)
   {
      System.out.println(this + " s: " + s);
      System.out.println(this + " this.s: " + this.s);
      return foo;
   }
}
