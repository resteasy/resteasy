/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection;

import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.cdi.injection.resource.RequiredCdiInjectableContextResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class RequiredCdiInjectableContextTest extends AbstractRequiredInjectableContextTest {

    public RequiredCdiInjectableContextTest() {
        super("/inject");
    }

    @Deployment(testable = false)
    public static Archive<?> deployment() {
        return defaultDeployment(RequiredCdiInjectableContextTest.class).addClasses(RequiredCdiInjectableContextResource.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
    }

    @Test
    public void client() throws Exception {
        final Response response = get("client/request");
        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
        Assertions.assertEquals("GET", response.readEntity(String.class));
    }
}
