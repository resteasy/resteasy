package org.jboss.resteasy.test.jsapi;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.jsapi.resource.CustomResource;
import org.jboss.resteasy.test.jsapi.resource.RootApplication;
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
 * @tpSubChapter Jsapi
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class JSAPIGetBasicJsapiHandlingScriptTest {

    private static final String JSAPI = "jsapi";
    private static final String DOUBLESLASH = "doubleSlash";

    static ResteasyClient client;
    protected static final Logger logger = Logger.getLogger(JSAPIGetBasicJsapiHandlingScriptTest.class.getName());

    @Deployment(name = JSAPI)
    public static Archive<?> deployJSAPI() {
        WebArchive war = TestUtil.prepareArchive(JSAPI);
        war.addAsWebInfResource(JSAPIGetBasicJsapiHandlingScriptTest.class.getPackage(), "web.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    @Deployment(name = DOUBLESLASH)
    public static Archive<?> deployDoubleSlash() {
        WebArchive war = TestUtil.prepareArchive(DOUBLESLASH);
        war.addClasses(CustomResource.class, RootApplication.class);
        war.addAsWebInfResource(JSAPIGetBasicJsapiHandlingScriptTest.class.getPackage(), "web_double_slash.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path, String deploymentName) {
        return PortProviderUtil.generateURL(path, deploymentName);
    }

    /**
     * @tpTestDetails The deployed application has configured JSApi servlet and the test gets the header of JSAPI script
     *                for handling request to REST resources with javascript.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment(JSAPI)
    public void testGetJsapiHeaderScript() throws Exception {
        WebTarget target = client.target(generateURL("/rest-js", JSAPI));
        Response response = target.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String responseString = response.readEntity(String.class);
        logger.info(responseString);
        Assertions.assertTrue(responseString.contains("REST.Request = function() {"),
                "Basic javascript generated by jsapi doesn't contain expected code");
    }

    /**
     * @tpTestDetails Tests whether the {@code request.setURI(uri)} would contain "//" in case the combination of
     *                {@code Path} and {@code ApplicationPath} would produce "//" or if the extra "/" was removed
     * @tpSince RESTEasy 7.0.0
     */
    @Test
    @OperateOnDeployment(DOUBLESLASH)
    public void testDoubleSlashInURI() {
        WebTarget target = client.target(generateURL("/rest-js", DOUBLESLASH));
        Response response = target.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String responseString = response.readEntity(String.class);
        logger.info(responseString);
        Assertions.assertTrue(responseString.contains("8080/doubleSlash/rootApplication/"),
                "Basic javascript generated by jsapi doesn't contain expected line");
        Assertions.assertTrue(responseString.contains("uri += 'path';"),
                "Basic javascript generated by jsapi doesn't contain expected line");
    }
}
