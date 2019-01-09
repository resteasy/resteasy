package org.jboss.resteasy.test.providers.multipart.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;

@Path("/ITest")
public interface XOPMultipartProxy {

   @POST
   @Consumes(MediaType.APPLICATION_XML)
   @Path("/getFileXOPMulti")
   @Produces("multipart/related")
   @XopWithMultipartRelated
   XOPMultipartProxyGetFileRestResponse getFileXOPMulti(XOPMultipartProxyGetFileRequest request) throws Exception;
}


