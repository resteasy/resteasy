package org.jboss.resteasy.test.resource.param;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.param.resource.RESTEasyParamBasicCustomValuesResource;
import org.jboss.resteasy.test.resource.param.resource.RESTEasyParamBasicJaxRsParamDifferentResource;
import org.jboss.resteasy.test.resource.param.resource.RESTEasyParamBasicJaxRsParamSameResource;
import org.jboss.resteasy.test.resource.param.resource.RESTEasyParamBasicProxy;
import org.jboss.resteasy.test.resource.param.resource.RESTEasyParamBasicProxyResource;
import org.jboss.resteasy.test.resource.param.resource.RESTEasyParamBasicResource;
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

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEasy param annotations (https://issues.jboss.org/browse/RESTEASY-1880)
 *                    Test logic is in the end-point in deployment.
 * @tpSince RESTEasy 3.6
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RESTEasyParamBasicTest {
    protected static final Logger logger = Logger.getLogger(JsonBindingTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(RESTEasyParamBasicTest.class.getSimpleName());
        war.addClass(RESTEasyParamBasicProxy.class);
        return TestUtil.finishContainerPrepare(war, null,
                RESTEasyParamBasicResource.class,
                RESTEasyParamBasicJaxRsParamDifferentResource.class,
                RESTEasyParamBasicJaxRsParamSameResource.class,
                RESTEasyParamBasicCustomValuesResource.class,
                RESTEasyParamBasicProxyResource.class);
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
        return PortProviderUtil.generateURL(path, RESTEasyParamBasicTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Basic check of new query parameters, matrix parameters, header parameters, cookie parameters and form parameters
     *                Test checks that RESTEasy can inject correct values to setters, constructors, class variables and method attributes
     *                This test uses new annotation only without any annotation value.
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void basicTest() {
        Response response = client.target(generateURL("/basic/a/pathParam0/pathParam1/pathParam2/pathParam3"))
                .queryParam("queryParam0", "queryParam0")
                .queryParam("queryParam1", "queryParam1")
                .queryParam("queryParam2", "queryParam2")
                .queryParam("queryParam3", "queryParam3")
                .matrixParam("matrixParam0", "matrixParam0")
                .matrixParam("matrixParam1", "matrixParam1")
                .matrixParam("matrixParam2", "matrixParam2")
                .matrixParam("matrixParam3", "matrixParam3")
                .request()
                .header("headerParam0", "headerParam0")
                .header("headerParam1", "headerParam1")
                .header("headerParam2", "headerParam2")
                .header("headerParam3", "headerParam3")
                .cookie("cookieParam0", "cookieParam0")
                .cookie("cookieParam1", "cookieParam1")
                .cookie("cookieParam2", "cookieParam2")
                .cookie("cookieParam3", "cookieParam3")
                .post(Entity.form(new Form()
                        .param("formParam0", "formParam0")
                        .param("formParam1", "formParam1")
                        .param("formParam2", "formParam2")
                        .param("formParam3", "formParam3")
                ));
        Assert.assertEquals("Success", 200, response.getStatus());
    }

    /**
     * @tpTestDetails Same check as basicTest with this changes:
     *                  * RESTEasy proxy is used
     *                  * test checks injection to method attributes only
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void proxyTest() {
        Response response = client.target(generateURL("/proxy/a/pathParam3"))
                .queryParam("queryParam3", "queryParam3")
                .matrixParam("matrixParam3", "matrixParam3")
                .request()
                .header("headerParam3", "headerParam3")
                .cookie("cookieParam3", "cookieParam3")
                .post(Entity.form(new Form()
                        .param("formParam3", "formParam3")
                ));
        Assert.assertEquals("Success", 200, response.getStatus());
    }

    /**
     * @tpTestDetails Checks new parameter annotations with custom values
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void customValuesTest() {
        Response response = client.target(generateURL("/custom/values/a/pathParam0/pathParam1/pathParam2/pathParam3"))
                .queryParam("queryParam0", "queryParam0")
                .queryParam("queryParam1", "queryParam1")
                .queryParam("queryParam2", "queryParam2")
                .queryParam("queryParam3", "queryParam3")
                .matrixParam("matrixParam0", "matrixParam0")
                .matrixParam("matrixParam1", "matrixParam1")
                .matrixParam("matrixParam2", "matrixParam2")
                .matrixParam("matrixParam3", "matrixParam3")
                .request()
                .header("headerParam0", "headerParam0")
                .header("headerParam1", "headerParam1")
                .header("headerParam2", "headerParam2")
                .header("headerParam3", "headerParam3")
                .cookie("cookieParam0", "cookieParam0")
                .cookie("cookieParam1", "cookieParam1")
                .cookie("cookieParam2", "cookieParam2")
                .cookie("cookieParam3", "cookieParam3")
                .post(Entity.form(new Form()
                        .param("formParam0", "formParam0")
                        .param("formParam1", "formParam1")
                        .param("formParam2", "formParam2")
                        .param("formParam3", "formParam3")
                ));
        Assert.assertEquals("Success", 200, response.getStatus());
    }

    /**
     * @tpTestDetails Checks both original and new parameters together in one end-point, original and new annotations uses the same param names
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void theSameNamesTest() {
        Response response = client.target(generateURL("/same/a/pathParam0/pathParam1/pathParam2/pathParam3"))
                .queryParam("queryParam0", "queryParam0")
                .queryParam("queryParam1", "queryParam1")
                .queryParam("queryParam2", "queryParam2")
                .queryParam("queryParam3", "queryParam3")
                .matrixParam("matrixParam0", "matrixParam0")
                .matrixParam("matrixParam1", "matrixParam1")
                .matrixParam("matrixParam2", "matrixParam2")
                .matrixParam("matrixParam3", "matrixParam3")
                .request()
                .header("headerParam0", "headerParam0")
                .header("headerParam1", "headerParam1")
                .header("headerParam2", "headerParam2")
                .header("headerParam3", "headerParam3")
                .cookie("cookieParam0", "cookieParam0")
                .cookie("cookieParam1", "cookieParam1")
                .cookie("cookieParam2", "cookieParam2")
                .cookie("cookieParam3", "cookieParam3")
                .post(Entity.form(new Form()
                        .param("formParam0", "formParam0")
                        .param("formParam1", "formParam1")
                        .param("formParam2", "formParam2")
                        .param("formParam3", "formParam3")
                ));
        Assert.assertEquals("Success", 200, response.getStatus());
    }

    /**
     * @tpTestDetails Checks both original and new parameters together in one end-point, original and new annotations uses different param names
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void differentNamesTest() {
        Response response = client.target(generateURL("/different/a/pathParam0/pathParam1/pathParam2/pathParam3/pathParam4"))
                .queryParam("queryParam0", "queryParam0")
                .queryParam("queryParam1", "queryParam1")
                .queryParam("queryParam2", "queryParam2")
                .queryParam("queryParam3", "queryParam3")
                .queryParam("queryParam4", "queryParam4")
                .matrixParam("matrixParam0", "matrixParam0")
                .matrixParam("matrixParam1", "matrixParam1")
                .matrixParam("matrixParam2", "matrixParam2")
                .matrixParam("matrixParam3", "matrixParam3")
                .matrixParam("matrixParam4", "matrixParam4")
                .request()
                .header("headerParam0", "headerParam0")
                .header("headerParam1", "headerParam1")
                .header("headerParam2", "headerParam2")
                .header("headerParam3", "headerParam3")
                .header("headerParam4", "headerParam4")
                .cookie("cookieParam0", "cookieParam0")
                .cookie("cookieParam1", "cookieParam1")
                .cookie("cookieParam2", "cookieParam2")
                .cookie("cookieParam3", "cookieParam3")
                .cookie("cookieParam4", "cookieParam4")
                .post(Entity.form(new Form()
                        .param("formParam0", "formParam0")
                        .param("formParam1", "formParam1")
                        .param("formParam2", "formParam2")
                        .param("formParam3", "formParam3")
                        .param("formParam4", "formParam4")
                ));
        Assert.assertEquals("Success", 200, response.getStatus());
    }

}
