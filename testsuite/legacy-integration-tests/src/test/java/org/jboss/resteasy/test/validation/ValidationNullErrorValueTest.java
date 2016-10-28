package org.jboss.resteasy.test.validation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.validation.resource.ValidationNullErrorValueResourceWithNullFieldAndProperty;
import org.jboss.resteasy.test.validation.resource.ValidationNullErrorValueResourceWithNullParameterAndReturnValue;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Validation
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for validation of null error value
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ValidationNullErrorValueTest {

    static ResteasyClient client;

    public static Archive<?> generateArchive(Class<?> clazz) {
        WebArchive war = TestUtil.prepareArchive(clazz.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, clazz);
    }

    @BeforeClass
    public static void before() throws Exception {
        client = new ResteasyClientBuilder().build();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    @Deployment(name = "ValidationNullErrorValueResourceWithNullFieldAndProperty")
    public static Archive<?> createTestArchiveDefault() {
        return generateArchive(ValidationNullErrorValueResourceWithNullFieldAndProperty.class);
    }

    @Deployment(name = "ValidationNullErrorValueResourceWithNullParameterAndReturnValue")
    public static Archive<?> createTestArchiveFalse() {
        return generateArchive(ValidationNullErrorValueResourceWithNullParameterAndReturnValue.class);
    }

    /**
     * @tpTestDetails Test null field and property.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNullFieldAndProperty() throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/get",
                ValidationNullErrorValueResourceWithNullFieldAndProperty.class.getSimpleName())).request().accept(MediaType.APPLICATION_XML).get();
        ViolationReport report = response.readEntity(ViolationReport.class);
        TestUtil.countViolations(report, 1, 1, 0, 0, 0);
        response.close();
    }

    /**
     * @tpTestDetails Test null return value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNullParameterAndReturnValue() throws Exception {
        {
            // Null query parameter
            Response response = client.target(PortProviderUtil.generateURL("/post",
                    ValidationNullErrorValueResourceWithNullParameterAndReturnValue.class.getSimpleName())).request()
                    .accept(MediaType.APPLICATION_XML).post(Entity.text(new String()));
            ViolationReport report = response.readEntity(ViolationReport.class);
            TestUtil.countViolations(report, 0, 0, 0, 1, 0);
            response.close();
        }

        {
            // Null return value
            Response response = client.target(PortProviderUtil.generateURL("/get",
                    ValidationNullErrorValueResourceWithNullParameterAndReturnValue.class.getSimpleName())).request()
                    .accept(MediaType.APPLICATION_XML).get();
            ViolationReport report = response.readEntity(ViolationReport.class);
            TestUtil.countViolations(report, 0, 0, 0, 0, 1);
            response.close();
        }
    }
}
