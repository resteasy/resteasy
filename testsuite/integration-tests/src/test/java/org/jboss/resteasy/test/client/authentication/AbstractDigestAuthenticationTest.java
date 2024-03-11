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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.resteasy.client.util.authentication.HttpAuthenticators;
import dev.resteasy.client.util.authentication.UserCredentials;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public abstract class AbstractDigestAuthenticationTest {

    private final String algorithm;
    @ArquillianResource
    protected URL url;

    private Client globalClient;

    protected AbstractDigestAuthenticationTest(final String algorithm) {
        this.algorithm = algorithm;
    }

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
    public void digestAuth() throws Exception {
        try (
                Client client = ClientBuilder.newBuilder()
                        .register(HttpAuthenticators.digest(CREDENTIALS_USER_1))
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
    public void digestAuthTarget() throws Exception {
        final Response response = globalClient.target(TestUtil.generateUri(url, "user"))
                .register(HttpAuthenticators.digest(CREDENTIALS_USER_1))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        final JsonObject json = response.readEntity(JsonObject.class);
        validate(json);
    }

    @Test
    public void digestAuthUser2() throws Exception {
        try (
                Client client = ClientBuilder.newBuilder()
                        .register(HttpAuthenticators.digest(CREDENTIALS_USER_2))
                        .build()) {
            final Response response = client.target(TestUtil.generateUri(url, "user"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final JsonObject json = response.readEntity(JsonObject.class);
            validate(USER_2, json, 1);
        }
    }

    @Test
    public void digestAsyncAuth() throws Exception {
        try (
                Client client = ClientBuilder.newBuilder()
                        .register(HttpAuthenticators.digest(CREDENTIALS_USER_1))
                        .build()) {
            final Response response = client.target(TestUtil.generateUri(url, "user"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .async()
                    .get().get(5, TimeUnit.SECONDS);
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            final JsonObject json = response.readEntity(JsonObject.class);
            validate(json);
        }
    }

    @Test
    public void digestAuthMultipleRequests() throws Exception {
        try (
                Client client = ClientBuilder.newBuilder()
                        .register(HttpAuthenticators.digest(CREDENTIALS_USER_1))
                        .build()) {
            Response response = client.target(TestUtil.generateUri(url, "user"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            JsonObject json = response.readEntity(JsonObject.class);
            validate(json, 1);

            response = client.target(TestUtil.generateUri(url, "user"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            json = response.readEntity(JsonObject.class);
            validate(json, 2);
        }
    }

    @Test
    public void digestAuthMultipleRequestsNoCache() throws Exception {
        try (
                Client client = ClientBuilder.newBuilder()
                        // Disable caching
                        .register(HttpAuthenticators.digest(-1, CREDENTIALS_USER_1))
                        .build()) {
            Response response = client.target(TestUtil.generateUri(url, "user"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            JsonObject json = response.readEntity(JsonObject.class);
            validate(json, 1);

            response = client.target(TestUtil.generateUri(url, "user"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            json = response.readEntity(JsonObject.class);
            validate(json, 1);
        }
    }

    @Test
    public void availableDigestAuth() throws Exception {
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
    public void invalidDigestAuth() throws Exception {
        try (
                Client client = ClientBuilder.newBuilder()
                        .register(HttpAuthenticators.digest(UserCredentials.clear(USER_1, new char[0])))
                        .build()) {
            final Response response = client.target(TestUtil.generateUri(url, "user"))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            Assertions.assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
        }
    }

    private void validate(final JsonObject json) {
        validate(json, 1);
    }

    private void validate(final JsonObject json, final int nc) {
        validate(USER_1, json, nc);
    }

    private void validate(final String username, final JsonObject json, final int nc) {
        Assertions.assertEquals(username, json.getString("username"));
        final String authHeader = json.getString("authHeader");
        Assertions.assertEquals("Digest ", authHeader.substring(0, 7),
                String.format("Expected header to start with \"Digest\" but was \"%s\"", authHeader));
        // Remove "Digest " from header values
        final Map<String, String> values = parseHeader(authHeader.substring(7));
        if (username.matches("\\p{ASCII}+")) {
            Assertions.assertEquals('"' + username + '"', values.get("username"),
                    String.format("Expected username=\"%s\" in %s", username, authHeader));
        } else {
            final String encodedUser = URLEncoder.encode(username, StandardCharsets.UTF_8);
            Assertions.assertEquals("UTF-8''" + encodedUser, values.get("username*"),
                    String.format("Expected username*=UTF-8''%s in %s", encodedUser, authHeader));
        }
        Assertions.assertEquals("auth", values.get("qop"),
                String.format("Expected qop=auth in %s", authHeader));
        int parsedNc = -1;
        try {
            parsedNc = Integer.parseInt(values.getOrDefault("nc", "-1"));
        } catch (NumberFormatException e) {
            Assertions.fail(String.format("Failed to parse nc; %s - header=%s", e.getMessage(), authHeader));
        }
        Assertions.assertEquals(nc, parsedNc, String.format("Expected nc=%08x in %s", nc, authHeader));
        Assertions.assertEquals('"' + TestAuth.REALM_NAME + '"', values.get("realm"),
                String.format("Expected realm=\"%s\" in %s", TestAuth.REALM_NAME, authHeader));
        Assertions.assertEquals(algorithm, values.get("algorithm"),
                String.format("Expected algorithm=%s in %s", algorithm, authHeader));
    }

    private Map<String, String> parseHeader(final String header) {
        final Map<String, String> result = new HashMap<>();
        final StringBuilder key = new StringBuilder();
        final StringBuilder value = new StringBuilder();
        boolean inKey = true;
        boolean endQuoteReq = false;
        // Remove the "Digest " from the start
        for (char c : header.toCharArray()) {
            if (c == ',' && !endQuoteReq) {
                inKey = true;
                result.put(key.toString(), value.toString());
                key.setLength(0);
                value.setLength(0);
                continue;
            }
            if (c == '=') {
                inKey = false;
                continue;
            }
            if (c == '"') {
                endQuoteReq = !endQuoteReq;
            }
            if (inKey) {
                key.append(c);
            } else {
                value.append(c);
            }
        }
        result.put(key.toString(), value.toString());
        return result;
    }

}
