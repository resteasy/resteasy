/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.resource.param;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
@RequiresModule(value = "org.jboss.resteasy.resteasy-core", minVersion = "7.0.0.Final")
public class PathProviderTest {
    @ArquillianResource
    private URI uri;

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, PathProviderTest.class.getSimpleName() + ".war")
                .addClasses(TestApplication.class, TestResource.class)
                .addAsWebResource("org/jboss/resteasy/test/resource/param/dummy.txt", "dummy.txt")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void uploadFile() throws Exception {
        var toUpload = java.nio.file.Path.of("src/test/resources/" +
                getClass().getPackageName().replace(".", File.separator) +
                "/dummy.txt");
        try (Client client = ClientBuilder.newClient();
                Response response = client.target(TestUtil.generateUri(uri, "test/file"))
                        .request(MediaType.WILDCARD)
                        .post(Entity.entity(toUpload, MediaType.APPLICATION_OCTET_STREAM))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final String entity = response.readEntity(String.class);
            Assertions.assertEquals(Files.readString(toUpload), entity);
        }
    }

    @Test
    public void downloadFile() throws Exception {
        var toDownload = java.nio.file.Path.of("src/test/resources/" +
                getClass().getPackageName().replace(".", File.separator) +
                "/dummy.txt");
        try (Client client = ClientBuilder.newClient();
                Response response = client.target(TestUtil.generateUri(uri, "test/file"))
                        .request(MediaType.WILDCARD)
                        .get()) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final String entity = response.readEntity(String.class);
            Assertions.assertEquals(Files.readString(toDownload), entity);
        }
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
    }

    @Path("/test/file")
    public static class TestResource {
        @Inject
        private ServletContext servletContext;

        @POST
        @Consumes(MediaType.APPLICATION_OCTET_STREAM)
        @Produces(MediaType.TEXT_PLAIN)
        public Response uploadFile(final java.nio.file.Path path) throws IOException {
            if (path == null) {
                throw new WebApplicationException("Path is null");
            }
            var contents = Files.readString(path);
            return Response.ok(contents).build();
        }

        @GET
        @Produces(MediaType.APPLICATION_OCTET_STREAM)
        public Response downloadFile() {
            return Response.ok(java.nio.file.Path.of(servletContext.getRealPath("/dummy.txt"))).build();
        }
    }
}
