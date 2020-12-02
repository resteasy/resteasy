package org.jboss.resteasy.test.cdi.modules.resource;

import javax.ejb.Local;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Local
public interface CDIModulesModulesResourceIntf {
   @GET
   @Path("test")
   Response test();
}
