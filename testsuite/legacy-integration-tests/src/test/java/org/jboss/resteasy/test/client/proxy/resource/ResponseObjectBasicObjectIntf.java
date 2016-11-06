package org.jboss.resteasy.test.client.proxy.resource;

import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.annotations.ResponseObject;
import org.jboss.resteasy.annotations.Status;
import org.jboss.resteasy.client.ClientResponse; //@cs-: clientresponse (Tests deprecated client proxy)

import javax.ws.rs.HeaderParam;

@ResponseObject
public interface ResponseObjectBasicObjectIntf {
    @Status
    int status();

    @Body
    String body();

    org.jboss.resteasy.client.jaxrs.internal.ClientResponse response();

    ClientResponse responseDeprecated();

    @HeaderParam("Content-Type")
    String contentType();
}
