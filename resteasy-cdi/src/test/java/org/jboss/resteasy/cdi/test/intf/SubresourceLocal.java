package org.jboss.resteasy.cdi.test.intf;

import javax.ws.rs.GET;

public interface SubresourceLocal
{
   @GET
   void foo();
}
