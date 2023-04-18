package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/messages/TFM")
public interface ProxyJaxbResourcePostMessageIntf {

    @POST
    @Consumes("application/xml")
    Response saveMessage(ProxyJaxbResourcePostMessage msg);

}
