package org.jboss.resteasy.cdi.validation;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.validation.hibernate.ValidateRequest;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 25, 2012
 */
@Path("incorrect")
@Stateless
@ValidateRequest
public class InputErrorResourceImpl implements InputErrorResource
{
   @Inject
   private Logger log;
   
   @Inject
   @NumberOneBinding
   @Min(13)
   private int numberOne;
   
   private int numberTwo;
   
   @GET
   @Path("test/{num}")
   public Response test(@PathParam("num") int num)
   {
      log.info("entering ErroneousResourceImpl.test()");
      System.out.println("entering testClassValidator(): numberOne: " + numberOne + ", numberTwo: " + numberTwo);
      return Response.ok().build();
   }

   @Override
   public int getNumberOne()
   {
      return numberOne;
   }
 
   @Override
   public int getNumberTwo()
   {
      return numberTwo;
   }

   @Override
   public void setNumberOne(int one)
   {
      numberOne = one;
   }

   @Override
   @Inject 
   public void setNumberTwo(@NumberTwoBinding int two)
   {
      numberTwo = two;
   }
}
