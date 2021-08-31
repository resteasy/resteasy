package org.jboss.resteasy.test.response.resource;

import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Variant;
import java.util.Locale;

@Path("/")
public class ContentLanguageHeaderResource {

   @GET
   @Path("language")
   @Produces({"text/plain"})
   public Response language() {
      ResponseBuilder responseBuilder = new ResponseBuilderImpl();
      Response response = responseBuilder.language("en-us").build();
      return response;
   }

   @GET
   @Path("language-ok")
   @Produces({"text/plain"})
   public Response languageOk() {
      Variant variant = Variant.languages(Locale.US).build().get(0);
      Response response = Response.ok("Hello World!", variant).build();
      return response;
   }

   @GET
   @Path("language-variant")
   @Produces({"text/plain"})
   public Response languageVariant() {
      Variant variant = Variant.languages(Locale.US).build().get(0);
      ResponseBuilder responseBuilder = new ResponseBuilderImpl();
      Response response = responseBuilder.variant(variant).build();
      return response;
   }
}
