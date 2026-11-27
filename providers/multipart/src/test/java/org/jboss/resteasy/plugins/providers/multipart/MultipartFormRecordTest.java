/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2026 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.plugins.providers.multipart;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

@RestBootstrap(MultipartFormRecordTest.TestResource.class)
public class MultipartFormRecordTest {

    @RestResource
    @RequestPath("/test/record")
    private WebTarget recordTarget;

    @Test
    public void readsRecordFromMultipartForm() {
        final MultipartFormDataOutput output = new MultipartFormDataOutput();
        output.addFormData("name", "bob", MediaType.TEXT_PLAIN_TYPE);
        output.addFormData("nickname", "bobby", MediaType.TEXT_PLAIN_TYPE);
        output.addFormData("count", "7", MediaType.TEXT_PLAIN_TYPE);

        try (Response response = recordTarget.request()
                .post(Entity.entity(output, MediaType.MULTIPART_FORM_DATA_TYPE))) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(),
                    () -> "Request failed: " + response.readEntity(String.class));
            Assertions.assertEquals("bob:bobby:7:null:0", response.readEntity(String.class));
        }
    }

    public record Upload(@FormParam("name") String name,
            @org.jboss.resteasy.annotations.jaxrs.FormParam String nickname,
            @FormParam("count") int count,
            @FormParam("absentText") String absentText,
            @FormParam("absentNumber") int absentNumber) {
    }

    @Path("/test")
    public static class TestResource {

        @POST
        @Path("/record")
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.TEXT_PLAIN)
        public String post(@MultipartForm Upload upload) {
            return upload.name() + ':' + upload.nickname() + ':' + upload.count() + ':'
                    + upload.absentText() + ':' + upload.absentNumber();
        }
    }
}
