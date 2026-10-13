/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.basic;

import java.net.URL;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.basic.resource.StatelessApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests that a stateless session bean {@link Application} subclass which does not declare a scope is
 * {@link Dependent @Dependent}, rather than getting the {@code @ApplicationScoped} default scope of the
 * {@link ApplicationPath @ApplicationPath} stereotype, which is not a valid scope for a stateless session bean.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class StatelessApplicationTest {

    @ArquillianResource
    public URL url;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, StatelessApplicationTest.class.getSimpleName() + ".war")
                .addClasses(
                        ScopeResource.class,
                        StatelessApplication.class);
    }

    /**
     * The application deploys, and its bean is {@code @Dependent}.
     */
    @Test
    public void dependentScope() throws Exception {
        try (
                Client client = ClientBuilder.newClient();
                Response response = client.target(TestUtil.generateUri(url, "/scope"))
                        .request(MediaType.TEXT_PLAIN)
                        .get()) {
            final String entity = response.readEntity(String.class);
            Assertions.assertEquals(200, response.getStatus(), entity);
            Assertions.assertEquals(Dependent.class.getName(), entity);
        }
    }

    @Path("/scope")
    @Produces(MediaType.TEXT_PLAIN)
    public static class ScopeResource {

        @Inject
        BeanManager beanManager;

        /**
         * Returns the scope of the bean of the {@link StatelessApplication}.
         */
        @GET
        public String scope() {
            final Bean<?> bean = beanManager.resolve(beanManager.getBeans(StatelessApplication.class));
            return bean == null ? "none" : bean.getScope().getName();
        }
    }
}
