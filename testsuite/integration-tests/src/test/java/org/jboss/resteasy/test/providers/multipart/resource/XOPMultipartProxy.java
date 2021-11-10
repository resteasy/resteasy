package org.jboss.resteasy.test.providers.multipart.resource;

import java.io.IOException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;


@Path("/ITest")
public interface XOPMultipartProxy {

   @POST
   @Consumes(MediaType.APPLICATION_XML)
   @Path("/getFileXOPMulti")
   @Produces("multipart/related")
   @XopWithMultipartRelated
   XOPMultipartProxyGetFileResponse getFile(String request) throws Exception;

   @POST
   @Consumes("multipart/related")
   @Produces(MediaType.APPLICATION_XML)
   @Path("/putFile")
   Response putFile(@XopWithMultipartRelated XOPMultipartProxyPutFileRequest putFileRequest) throws IOException;
}


