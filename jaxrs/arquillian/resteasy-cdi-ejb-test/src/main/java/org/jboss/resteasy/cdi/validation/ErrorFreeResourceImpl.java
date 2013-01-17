package org.jboss.resteasy.cdi.validation;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 25, 2012
 */
@Path("correct")
@Stateless
@SumConstraint(min=3, max=17)
public class ErrorFreeResourceImpl implements ErrorFreeResource
{
   @Inject
   private Logger log;
   
   @Inject
   @Min(3)
   @Max(7)
   @NumberOneBinding
   private int numberOne;
   
   private int numberTwo;
   
   @GET
   @Path("test/{num}")
   @Max(10)
   public int test(@Min(5) @Max(10) @PathParam("num") int num)
   {
      log.info("entering ErrorFreeResourceImpl.test()");
      return num + 1;
   }

   @Override
   public int getNumberOne()
   {
      return numberOne;
   }

   @Override
   @Min(13)
   @Max(17) 
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
