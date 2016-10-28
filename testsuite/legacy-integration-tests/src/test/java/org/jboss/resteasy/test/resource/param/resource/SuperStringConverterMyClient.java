package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/")
public interface SuperStringConverterMyClient {
    @Path("person/{person}")
    @PUT
    void put(@PathParam("person") SuperStringConverterPerson p);

    @Path("company/{company}")
    @PUT
    void putCompany(@PathParam("company") SuperStringConverterCompany c);
}
