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
   public String regex(@QueryParam("w") @Separator("[-,;]") List<String> list);

   @Path("regex/client/cookie")
   @GET
   public String regexClientCookie(@Separator("(-)") @CookieParam("p") Set<String> ss);
   
   @Path("regex/client/header")
   @GET
   public String regexClientHeader(@Separator("(-)") @HeaderParam("p") Set<String> ss);
   
   @Path("regex/client/matrix")
   @GET
   public String regexClientMatrix(@Separator("(-)") @MatrixParam("p") Set<String> ss);
   
   @Path("regex/client/query")
   @GET
   public String regexClientQuery(@Separator("(-)") @QueryParam("p") Set<String> ss);
   
   @Path("regex/client/path/{p}")
   @GET
   public String regexClientPath(@Separator("(-)") @PathParam("p") Set<String> ss);
}