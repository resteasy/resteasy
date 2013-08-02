package org.jboss.resteasy.resteasy801;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

public interface TestSubResourceIntf
{
   @GET
   @Path("list")
   @Produces("application/*+json")
   public List<AbstractParent> resourceMethod();

   @GET
   @Path("one")
   @Produces("application/*+json")
   public AbstractParent resourceMethodOne();
}
