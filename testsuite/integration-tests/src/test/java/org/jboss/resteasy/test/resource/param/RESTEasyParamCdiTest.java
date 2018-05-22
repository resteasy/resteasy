package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.param.resource.RESTEasyParamCdiResource;
import org.jboss.resteasy.test.providers.jsonb.basic.JsonBindingTest;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.is;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEasy param annotations (https://issues.jboss.org/browse/RESTEASY-1880)
 * Test logic is in the end-point in deployment.
 * This test checks CDI integration.
 * @tpSince RESTEasy 3.6
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RESTEasyParamCdiTest {
    protected static final Logger logger = Logger.getLogger(JsonBindingTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(RESTEasyParamCdiTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, RESTEasyParamCdiResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, RESTEasyParamCdiTest.class.getSimpleName());
    }


    /**
     * @tpTestDetails Checks end-point with @RequestScoped annotation
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void requestScopedTest() throws Exception {
        for (Integer i = 0; i < 100; i++) {
            logger.info("Request " + i);
            String defaultValue = i.toString();
            Response response = client.target(generateURL(String.format("/%d/%d/%d/%d",
                    i, i, i, i)))
                    .queryParam("queryParam0", defaultValue)
                    .queryParam("queryParam1", defaultValue)
                    .queryParam("queryParam2", defaultValue)
                    .queryParam("queryParam3", defaultValue)
                    .matrixParam("matrixParam0", defaultValue)
                    .matrixParam("matrixParam1", defaultValue)
                    .matrixParam("matrixParam2", defaultValue)
                    .matrixParam("matrixParam3", defaultValue)
                    .request()
                    .header("headerParam0", defaultValue)
                    .header("headerParam1", defaultValue)
                    .header("headerParam2", defaultValue)
                    .header("headerParam3", defaultValue)
                    .cookie("cookieParam0", defaultValue)
                    .cookie("cookieParam1", defaultValue)
                    .cookie("cookieParam2", defaultValue)
                    .cookie("cookieParam3", defaultValue)
                    .post(Entity.form(new Form()
                            .param("formParam0", defaultValue)
                            .param("formParam1", defaultValue)
                            .param("formParam2", defaultValue)
                            .param("formParam3", defaultValue)
                    ));
            Assert.assertThat("expected response code is 200, get: " + response.getStatus(),
                    response.getStatus(), is(200));
            String message = response.readEntity(String.class);
            Assert.assertThat("expected value: " + defaultValue + ", get: " + message, message, is(defaultValue));
        }
    }
}
