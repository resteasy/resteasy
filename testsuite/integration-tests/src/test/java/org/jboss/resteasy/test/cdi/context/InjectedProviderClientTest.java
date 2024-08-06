/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.cdi.context;

import java.net.URI;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ContextResolver;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.context.resources.EchoResource;
import org.jboss.resteasy.test.cdi.context.resources.EnumHeaderDelegate;
import org.jboss.resteasy.test.cdi.context.resources.JsonToString;
import org.jboss.resteasy.test.cdi.context.resources.JsonToStringContextResolver;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * This is a test for <a href="https://issues.redhat.com/browse/RESTEASY-3520">RESTEASY-3520</a>. It injects a
 * {@link ContextResolver} implementation and registers it with a client. The injected resolver is likely a CDI client
 * proxy given the {@link ApplicationScoped @ApplicationScoped} CDI scope for
 * {@linkplain jakarta.ws.rs.ext.Provider providers}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@ApplicationScoped
@RequiresModule(value = "org.jboss.resteasy.resteasy-core", minVersion = "7.0.0.Alpha3")
public class InjectedProviderClientTest {

    @Inject
    private JsonToStringContextResolver resolver;

    @Inject
    private EnumHeaderDelegate headerDelegate;

    @Inject
    private Client client;

    @ArquillianResource
    private URI uri;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(JsonToString.class,
                        JsonToStringContextResolver.class,
                        EchoResource.class,
                        EnumHeaderDelegate.class,
                        EchoResource.ResponseType.class,
                        TestUtil.class,
                        TestApplication.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void contextResolverInjected() throws Exception {
        Assertions.assertNotNull(resolver);
        Assertions.assertNotNull(headerDelegate);
        final String entityJson = "{\"test\":\"value\"}";
        try (
                Response response = client
                        .register(resolver)
                        .register(headerDelegate)
                        .target(TestUtil.generateUri(uri, "/echo")).request()
                        .post(Entity.json(entityJson))) {
            Assertions.assertEquals(200, response.getStatus());
            final var json = response.readEntity(JsonObject.class);
            Assertions.assertEquals(entityJson, json.getString("entity"));
            Assertions.assertEquals(EchoResource.ResponseType.TEST,
                    headerDelegate.fromString(response.getHeaderString(EchoResource.RESPONSE_TYPE_HEADER)));
        }
    }
}
