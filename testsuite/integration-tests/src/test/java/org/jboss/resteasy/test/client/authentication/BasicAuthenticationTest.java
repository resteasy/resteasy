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

package org.jboss.resteasy.test.client.authentication;

import static org.jboss.resteasy.test.client.authentication.TestAuth.CREDENTIALS_USER_1;
import static org.jboss.resteasy.test.client.authentication.TestAuth.CREDENTIALS_USER_2;
import static org.jboss.resteasy.test.client.authentication.TestAuth.USER_1;
import static org.jboss.resteasy.test.client.authentication.TestAuth.USER_2;

import java.net.URL;

import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import dev.resteasy.client.util.authentication.HttpAuthenticators;
import dev.resteasy.client.util.authentication.UserCredentials;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@ServerSetup(AuthenticationSetupTask.class)
public class BasicAuthenticationTest {

    @ArquillianResource
    private URL url;

    private Client globalClient;

    @BeforeEach
    public void createClient() {
        globalClient = ClientBuilder.newClient();
    }

    @AfterEach
    public void closeClient() {
        if (globalClient != null) {
            globalClient.close();
        }
    }

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, BasicAuthenticationTest.class.getSimpleName() + ".war")
                .addClasses(
                        RestActivator.class,
                        UserResource.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(TestAuth.createJBossWebXml(), "jboss-web.xml")
                .addAsWebInfResource(TestAuth.createWebXml("BASIC"), "web.xml");
    }

    @Test
    public void failedAuth() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(TestUtil.generateUri(url, "user"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            Assertions.assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
        }
    }

    @Test
    public void basicAuth() throws Exception {
        try (
                Client client = ClientBuilder.newBuilder()
                        .register(HttpAuthenticators.basic(CREDENTIALS_USER_1))
                        .build()) {
            final Response response = client.target(TestUtil.generateUri(url, "user"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final JsonObject json = response.readEntity(JsonObject.class);
            validate(json);
        }
    }

    @Test
    public void basicAuthTarget() throws Exception {
        final Response response = globalClient.target(TestUtil.generateUri(url, "user"))
                .register(HttpAuthenticators.basic(CREDENTIALS_USER_1))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        final JsonObject json = response.readEntity(JsonObject.class);
        validate(json);
    }

    @Test
    public void basicAuthUser2() throws Exception {
        try (
                Client client = ClientBuilder.newBuilder()
                        .register(HttpAuthenticators.basic(CREDENTIALS_USER_2))
                        .build()) {
            final Response response = client.target(TestUtil.generateUri(url, "user"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final JsonObject json = response.readEntity(JsonObject.class);
            Assertions.assertEquals(USER_2, json.getString("username"));
            final String authHeader = json.getString("authHeader");
            Assertions.assertEquals(TestAuth.BASIC_AUTH_HEADER_USER_2, authHeader);
        }
    }

    @Test
    public void available() throws Exception {
        try (
                Client client = ClientBuilder.newBuilder()
                        .register(HttpAuthenticators.available(CREDENTIALS_USER_1))
                        .build()) {
            final Response response = client.target(TestUtil.generateUri(url, "user"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final JsonObject json = response.readEntity(JsonObject.class);
            validate(json);
        }
    }

    @Test
    public void invalidBasicAuth() throws Exception {
        try (
                Client client = ClientBuilder.newBuilder()
                        .register(HttpAuthenticators.basic(UserCredentials.clear(USER_1, new char[] { 'x' })))
                        .build()) {
            final Response response = client.target(TestUtil.generateUri(url, "user"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            Assertions.assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
        }
    }

    private void validate(final JsonObject json) {
        Assertions.assertEquals(USER_1, json.getString("username"));
        final String authHeader = json.getString("authHeader");
        Assertions.assertEquals(TestAuth.BASIC_AUTH_HEADER_USER_1, authHeader);
    }
}
