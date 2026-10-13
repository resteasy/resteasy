/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.resteasy.test.cdi.params.resources.VetoedParamResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Disabled;

/**
 * Tests that a resource which is not managed by CDI, as it's {@link jakarta.enterprise.inject.Vetoed @Vetoed}, still
 * gets the standard Jakarta REST parameter injection in a CDI environment.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ArquillianTest
@RunAsClient
class VetoedParamTest extends AbstractParamTest {

    VetoedParamTest() {
        super("vetoed");
    }

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return defaultDeployment(VetoedParamTest.class).addClass(VetoedParamResource.class);
    }

    @Override
    @Disabled("Setter method parameters annotated with @*Param are only injected for resources managed by CDI")
    void methodParametersAnnotated() {
        super.methodParametersAnnotated();
    }

}
