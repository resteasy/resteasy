package org.jboss.resteasy.test.validation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.validation.resource.ValidationCoreClassConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationCoreClassValidator;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFoo;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooReaderWriter;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooValidator;
import org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithAllViolationTypes;
import org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithReturnValues;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for default value of resteasy.validation.suppress.path parameter
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ValidationSurpressPathDefaultTest extends ValidationSuppressPathTestBase {
    @SuppressWarnings(value = "unchecked")
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive("Validation-test")
                .addClasses(ValidationCoreFoo.class, ValidationCoreFooConstraint.class, ValidationCoreFooReaderWriter.class,
                        ValidationCoreFooValidator.class)
                .addClasses(ValidationCoreClassConstraint.class, ValidationCoreClassValidator.class)
                .addClasses(ValidationCoreResourceWithAllViolationTypes.class, ValidationCoreResourceWithReturnValues.class)
                .addClass(ValidationSuppressPathTestBase.class)
                .addAsResource("META-INF/services/jakarta.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    /**
     * @tpTestDetails Test input violations.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInputViolations() throws Exception {
        doTestInputViolations("s", "t", "", new String[] { "post.arg0", "post.foo" });
    }

    /**
     * @tpTestDetails Test return value violations.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReturnValues() throws Exception {
        doTestReturnValueViolations("postNative.<return value>");
    }
}
