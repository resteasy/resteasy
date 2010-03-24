package org.jboss.resteasy.cdi.test;

import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@Produces("text/plain")
public class SubResource implements Serializable
{
   private static final long serialVersionUID = 8722971164845596111L;

   @GET
   public String foo()
   {
      return "bar";
   }
}
