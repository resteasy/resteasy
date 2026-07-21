/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.core.config.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.spi.config.ConfigurationFactory;

/**
 * A test application REST resource, exposing a method that returns the FQDN of the {@link ConfigurationFactory}
 * instance returned by {@link ConfigurationFactory#getInstance()}.
 */
@Path("/config")
public class ConfigurationFactoryResource {

    @GET
    @Path("factory-class")
    @Produces("text/plain")
    public String getFactoryClass() {
        return ConfigurationFactory.getInstance().getClass().getName();
    }
}
