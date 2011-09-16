package org.jboss.resteasy.spring.beanprocessor;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/count")
public class Counter
{
   int counter;

   @POST
   public String count()
   {
      return Integer.toString(counter++);
   }
}