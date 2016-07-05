package org.jboss.resteasy.test.cdi.modules.resource;

import javax.ejb.Local;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Local
public interface CDIModulesModulesResourceIntf {
    @GET
    @Path("test")
    Response test();
}
