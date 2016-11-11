package org.jboss.resteasy.test.client.proxy.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.core.Response;
import java.net.URI;

public class ProxyJaxbResourceMessageResource implements ProxyJaxbResourcePostMessageIntf {

    private static Logger logger = Logger.getLogger(ProxyJaxbResourceMessageResource.class.getName());

    @Override
    public Response saveMessage(ProxyJaxbResourcePostMessage msg) {
        logger.info("saveMessage");
        return Response.created(URI.create("/foo/bar")).build();
    }
}
