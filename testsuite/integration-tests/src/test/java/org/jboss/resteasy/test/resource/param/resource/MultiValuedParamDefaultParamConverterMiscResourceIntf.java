package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

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