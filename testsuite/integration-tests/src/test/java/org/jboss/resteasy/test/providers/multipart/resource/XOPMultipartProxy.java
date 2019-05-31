package org.jboss.resteasy.test.providers.multipart.resource;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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


