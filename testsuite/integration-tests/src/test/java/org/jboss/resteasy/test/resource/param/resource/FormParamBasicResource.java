package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.SortedSet;

@Path(value = "/FormParamTest/")
public class FormParamBasicResource {
   @Path(value = "/ParamEntityWithFromString")
   @POST
   @Consumes("application/x-www-form-urlencoded")
   public Response fromString(
         @Encoded @DefaultValue("FromString") @FormParam("default_argument") FormParamEntityWithFromString defaultArgument) {
      return Response.ok(response(defaultArgument.getValue())).build();
   }

   public static final String response(String argument) {
      return new StringBuilder().append("CTS_FORMPARAM:").append(argument)
            .toString();
   }

   @Path(value = "/string")
   @POST
   @Consumes("application/x-www-form-urlencoded")
   public Response string(
         @Encoded @DefaultValue("FromString") @FormParam("default_argument") String defaultArgument) {
      return Response.ok(response(defaultArgument)).build();
   }

   @Path(value = "/SortedSetFromString")
   @POST
   @Consumes("application/x-www-form-urlencoded")
   public Response sortedSetFromString(
         @Encoded @DefaultValue("SortedSetFromString") @FormParam("default_argument") SortedSet<FormParamEntityWithFromString> defaultArgument) {
      return Response.ok(response(defaultArgument.first().getValue()))
            .build();
   }

   @Path(value = "/ListConstructor")
   @POST
   @Consumes("application/x-www-form-urlencoded")
   public Response defaultListConstructor(
         @DefaultValue("ListConstructor") @FormParam("default_argument") List<FormParamEntityWithConstructor> defaultArgument) {
      return Response.ok(
            response(defaultArgument.listIterator().next().getValue()))
            .build();
   }

   @Path(value = "/IllegalArgumentException")
   @POST
   @Consumes("application/x-www-form-urlencoded")
   public Response throwWebApplicationException(
         @DefaultValue("SortedSetFromString") @FormParam("default_argument") FormParamEntityThrowsIllegaArgumentException defaultArgument) {
      return Response.ok().build();
   }


}
