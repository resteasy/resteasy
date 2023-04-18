package org.jboss.resteasy.test.client.proxy.resource;

import java.net.URI;

import javax.ws.rs.GET;

import org.jboss.resteasy.annotations.LinkHeaderParam;
import org.jboss.resteasy.annotations.ResponseObject;
import org.jboss.resteasy.annotations.Status;

@ResponseObject
public interface ResponseObjectHateoasObject {
    @Status
    int status();

    @LinkHeaderParam(rel = "nextLink")
    URI nextLink();

    @GET
    @LinkHeaderParam(rel = "nextLink")
    String followNextLink();
}
