/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.resteasy.test.cdi.injection.resource.RequiredInjectableContextResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ArquillianTest
@RunAsClient
@RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "7.0.5.Final")
public class RequiredInjectableContextTest extends AbstractRequiredInjectableContextTest {

    RequiredInjectableContextTest() {
        super("/context");
    }

    @Deployment(testable = false)
    public static Archive<?> deployment() {
        return defaultDeployment(RequiredInjectableContextTest.class).addClasses(RequiredInjectableContextResource.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
    }
}
