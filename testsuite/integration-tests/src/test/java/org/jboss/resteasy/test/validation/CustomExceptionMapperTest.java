package org.jboss.resteasy.test.validation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.test.validation.resource.CustomExceptionMapperClassConstraint;
import org.jboss.resteasy.test.validation.resource.CustomExceptionMapperClassValidator;
import org.jboss.resteasy.test.validation.resource.CustomExceptionMapperResource;
import org.jboss.resteasy.test.validation.resource.CustomExceptionMapperReport;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import java.io.File;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1137.
 * This package (along with @see org.jboss.resteasy.test.resteasy1137) tests versioning compatibility
 * of the class org.jboss.resteasy.api.validation.ResteasyViolationException in module resteasy-validator-provider11.
 *
 * As of release 3.0.12.Final, ResteasyViolationException was changed from a subclass of javax.validation.ValidationException
 * to a subclass of javax.validation.ConstraintViolationException, which is a subclass of javax.validation.ValidationException.
 *
 * The jar validation-versioning.jar in src/test/resources/org/jboss/test/validation contains the class
 * org.jboss.resteasy.test.validation.versioning.CustomExceptionMapper, with the method
 *
 * <p>
 * <pre>
 * {@code
 *   public Response toResponse(ResteasyViolationException rve);
 * }
 * </pre>
 * <p>
 *
 * which was compiled with the previous version of ResteasyViolationException. The test in these two packages shows
 * that the two versions of ResteasyViolationException are binary compatible.
 *
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CustomExceptionMapperTest {
    static final String testFilePath = TestUtil.getResourcePath(CustomExceptionMapperTest.class, "validation-versioning.customjar");

    @Deployment
    public static Archive<?> createTestArchive() {
        File file = new File(testFilePath);
        Assert.assertTrue("File " + testFilePath + " doesn't exists", file.exists());
        WebArchive war = TestUtil.prepareArchive(CustomExceptionMapperTest.class.getSimpleName())
                .addClasses(CustomExceptionMapperResource.class)
                .addClasses(CustomExceptionMapperClassConstraint.class, CustomExceptionMapperClassValidator.class,
                        CustomExceptionMapperReport.class)
                .addAsLibrary(file, "validation-versioning.jar")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        return war;
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CustomExceptionMapperTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Text exception mapper for input violations
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testExceptionMapperInputViolations() throws Exception {
        Client client = ClientBuilder.newClient();
        Builder builder = client.target(generateURL("/all/a/b/c")).request();
        builder.accept(MediaType.APPLICATION_XML);
        ClientResponse response = (ClientResponse) builder.get();
        Assert.assertEquals("User defined error code is not in response", 444, response.getStatus());
        CustomExceptionMapperReport report = response.readEntity(CustomExceptionMapperReport.class);
        Assert.assertTrue("Wrong count of validation error", countViolations(report, 1, 1, 1, 1, 0));
    }

    /**
     * @tpTestDetails Text exception mapper for output violations
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testExceptionMapperOutputViolations() throws Exception {
        Client client = ClientBuilder.newClient();
        Builder builder = client.target(generateURL("/all/abc/defg/hijkl")).request();
        builder.accept(MediaType.APPLICATION_XML);
        ClientResponse response = (ClientResponse) builder.get();
        Assert.assertEquals("User defined error code is not in response", 444, response.getStatus());
        CustomExceptionMapperReport report = response.readEntity(CustomExceptionMapperReport.class);
        Assert.assertTrue("Wrong count of validation error", countViolations(report, 0, 0, 0, 0, 1));
    }

    /**
     * Custom CustomExceptionMapperReport class used. TestUtil methods can not be used instead of this method.
     */
    protected boolean countViolations(CustomExceptionMapperReport report, int fieldCount, int propertyCount, int classCount, int parameterCount, int returnValueCount) {
        return report.getFieldViolations() == fieldCount
                && report.getPropertyViolations() == propertyCount
                && report.getClassViolations() == classCount
                && report.getParameterViolations() == parameterCount
                && report.getReturnValueViolations() == returnValueCount;
    }
}
