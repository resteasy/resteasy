package org.jboss.resteasy.test.form.resource;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;

@Path("/myform")
public class FormResourceSecond {
    @GET
    @Path("/server")
    @Produces("application/x-www-form-urlencoded")
    public MultivaluedMap<String, String> retrieveServername() {

        MultivaluedMap<String, String> serverMap = new MultivaluedMapImpl<String, String>();
        serverMap.add("servername", "srv1");
        serverMap.add("servername", "srv2");

        return serverMap;
    }

    @POST
    public void post() {

    }
}
