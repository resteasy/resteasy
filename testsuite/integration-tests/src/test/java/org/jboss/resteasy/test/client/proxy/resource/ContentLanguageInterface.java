package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

@Path("/api")
public interface ContentLanguageInterface
{

   @POST
   @Produces(MediaType.TEXT_PLAIN)
   @Consumes(MediaType.TEXT_PLAIN)
   String contentLang1(@HeaderParam(HttpHeaders.CONTENT_LANGUAGE) String contentLanguage, String subject);

   @Path("/second")
   @POST
   @Produces(MediaType.TEXT_PLAIN)
   @Consumes(MediaType.TEXT_PLAIN)
   String contentLang2(String subject, @HeaderParam(HttpHeaders.CONTENT_LANGUAGE) String contentLanguage);

}
