package org.jboss.resteasy.test.client.proxy.resource;

import org.jboss.resteasy.annotations.LinkHeaderParam;
import org.jboss.resteasy.annotations.ResponseObject;
import org.jboss.resteasy.annotations.Status;

import javax.ws.rs.GET;
import java.net.URI;

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
