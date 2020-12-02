package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.annotations.Separator;

@Path("misc")
public interface MultiValuedParamDefaultParamConverterMiscResourceIntf {

   @Path("regex")
   @GET
   String regex(@QueryParam("w") @Separator("[-,;]") List<String> list);

   @Path("regex/client/cookie")
   @GET
   String regexClientCookie(@Separator("(-)") @CookieParam("p") Set<String> ss);

   @Path("regex/client/header")
   @GET
   String regexClientHeader(@Separator("(-)") @HeaderParam("p") Set<String> ss);

   @Path("regex/client/matrix")
   @GET
   String regexClientMatrix(@Separator("(-)") @MatrixParam("p") Set<String> ss);

   @Path("regex/client/query")
   @GET
   String regexClientQuery(@Separator("(-)") @QueryParam("p") Set<String> ss);

   @Path("regex/client/path/{p}")
   @GET
   String regexClientPath(@Separator("(-)") @PathParam("p") Set<String> ss);
}