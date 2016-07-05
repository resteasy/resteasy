package org.jboss.resteasy.test.validation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFoo;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooReaderWriter;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooValidator;
import org.jboss.resteasy.test.validation.resource.ValidationCoreClassConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationCoreClassValidator;
import org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithAllViolationTypes;
import org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithReturnValues;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for true value of resteasy.validation.suppress.path parameter
 * @tpSince EAP 7.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ValidationSurpressPathTrueTest extends ValidationSuppressPathTestBase {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive("Validation-test")
                .addClasses(ValidationCoreFoo.class, ValidationCoreFooConstraint.class, ValidationCoreFooReaderWriter.class, ValidationCoreFooValidator.class)
                .addClasses(ValidationCoreClassConstraint.class, ValidationCoreClassValidator.class)
                .addClasses(ValidationCoreResourceWithAllViolationTypes.class, ValidationCoreResourceWithReturnValues.class)
                .addClass(ValidationSuppressPathTestBase.class)
                .addAsResource("META-INF/services/javax.ws.rs.ext.Providers");
        Map<String, String> contextParams = new HashMap<>();
        contextParams.put("resteasy.validation.suppress.path", "true");
        return TestUtil.finishContainerPrepare(war, contextParams, null);
    }

    /**
     * @tpTestDetails Test input violations.
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testInputViolations() throws Exception {
        doTestInputViolations("*", "*", "*", "*");
    }

    /**
     * @tpTestDetails Test return value violations.
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testReturnValues() throws Exception {
        doTestReturnValueViolations("*");
    }
}
