package org.jboss.resteasy.test.resource.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.test.resource.basic.resource.UriInfoEncodedQueryResource;
import org.jboss.resteasy.test.resource.basic.resource.UriInfoEncodedTemplateResource;
import org.jboss.resteasy.test.resource.basic.resource.UriInfoEscapedMatrParamResource;
import org.jboss.resteasy.test.resource.basic.resource.UriInfoQueryParamsResource;
import org.jboss.resteasy.test.resource.basic.resource.UriInfoRelativizeResource;
import org.jboss.resteasy.test.resource.basic.resource.UriInfoSimpleResource;
import org.jboss.resteasy.test.resource.basic.resource.UriInfoSimpleSingletonResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests for java.net.URI class
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class UriInfoTest {

    protected final Logger logger = LogManager.getLogger(UriInfoTest.class.getName());

    private static Client client;

    @BeforeClass
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
        client = null;
    }

    @SuppressWarnings(value = "unchecked")
    @Deployment(name = "UriInfoSimpleResource")
    public static Archive<?> deployUriInfoSimpleResource() {
        WebArchive war = TestUtil.prepareArchive(UriInfoSimpleResource.class.getSimpleName());
        war.addClass(PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, UriInfoSimpleResource.class);
    }

    /**
     * @tpTestDetails Check uri from resource on server. Simple resource is used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUriInfo() throws Exception {
        basicTest("/simple", UriInfoSimpleResource.class.getSimpleName());
        basicTest("/simple/fromField", UriInfoSimpleResource.class.getSimpleName());
    }

    @Deployment(name = "UriInfoSimpleSingletonResource")
    public static Archive<?> deployUriInfoSimpleResourceAsSingleton() {
        WebArchive war = TestUtil.prepareArchive(UriInfoSimpleSingletonResource.class.getSimpleName());
        war.addClass(PortProviderUtil.class);
        List<Class<?>> singletons = new ArrayList<>();
        singletons.add(UriInfoSimpleSingletonResource.class);
        return TestUtil.finishContainerPrepare(war, null, singletons, (Class<?>[]) null);
    }

    /**
     * @tpTestDetails Check uri from resource on server. Resource is set as singleton to RESTEasy.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUriInfoWithSingleton() throws Exception {
        basicTest("/simple/fromField", UriInfoSimpleSingletonResource.class.getSimpleName());
    }

    @Deployment(name = "UriInfoEscapedMatrParamResource")
    public static Archive<?> deployUriInfoEscapedMatrParamResource() {
        WebArchive war = TestUtil.prepareArchive(UriInfoEscapedMatrParamResource.class.getSimpleName());
        war.addClass(PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, UriInfoEscapedMatrParamResource.class);
    }

    /**
     * @tpTestDetails Check uri from resource on server. Test complex parameter.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEscapedMatrParam() throws Exception {
        basicTest("/queryEscapedMatrParam;a=a%3Bb;b=x%2Fy;c=m%5Cn;d=k%3Dl", UriInfoEscapedMatrParamResource.class.getSimpleName());
    }

    @Deployment(name = "UriInfoEncodedTemplateResource")
    public static Archive<?> deployUriInfoEncodedTemplateResource() {
        WebArchive war = TestUtil.prepareArchive(UriInfoEncodedTemplateResource.class.getSimpleName());
        war.addClass(PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, UriInfoEncodedTemplateResource.class);
    }

    /**
     * @tpTestDetails Check uri from resource on server. Test space character in URI.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEncodedTemplateParams() throws Exception {
        basicTest("/a%20b/x%20y", UriInfoEncodedTemplateResource.class.getSimpleName());
    }


    @Deployment(name = "UriInfoEncodedQueryResource")
    public static Archive<?> deployUriInfoEncodedQueryResource() {
        WebArchive war = TestUtil.prepareArchive(UriInfoEncodedQueryResource.class.getSimpleName());
        war.addClass(PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, UriInfoEncodedQueryResource.class);
    }

    /**
     * @tpTestDetails Check uri from resource on server. Test space character in URI attribute.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEncodedQueryParams() throws Exception {
        basicTest("/query?a=a%20b", UriInfoEncodedQueryResource.class.getSimpleName());
    }

    @Deployment(name = "UriInfoRelativizeResource")
    public static Archive<?> deployUriInfoRelativizeResource() {
        WebArchive war = TestUtil.prepareArchive(UriInfoRelativizeResource.class.getSimpleName());
        war.addClass(PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, UriInfoRelativizeResource.class);
    }

    /**
     * @tpTestDetails Check uri from resource on server. Test return value from resource - same URI address.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRelativize() throws Exception {
            String uri = PortProviderUtil.generateURL("/", UriInfoRelativizeResource.class.getSimpleName());
            WebTarget target = client.target(uri);
            String result;
            result = target.path("a/b/c").queryParam("to", "a/d/e").request().get(String.class);
            Assert.assertEquals("../../d/e", result);
            result = target.path("a/b/c").queryParam("to", UriBuilder.fromUri(uri).path("a/d/e").build().toString()).request().get(String.class);
            Assert.assertEquals("../../d/e", result);
            result = target.path("a/b/c").queryParam("to", "http://foobar/a/d/e").request().get(String.class);
            Assert.assertEquals("http://foobar/a/d/e", result);
    }

    /**
     * @tpTestDetails Check uri on client. Base unit test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResolve() throws Exception {
        URI uri = new URI("http://localhost/base1/base2");
        logger.info(String.format("Resolved foo: %s", uri.resolve("foo")));
        logger.info(String.format("Resolved /foo: %s", uri.resolve("/foo")));
        logger.info(String.format("Resolved ../foo: %s", uri.resolve("../foo")));
    }


    private void basicTest(String path, String testName) throws Exception {
        Response response = client.target(PortProviderUtil.generateURL(path, testName)).request().get();
        try {
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        } finally {
            response.close();
        }
    }

    @Deployment(name = "UriInfoQueryParamsResource")
    public static Archive<?> deployUriInfoQueryParamsResource() {
        WebArchive war = TestUtil.prepareArchive(UriInfoQueryParamsResource.class.getSimpleName());
        war.addClass(PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, UriInfoQueryParamsResource.class);
    }

    /**
     * @tpTestDetails Test that UriInfo.getQueryParameters() returns an immutable map. Test's logic is in end-point.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    @Category({NotForForwardCompatibility.class})
    public void testQueryParamsMutability() throws Exception {
        basicTest("/queryParams?a=a,b", "UriInfoQueryParamsResource");
    }

}
