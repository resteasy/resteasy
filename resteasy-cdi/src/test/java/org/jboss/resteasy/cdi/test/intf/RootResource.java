package org.jboss.resteasy.cdi.test.intf;

import javax.ws.rs.Path;

@Path("resource")
public class RootResource implements SubresourceLocal
{
   public void foo()
   {
   }
}
