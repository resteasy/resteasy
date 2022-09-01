/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class MultipartProviderTest {

    private static SeBootstrap.Instance INSTANCE;

    @BeforeClass
    public static void start() throws Exception {
        INSTANCE = SeBootstrap.start(TestApplication.class).toCompletableFuture().get(10, TimeUnit.SECONDS);
    }

    @AfterClass
    public static void stop() throws Exception {
        final SeBootstrap.Instance instance = INSTANCE;
        if (instance != null) {
            instance.stop().toCompletableFuture().get(10, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testMultipart() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            MultipartFormDataOutput multipart = new MultipartFormDataOutput();
            multipart.addFormData("content", "test content".getBytes(StandardCharsets.UTF_8), MediaType.APPLICATION_OCTET_STREAM_TYPE);
            try (
                    Response response = client.target(INSTANCE.configuration().baseUriBuilder().path("test"))
                            .request(MediaType.MULTIPART_FORM_DATA_TYPE)
                            .post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA_TYPE))
            ) {
                Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
                final MultipartFormDataInput input = response.readEntity(MultipartFormDataInput.class);
                if (input.getParts().size() != 2) {
                    final String msg = "Expected 2 entries got " +
                            input.getParts().size() +
                            '.' +
                            System.lineSeparator() +
                            getMessage(input);
                    Assert.fail(msg);
                }
                final Map<String, List<InputPart>> parts = input.getFormDataMap();
                List<InputPart> inputParts = parts.get("received-content");
                Assert.assertNotNull(getMessage(input), inputParts);
                Assert.assertEquals(1, inputParts.size());
                Assert.assertEquals("test content", inputParts.get(0).getBodyAsString());

                inputParts = parts.get("added-content");
                Assert.assertNotNull(getMessage(input), inputParts);
                Assert.assertEquals(1, inputParts.size());
                Assert.assertEquals("test added content", inputParts.get(0).getBodyAsString());
            }
        }
    }

    private static String getMessage(final MultipartFormDataInput input) {
        final StringBuilder msg = new StringBuilder();
        final Iterator<Map.Entry<String, List<InputPart>>> iter = input.getFormDataMap().entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<String, List<InputPart>> entry = iter.next();
            msg.append('[')
                    .append(entry.getKey())
                    .append("={");
            final Iterator<InputPart> partIter = entry.getValue().iterator();
            while (partIter.hasNext()) {
                final InputPart ip = partIter.next();
                try {
                    msg.append('{')
                            .append("headers=")
                            .append(ip.getHeaders())
                            .append(", mediaType=")
                            .append(ip.getMediaType())
                            .append(", body=")
                            .append(ip.getBodyAsString());
                    if (partIter.hasNext()) {
                        msg.append("}, ");
                    } else {
                        msg.append('}');
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (iter.hasNext()) {
                msg.append("], ");
            } else {
                msg.append(']');
            }
        }
        return msg.toString();
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(TestResource.class);
        }
    }

    @Path("/test")
    public static class TestResource {

        @POST
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.MULTIPART_FORM_DATA)
        public Response entity(@MultipartForm final TestEntity testEntity) {
            MultipartFormDataOutput multipart = new MultipartFormDataOutput();
            multipart.addFormData("received-content", testEntity.getName()
                    .getBytes(StandardCharsets.UTF_8), MediaType.APPLICATION_OCTET_STREAM_TYPE);
            multipart.addFormData("added-content", "test added content".getBytes(StandardCharsets.UTF_8), MediaType.APPLICATION_OCTET_STREAM_TYPE);

            GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<>(multipart, MultipartFormDataOutput.class);
            return Response.ok(entity, MediaType.MULTIPART_FORM_DATA).build();
        }
    }

    public static class TestEntity {
        @FormParam("content")
        @PartType(MediaType.APPLICATION_OCTET_STREAM)
        private byte[] name;

        public String getName() {
            return new String(name, StandardCharsets.UTF_8);
        }

        public void setName(final String name) {
            this.name = name.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof TestEntity)) {
                return false;
            }
            return Objects.equals(((TestEntity) obj).name, name);
        }
    }
}

