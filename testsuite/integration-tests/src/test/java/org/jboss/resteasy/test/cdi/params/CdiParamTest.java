/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.resteasy.test.cdi.params.resources.CdiParamResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * Test that {@code @*Param} annotated fields, constructor parameters and method parameters are injected.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ArquillianTest
class CdiParamTest extends AbstractParamTest {

    CdiParamTest() {
        super("params");
    }

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return defaultDeployment(CdiParamTest.class).addClass(CdiParamResource.class);
    }

}
