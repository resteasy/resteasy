/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2023 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.test.resteasy736;

import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

@Path("/")
@Produces("text/plain")
public class TestResource {

    private static final Logger LOG = Logger.getLogger(TestResource.class);

    @GET
    @Path("test")
    public void test(final @Suspended AsyncResponse response) {
        response.setTimeout(5000, TimeUnit.MILLISECONDS);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    LOG.info("TestResource: async thread started");
                    Thread.sleep(10000);
                    Response jaxrs = Response.ok("test").type(MediaType.TEXT_PLAIN).build();
                    response.resume(jaxrs);
                    LOG.info("TestResource: async thread finished");
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        };
        t.start();
    }

    @GET
    @Path("default")
    public void defaultTest(final @Suspended AsyncResponse response) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    LOG.info("TestResource: async thread started");
                    Thread.sleep(35000); // Jetty async timeout defaults to 30000.
                    Response jaxrs = Response.ok("test").type(MediaType.TEXT_PLAIN).build();
                    response.resume(jaxrs);
                    LOG.info("TestResource: async thread finished");
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        };
        t.start();
    }
}
