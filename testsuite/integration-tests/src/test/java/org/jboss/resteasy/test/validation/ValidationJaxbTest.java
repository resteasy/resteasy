package org.jboss.resteasy.test.validation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.validation.resource.ValidationCoreClassConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationCoreClassValidator;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFoo;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooReaderWriter;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooValidator;
import org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithAllViolationTypes;
import org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithReturnValues;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.containsString;

import java.util.Arrays;
import java.util.List;

/**
 * @tpSubChapter Validation
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for JBEAP-3280
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ValidationJaxbTest {
    ResteasyClient client;
    private static final String UNEXPECTED_VALIDATION_ERROR_MSG = "Unexpected validation error";

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build().register(ValidationCoreFooReaderWriter.class);
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ValidationJaxbTest.class.getSimpleName())
                .addClasses(ValidationCoreFoo.class, ValidationCoreFooConstraint.class, ValidationCoreFooReaderWriter.class, ValidationCoreFooValidator.class)
                .addClasses(ValidationCoreClassConstraint.class, ValidationCoreClassValidator.class)
                .addClasses(ValidationCoreResourceWithAllViolationTypes.class, ValidationCoreResourceWithReturnValues.class)
                .addAsResource("META-INF/services/javax.ws.rs.ext.Providers")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ValidationJaxbTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Raw XML check.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRawXML() throws Exception {
        doRawTest(MediaType.APPLICATION_XML_TYPE, "<fieldViolations><constraintType>FIELD</constraintType><path>s</path>");
    }

    /**
     * @tpTestDetails Raw JSON check.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRawJSON() throws Exception {
        doRawTest(MediaType.APPLICATION_JSON_TYPE, "\"fieldViolations\":[{\"constraintType\":\"FIELD\",\"path\":\"s\"");
    }

    /**
     * @tpTestDetails ViolationReport from XML check.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXML() throws Exception {
        doTest(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * @tpTestDetails ViolationReport from JSON check.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJSON() throws Exception {
        doTest(MediaType.APPLICATION_JSON_TYPE);
    }

    public void doTest(MediaType mediaType) throws Exception {
        ValidationCoreFoo foo = new ValidationCoreFoo("p");
        Response response = client.target(generateURL("/all/a/z")).request().accept(mediaType).post(Entity.entity(foo, "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assert.assertNotNull("Validation header is missing", header);
        Assert.assertTrue("Wrong value of validation header", Boolean.valueOf(header));
        ViolationReport r = response.readEntity(ViolationReport.class);
        TestUtil.countViolations(r, 1, 1, 1, 1, 0);
        ResteasyConstraintViolation violation = r.getFieldViolations().iterator().next();
        Assert.assertEquals(UNEXPECTED_VALIDATION_ERROR_MSG, "s", violation.getPath());
        violation = r.getPropertyViolations().iterator().next();
        Assert.assertEquals(UNEXPECTED_VALIDATION_ERROR_MSG, "t", violation.getPath());
        violation = r.getClassViolations().iterator().next();
        Assert.assertEquals(UNEXPECTED_VALIDATION_ERROR_MSG, "", violation.getPath());
        violation = r.getParameterViolations().iterator().next();
        String[] paths = new String[]{"post.arg0", "post.foo"};
        Assert.assertTrue(UNEXPECTED_VALIDATION_ERROR_MSG + paths, Arrays.asList(paths).contains(violation.getPath()));
        response.close();
    }

    public void doRawTest(MediaType mediaType, String expected) throws Exception {
        ValidationCoreFoo foo = new ValidationCoreFoo("p");
        Response response = client.target(generateURL("/all/a/z")).request().accept(mediaType).post(Entity.entity(foo, "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assert.assertNotNull("Validation header is missing", header);
        Assert.assertTrue("Wrong value of validation header", Boolean.valueOf(header));
        String report = response.readEntity(String.class);
        Assert.assertThat(UNEXPECTED_VALIDATION_ERROR_MSG, report, containsString(expected));
        response.close();
    }
}
