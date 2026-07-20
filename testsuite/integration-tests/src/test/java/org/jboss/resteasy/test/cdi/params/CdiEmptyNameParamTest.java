/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.resteasy.test.cdi.params.resources.CdiParamEmptyNamesResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Tag;

/**
 * Test that {@code @*Param} annotated fields, constructor parameters and method parameters are injected. The names are
 * left empty and the name for the annotation is resolved from the annotated element.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ArquillianTest
@Tag("requires-enhanced-cdi")
class CdiEmptyNameParamTest extends AbstractParamTest {

    CdiEmptyNameParamTest() {
        super("empty-names");
    }

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return defaultDeployment(CdiEmptyNameParamTest.class).addClass(CdiParamEmptyNamesResource.class);
    }

}
