package org.jboss.resteasy.test.resource.proxy;

import javax.ws.rs.GET;

/**
 * Objects of this class are used as subresources.
 *
 * @author Jozef Hartinger
 */
public class Car
{

   private String id;

   public Car()
   {
   }

   public Car(String id)
   {
      this.id = id;
   }

   @GET
   public String getId()
   {
      return id;
   }
}
