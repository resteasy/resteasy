/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.embedded.test.providers.custom.resource;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;

@Path("/")
public class ReaderWriterResource {
    private static Logger logger = Logger.getLogger(ReaderWriterResource.class);

    @Path("/simple")
    @GET
    public Response get() {
        Response.ResponseBuilder builder = Response.ok("hello world".getBytes());
        builder.header("CoNtEnT-type", "text/plain");
        return builder.build();
    }

    @Path("/string")
    @GET
    public Response getString() {
        Response.ResponseBuilder builder = Response.ok("hello world");
        builder.header("CoNtEnT-type", "text/plain");
        logger.info("getString");
        return builder.build();
    }

    @Path("/complex")
    @GET
    public Object getComplex() {
        Response.ResponseBuilder builder = Response.status(HttpResponseCodes.SC_FOUND)
                .entity("hello world".getBytes());
        builder.header("CoNtEnT-type", "text/plain");
        return builder.build();
    }

    @Path("/implicit")
    @GET
    @Produces("application/xml")
    public Object getCustomer() {
        logger.info("GET CUSTOEMR");
        ReaderWriterCustomer cust = new ReaderWriterCustomer();
        cust.setName("bill");
        return Response.ok(cust).build();
    }

    @Path("/implicit")
    @DELETE
    public Object deleteCustomer() {
        return Response.ok().build();
    }

    @Path("/complex")
    @DELETE
    public void deleteComplex() {

    }

    @Path("/priority")
    @GET
    @Produces("application/xml")
    public Object getCustomerWithProviderPriority() {
        logger.info("Get customer with application packaged prioritized provider");
        ReaderWriterCustomer cust = new ReaderWriterCustomer();
        cust.setName("resteasy");
        return Response.ok(cust).build();
    }

}
