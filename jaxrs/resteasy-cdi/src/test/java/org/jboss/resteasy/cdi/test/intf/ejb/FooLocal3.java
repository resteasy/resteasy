package org.jboss.resteasy.cdi.test.intf.ejb;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("foo")
public interface FooLocal3
{
   @GET
   String foo3();
}
