/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.resteasy.test.cdi.params.resources.InvalidCdiParamResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;

/**
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ArquillianTest
@Tag("requires-enhanced-cdi")
class InvalidCdiBeanParamTest extends AbstractParamTest {

    InvalidCdiBeanParamTest() {
        super("invalid-bean");
    }

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return defaultDeployment(InvalidCdiBeanParamTest.class).addClass(InvalidCdiParamResource.class);
    }

    @Disabled("Setter method parameters annotated with @*Param are not required by the specification and RESTEasy does not currently allow it.")
    @Override
    void methodParametersAnnotated() {
        super.methodParametersAnnotated();
    }
}
