package org.jboss.resteasy.test.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
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
 * @tpTestCaseDetails Test for getter return value validation
 * @tpSince EAP 7.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class GetterReturnValueValidatedTest {
    protected static final Logger logger = LogManager.getLogger(GetterReturnValueNotValidatedTest.class.getName());
    ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(GetterReturnValueValidatedTest.class.getSimpleName())
                .addClasses(GetterReturnValueValidatedResourceWithGetterViolation.class)
                .addClasses(GetterReturnValueValidatedResourceResetCount.class)
                .addAsResource("META-INF/services/javax.ws.rs.ext.Providers")
                .addAsResource(GetterReturnValueValidatedTest.class.getPackage(), "GetterReturnValueValidatedValidation.xml", "META-INF/validation.xml");
        return TestUtil.finishContainerPrepare(war, null, null);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, GetterReturnValueValidatedTest.class.getSimpleName());
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
     * @tpTestDetails Validation of getter return value is expected because of specific validation.xml file.
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testReturnValues() throws Exception {
        logger.info("____________________ PRETEST ___________________________________");
        Response response = client.target(generateURL("/get")).request().get();
        response.close();

        logger.info("____________________ SET RELOADED _____________________________");
        response = client.target(generateURL("/set")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Valid native constraint
        logger.info("____________________ MAIN TEST LOGIC __________________________");
        response = client.target(generateURL("/get")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assert.assertNotNull("Missing validation header", header);
        Assert.assertTrue("Wrong value of validation header", Boolean.valueOf(header));
        String entity = response.readEntity(String.class);
        ResteasyViolationException e = new ResteasyViolationException(entity);
        TestUtil.countViolations(e, 1, 0, 0, 0, 0, 1);
        response.close();
    }
}
