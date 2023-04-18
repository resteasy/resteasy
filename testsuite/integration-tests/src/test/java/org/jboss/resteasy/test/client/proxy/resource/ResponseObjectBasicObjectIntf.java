package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.HeaderParam;

import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.annotations.ResponseObject;
import org.jboss.resteasy.annotations.Status;

@ResponseObject
public interface ResponseObjectBasicObjectIntf {
    @Status
    int status();

    @Body
    String body();

    org.jboss.resteasy.client.jaxrs.internal.ClientResponse response();

    @HeaderParam("Content-Type")
    String contentType();
}
