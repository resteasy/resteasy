package org.jboss.resteasy.test.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.validation.resource.GetterReturnValueValidatedResourceResetCount;
import org.jboss.resteasy.test.validation.resource.GetterReturnValueValidatedResourceWithGetterViolation;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooReaderWriter;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test ignored validation
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class GetterReturnValueNotValidatedTest {
    protected final Logger logger = LogManager.getLogger(GetterReturnValueNotValidatedTest.class.getName());
    ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(GetterReturnValueNotValidatedTest.class.getSimpleName())
                .addClasses(GetterReturnValueValidatedResourceWithGetterViolation.class)
                .addClasses(GetterReturnValueValidatedResourceResetCount.class)
                .addAsResource("META-INF/services/javax.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, GetterReturnValueNotValidatedTest.class.getSimpleName());
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build().register(ValidationCoreFooReaderWriter.class);
    }

    @After
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
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Valid native constraint
        response = client.target(generateURL("/get")).request().get();
        String entity = response.readEntity(String.class);
        logger.info(String.format("Response: %s", entity.replace('\r', ' ').replace('\t', ' ').replace('\n', ' ')));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assert.assertNull("Validation header was not excepted", header);
        Assert.assertEquals("Wrong content of response", "a", entity);
        response.close();
    }
}
