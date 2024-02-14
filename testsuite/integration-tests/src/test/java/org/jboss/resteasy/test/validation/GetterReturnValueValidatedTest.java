package org.jboss.resteasy.test.validation;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.validation.ResteasyViolationExceptionImpl;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.resource.GetterReturnValueValidatedResourceResetCount;
import org.jboss.resteasy.test.validation.resource.GetterReturnValueValidatedResourceWithGetterViolation;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooReaderWriter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for getter return value validation
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class GetterReturnValueValidatedTest {
    ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(GetterReturnValueValidatedTest.class.getSimpleName())
                .addClasses(GetterReturnValueValidatedResourceWithGetterViolation.class)
                .addClasses(GetterReturnValueValidatedResourceResetCount.class)
                .addAsResource("META-INF/services/jakarta.ws.rs.ext.Providers")
                .addAsResource(GetterReturnValueValidatedTest.class.getPackage(), "GetterReturnValueValidatedValidation.xml",
                        "META-INF/validation.xml");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, GetterReturnValueValidatedTest.class.getSimpleName());
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient().register(ValidationCoreFooReaderWriter.class);
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Validation of getter return value is expected because of specific validation.xml file.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReturnValues() throws Exception {
        Response response = client.target(generateURL("/get")).request().get();
        response.close();

        response = client.target(generateURL("/set")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Valid native constraint
        response = client.target(generateURL("/get")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, "Missing validation header");
        Assertions.assertTrue(Boolean.valueOf(header), "Wrong value of validation header");
        String entity = response.readEntity(String.class);
        ResteasyViolationException e = new ResteasyViolationExceptionImpl(entity);
        TestUtil.countViolations(e, 1, 0, 0, 0, 1);
        response.close();
    }
}
