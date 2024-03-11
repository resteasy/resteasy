package org.jboss.resteasy.test.validation;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
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
 * @tpTestCaseDetails Test ignored validation
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class GetterReturnValueNotValidatedTest {
    protected final Logger logger = Logger.getLogger(GetterReturnValueNotValidatedTest.class.getName());
    ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(GetterReturnValueNotValidatedTest.class.getSimpleName())
                .addClasses(GetterReturnValueValidatedResourceWithGetterViolation.class)
                .addClasses(GetterReturnValueValidatedResourceResetCount.class)
                .addAsResource("META-INF/services/jakarta.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, GetterReturnValueNotValidatedTest.class.getSimpleName());
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
     * @tpTestDetails Validation of getter return value is not expected.
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
        String entity = response.readEntity(String.class);
        logger.info(String.format("Response: %s", entity.replace('\r', ' ').replace('\t', ' ').replace('\n', ' ')));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assertions.assertNull(header, "Validation header was not excepted");
        Assertions.assertEquals("a", entity, "Wrong content of response");
        response.close();
    }
}
