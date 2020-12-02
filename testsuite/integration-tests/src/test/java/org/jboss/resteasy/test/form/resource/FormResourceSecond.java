package org.jboss.resteasy.test.form.resource;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MultivaluedMap;

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
