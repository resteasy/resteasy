package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/messages/TFM")
public interface ProxyJaxbResourcePostMessageIntf {

   @POST
   @Consumes("application/xml")
   Response saveMessage(ProxyJaxbResourcePostMessage msg);

}
