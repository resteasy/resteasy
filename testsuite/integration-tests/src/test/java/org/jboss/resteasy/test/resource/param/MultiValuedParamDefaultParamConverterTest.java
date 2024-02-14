package org.jboss.resteasy.test.resource.param;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterConstructorClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterCookieResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterFromStringClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterHeaderDelegate;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterHeaderDelegateClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterHeaderResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterMatrixResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterMiscResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterParamConverter;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterParamConverterClass;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterParamConverterProvider;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterPathResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterQueryResource;
import org.jboss.resteasy.test.resource.param.resource.MultiValuedParamDefaultParamConverterValueOfClass;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for RESTEasy extended support for multivalue @*Param (RESTEASY-1566 + RESTEASY-1746)
 *                    org.jboss.resteasy.plugins.providers.MultiValuedArrayParamConverter and
 *                    org.jboss.resteasy.plugins.providers.MultiValuedArrayParamConverter are used
 * @tpSince RESTEasy 4.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@TestMethodOrder(MethodName.class)
public class MultiValuedParamDefaultParamConverterTest {

    private static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MultiValuedParamDefaultParamConverterTest.class.getSimpleName());
        war.addClass(MultiValuedParamDefaultParamConverterConstructorClass.class);
        war.addClass(MultiValuedParamDefaultParamConverterFromStringClass.class);
        war.addClass(MultiValuedParamDefaultParamConverterHeaderDelegateClass.class);
        war.addClass(MultiValuedParamDefaultParamConverterHeaderDelegate.class);
        war.addClass(MultiValuedParamDefaultParamConverterParamConverterClass.class);
        war.addClass(MultiValuedParamDefaultParamConverterParamConverter.class);
        war.addClass(MultiValuedParamDefaultParamConverterValueOfClass.class);
        return TestUtil.finishContainerPrepare(war, null, MultiValuedParamDefaultParamConverterParamConverterProvider.class,
                MultiValuedParamDefaultParamConverterHeaderDelegate.class,
                MultiValuedParamDefaultParamConverterCookieResource.class,
                MultiValuedParamDefaultParamConverterHeaderResource.class,
                MultiValuedParamDefaultParamConverterMatrixResource.class,
                MultiValuedParamDefaultParamConverterMiscResource.class,
                MultiValuedParamDefaultParamConverterPathResource.class,
                MultiValuedParamDefaultParamConverterQueryResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MultiValuedParamDefaultParamConverterTest.class.getSimpleName());
    }

    @BeforeAll
    public static void beforeClass() throws Exception {
        client = ClientBuilder.newClient();
        ClientConfiguration config = ((ClientConfiguration) client.getConfiguration());
        MultiValuedParamDefaultParamConverterHeaderDelegate delegate = new MultiValuedParamDefaultParamConverterHeaderDelegate();
        config.addHeaderDelegate(MultiValuedParamDefaultParamConverterHeaderDelegateClass.class, delegate);
    }

    @AfterAll
    public static void afterClass() throws Exception {
        client.close();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @tpTestDetails QueryParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testCookie() {

        doTestCookie("constructor", "separator", "#", "list");
        doTestCookie("constructor", "separator", "#", "set");
        doTestCookie("constructor", "separator", "#", "sortedset");
        doTestCookie("constructor", "separator", "#", "array");
        doTestCookie("constructor", "regex", "#", "list");
        doTestCookie("constructor", "regex", "#", "set");
        doTestCookie("constructor", "regex", "#", "sortedset");
        doTestCookie("constructor", "regex", "#", "array");
        doTestCookie("constructor", "default", "-", "list");
        doTestCookie("constructor", "default", "-", "set");
        doTestCookie("constructor", "default", "-", "sortedset");
        doTestCookie("constructor", "default", "-", "array");

        doTestCookie("valueOf", "separator", "#", "list");
        doTestCookie("valueOf", "separator", "#", "set");
        doTestCookie("valueOf", "separator", "#", "sortedset");
        doTestCookie("valueOf", "separator", "#", "array");
        doTestCookie("valueOf", "regex", "#", "list");
        doTestCookie("valueOf", "regex", "#", "set");
        doTestCookie("valueOf", "regex", "#", "sortedset");
        doTestCookie("valueOf", "regex", "#", "array");
        doTestCookie("valueOf", "default", "-", "list");
        doTestCookie("valueOf", "default", "-", "set");
        doTestCookie("valueOf", "default", "-", "sortedset");
        doTestCookie("valueOf", "default", "-", "array");

        doTestCookie("fromString", "separator", "#", "list");
        doTestCookie("fromString", "separator", "#", "set");
        doTestCookie("fromString", "separator", "#", "sortedset");
        doTestCookie("fromString", "separator", "#", "array");
        doTestCookie("fromString", "regex", "#", "list");
        doTestCookie("fromString", "regex", "#", "set");
        doTestCookie("fromString", "regex", "#", "sortedset");
        doTestCookie("fromString", "regex", "#", "array");
        doTestCookie("fromString", "default", "-", "list");
        doTestCookie("fromString", "default", "-", "set");
        doTestCookie("fromString", "default", "-", "sortedset");
        doTestCookie("fromString", "default", "-", "array");

        doTestCookie("paramConverter", "separator", "#", "list");
        doTestCookie("paramConverter", "separator", "#", "set");
        doTestCookie("paramConverter", "separator", "#", "sortedset");
        doTestCookie("paramConverter", "separator", "#", "array");
        doTestCookie("paramConverter", "regex", "#", "list");
        doTestCookie("paramConverter", "regex", "#", "set");
        doTestCookie("paramConverter", "regex", "#", "sortedset");
        doTestCookie("paramConverter", "regex", "#", "array");
        doTestCookie("paramConverter", "default", "-", "list");
        doTestCookie("paramConverter", "default", "-", "set");
        doTestCookie("paramConverter", "default", "-", "sortedset");
        doTestCookie("paramConverter", "default", "-", "array");
    }

    /**
     * @tpTestDetails QueryParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testHeader() {

        doTestHeader("constructor", "separator", "-", "list");
        doTestHeader("constructor", "separator", "-", "set");
        doTestHeader("constructor", "separator", "-", "sortedset");
        doTestHeader("constructor", "separator", "-", "array");
        doTestHeader("constructor", "regex", "-", "list");
        doTestHeader("constructor", "regex", "-", "set");
        doTestHeader("constructor", "regex", "-", "sortedset");
        doTestHeader("constructor", "regex", "-", "array");
        doTestHeader("constructor", "default", ",", "list");
        doTestHeader("constructor", "default", ",", "set");
        doTestHeader("constructor", "default", ",", "sortedset");
        doTestHeader("constructor", "default", ",", "array");

        doTestHeader("valueOf", "separator", "-", "list");
        doTestHeader("valueOf", "separator", "-", "set");
        doTestHeader("valueOf", "separator", "-", "sortedset");
        doTestHeader("valueOf", "separator", "-", "array");
        doTestHeader("valueOf", "regex", "-", "list");
        doTestHeader("valueOf", "regex", "-", "set");
        doTestHeader("valueOf", "regex", "-", "sortedset");
        doTestHeader("valueOf", "regex", "-", "array");
        doTestHeader("valueOf", "default", ",", "list");
        doTestHeader("valueOf", "default", ",", "set");
        doTestHeader("valueOf", "default", ",", "sortedset");
        doTestHeader("valueOf", "default", ",", "array");

        doTestHeader("fromString", "separator", "-", "list");
        doTestHeader("fromString", "separator", "-", "set");
        doTestHeader("fromString", "separator", "-", "sortedset");
        doTestHeader("fromString", "separator", "-", "array");
        doTestHeader("fromString", "regex", "-", "list");
        doTestHeader("fromString", "regex", "-", "set");
        doTestHeader("fromString", "regex", "-", "sortedset");
        doTestHeader("fromString", "regex", "-", "array");
        doTestHeader("fromString", "default", ",", "list");
        doTestHeader("fromString", "default", ",", "set");
        doTestHeader("fromString", "default", ",", "sortedset");
        doTestHeader("fromString", "default", ",", "array");

        doTestHeader("headerDelegate", "separator", "-", "list");
        doTestHeader("headerDelegate", "separator", "-", "set");
        doTestHeader("headerDelegate", "separator", "-", "sortedset");
        doTestHeader("headerDelegate", "separator", "-", "array");
        doTestHeader("headerDelegate", "regex", "-", "list");
        doTestHeader("headerDelegate", "regex", "-", "set");
        doTestHeader("headerDelegate", "regex", "-", "sortedset");
        doTestHeader("headerDelegate", "regex", "-", "array");
        doTestHeader("headerDelegate", "default", ",", "list");
        doTestHeader("headerDelegate", "default", ",", "set");
        doTestHeader("headerDelegate", "default", ",", "sortedset");
        doTestHeader("headerDelegate", "default", ",", "array");

        doTestHeader("paramConverter", "separator", "-", "list");
        doTestHeader("paramConverter", "separator", "-", "set");
        doTestHeader("paramConverter", "separator", "-", "sortedset");
        doTestHeader("paramConverter", "separator", "-", "array");
        doTestHeader("paramConverter", "regex", "-", "list");
        doTestHeader("paramConverter", "regex", "-", "set");
        doTestHeader("paramConverter", "regex", "-", "sortedset");
        doTestHeader("paramConverter", "regex", "-", "array");
        doTestHeader("paramConverter", "default", ",", "list");
        doTestHeader("paramConverter", "default", ",", "set");
        doTestHeader("paramConverter", "default", ",", "sortedset");
        doTestHeader("paramConverter", "default", ",", "array");
    }

    /**
     * @tpTestDetails QueryParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testMatrix() {

        doTestMatrix("constructor", "separator", "-", "list");
        doTestMatrix("constructor", "separator", "-", "set");
        doTestMatrix("constructor", "separator", "-", "sortedset");
        doTestMatrix("constructor", "separator", "-", "array");
        doTestMatrix("constructor", "regex", "-", "list");
        doTestMatrix("constructor", "regex", "-", "set");
        doTestMatrix("constructor", "regex", "-", "sortedset");
        doTestMatrix("constructor", "regex", "-", "array");
        doTestMatrix("constructor", "default", ",", "list");
        doTestMatrix("constructor", "default", ",", "set");
        doTestMatrix("constructor", "default", ",", "sortedset");
        doTestMatrix("constructor", "default", ",", "array");

        doTestMatrix("valueOf", "separator", "-", "list");
        doTestMatrix("valueOf", "separator", "-", "set");
        doTestMatrix("valueOf", "separator", "-", "sortedset");
        doTestMatrix("valueOf", "separator", "-", "array");
        doTestMatrix("valueOf", "regex", "-", "list");
        doTestMatrix("valueOf", "regex", "-", "set");
        doTestMatrix("valueOf", "regex", "-", "sortedset");
        doTestMatrix("valueOf", "regex", "-", "array");
        doTestMatrix("valueOf", "default", ",", "list");
        doTestMatrix("valueOf", "default", ",", "set");
        doTestMatrix("valueOf", "default", ",", "sortedset");
        doTestMatrix("valueOf", "default", ",", "array");

        doTestMatrix("fromString", "separator", "-", "list");
        doTestMatrix("fromString", "separator", "-", "set");
        doTestMatrix("fromString", "separator", "-", "sortedset");
        doTestMatrix("fromString", "separator", "-", "array");
        doTestMatrix("fromString", "regex", "-", "list");
        doTestMatrix("fromString", "regex", "-", "set");
        doTestMatrix("fromString", "regex", "-", "sortedset");
        doTestMatrix("fromString", "regex", "-", "array");
        doTestMatrix("fromString", "default", ",", "list");
        doTestMatrix("fromString", "default", ",", "set");
        doTestMatrix("fromString", "default", ",", "sortedset");
        doTestMatrix("fromString", "default", ",", "array");

        doTestMatrix("paramConverter", "separator", "-", "list");
        doTestMatrix("paramConverter", "separator", "-", "set");
        doTestMatrix("paramConverter", "separator", "-", "sortedset");
        doTestMatrix("paramConverter", "separator", "-", "array");
        doTestMatrix("paramConverter", "regex", "-", "list");
        doTestMatrix("paramConverter", "regex", "-", "set");
        doTestMatrix("paramConverter", "regex", "-", "sortedset");
        doTestMatrix("paramConverter", "regex", "-", "array");
        doTestMatrix("paramConverter", "default", ",", "list");
        doTestMatrix("paramConverter", "default", ",", "set");
        doTestMatrix("paramConverter", "default", ",", "sortedset");
        doTestMatrix("paramConverter", "default", ",", "array");
    }

    /**
     * @tpTestDetails QueryParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testPath() {

        doTestPath("constructor", "separator", "-", "list");
        doTestPath("constructor", "separator", "-", "set");
        doTestPath("constructor", "separator", "-", "sortedset");
        doTestPath("constructor", "separator", "-", "array");
        doTestPath("constructor", "regex", "-", "list");
        doTestPath("constructor", "regex", "-", "set");
        doTestPath("constructor", "regex", "-", "sortedset");
        doTestPath("constructor", "regex", "-", "array");
        doTestPath("constructor", "default", ",", "list");
        doTestPath("constructor", "default", ",", "set");
        doTestPath("constructor", "default", ",", "sortedset");
        doTestPath("constructor", "default", ",", "array");

        doTestPath("valueOf", "separator", "-", "list");
        doTestPath("valueOf", "separator", "-", "set");
        doTestPath("valueOf", "separator", "-", "sortedset");
        doTestPath("valueOf", "separator", "-", "array");
        doTestPath("valueOf", "regex", "-", "list");
        doTestPath("valueOf", "regex", "-", "set");
        doTestPath("valueOf", "regex", "-", "sortedset");
        doTestPath("valueOf", "regex", "-", "array");
        doTestPath("valueOf", "default", ",", "list");
        doTestPath("valueOf", "default", ",", "set");
        doTestPath("valueOf", "default", ",", "sortedset");
        doTestPath("valueOf", "default", ",", "array");

        doTestPath("fromString", "separator", "-", "list");
        doTestPath("fromString", "separator", "-", "set");
        doTestPath("fromString", "separator", "-", "sortedset");
        doTestPath("fromString", "separator", "-", "array");
        doTestPath("fromString", "regex", "-", "list");
        doTestPath("fromString", "regex", "-", "set");
        doTestPath("fromString", "regex", "-", "sortedset");
        doTestPath("fromString", "regex", "-", "array");
        doTestPath("fromString", "default", ",", "list");
        doTestPath("fromString", "default", ",", "set");
        doTestPath("fromString", "default", ",", "sortedset");
        doTestPath("fromString", "default", ",", "array");

        doTestPath("paramConverter", "separator", "-", "list");
        doTestPath("paramConverter", "separator", "-", "set");
        doTestPath("paramConverter", "separator", "-", "sortedset");
        doTestPath("paramConverter", "separator", "-", "array");
        doTestPath("paramConverter", "regex", "-", "list");
        doTestPath("paramConverter", "regex", "-", "set");
        doTestPath("paramConverter", "regex", "-", "sortedset");
        doTestPath("paramConverter", "regex", "-", "array");
        doTestPath("paramConverter", "default", ",", "list");
        doTestPath("paramConverter", "default", ",", "set");
        doTestPath("paramConverter", "default", ",", "sortedset");
        doTestPath("paramConverter", "default", ",", "array");
    }

    /**
     * @tpTestDetails QueryParam test
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testQuery() {

        doTestQuery("constructor", "separator", "-", "list");
        doTestQuery("constructor", "separator", "-", "set");
        doTestQuery("constructor", "separator", "-", "sortedset");
        doTestQuery("constructor", "separator", "-", "array");
        doTestQuery("constructor", "regex", "-", "list");
        doTestQuery("constructor", "regex", "-", "set");
        doTestQuery("constructor", "regex", "-", "sortedset");
        doTestQuery("constructor", "regex", "-", "array");
        doTestQuery("constructor", "default", ",", "list");
        doTestQuery("constructor", "default", ",", "set");
        doTestQuery("constructor", "default", ",", "sortedset");
        doTestQuery("constructor", "default", ",", "array");

        doTestQuery("valueOf", "separator", "-", "list");
        doTestQuery("valueOf", "separator", "-", "set");
        doTestQuery("valueOf", "separator", "-", "sortedset");
        doTestQuery("valueOf", "separator", "-", "array");
        doTestQuery("valueOf", "regex", "-", "list");
        doTestQuery("valueOf", "regex", "-", "set");
        doTestQuery("valueOf", "regex", "-", "sortedset");
        doTestQuery("valueOf", "regex", "-", "array");
        doTestQuery("valueOf", "default", ",", "list");
        doTestQuery("valueOf", "default", ",", "set");
        doTestQuery("valueOf", "default", ",", "sortedset");
        doTestQuery("valueOf", "default", ",", "array");

        doTestQuery("fromString", "separator", "-", "list");
        doTestQuery("fromString", "separator", "-", "set");
        doTestQuery("fromString", "separator", "-", "sortedset");
        doTestQuery("fromString", "separator", "-", "array");
        doTestQuery("fromString", "regex", "-", "list");
        doTestQuery("fromString", "regex", "-", "set");
        doTestQuery("fromString", "regex", "-", "sortedset");
        doTestQuery("fromString", "regex", "-", "array");
        doTestQuery("fromString", "default", ",", "list");
        doTestQuery("fromString", "default", ",", "set");
        doTestQuery("fromString", "default", ",", "sortedset");
        doTestQuery("fromString", "default", ",", "array");

        doTestQuery("paramConverter", "separator", "-", "list");
        doTestQuery("paramConverter", "separator", "-", "set");
        doTestQuery("paramConverter", "separator", "-", "sortedset");
        doTestQuery("paramConverter", "separator", "-", "array");
        doTestQuery("paramConverter", "regex", "-", "list");
        doTestQuery("paramConverter", "regex", "-", "set");
        doTestQuery("paramConverter", "regex", "-", "sortedset");
        doTestQuery("paramConverter", "regex", "-", "array");
        doTestQuery("paramConverter", "default", ",", "list");
        doTestQuery("paramConverter", "default", ",", "set");
        doTestQuery("paramConverter", "default", ",", "sortedset");
        doTestQuery("paramConverter", "default", ",", "array");
    }

    /**
     * @tpTestDetails
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testMisc() {

        // Verifies that @Separator with a set of separators works.
        String response = client.target(generateURL("/misc/regex")).queryParam("w", "w1-w2;w3,w4").request().get(String.class);
        Assertions.assertEquals("cw1|cw2|cw3|cw4|", response);

        // This following tests verify that MultiValuedParamConverterProvider does not engage on the
        // server side if @Separator has an inappropriate value.
        response = client.target(generateURL("/misc/regex/server/cookie")).request().cookie("p", "p1-p2").get(String.class);
        Assertions.assertEquals("p1-p2|", response);

        response = client.target(generateURL("/misc/regex/server/header")).request().header("p", "p1-p2").get(String.class);
        Assertions.assertEquals("p1-p2|", response);

        response = client.target(generateURL("/misc/regex/server/matrix")).matrixParam("p", "p1-p2").request()
                .get(String.class);
        Assertions.assertEquals("p1-p2|", response);

        response = client.target(generateURL("/misc/regex/server/path/p1-p2")).request().get(String.class);
        Assertions.assertEquals("p1-p2|", response);

        response = client.target(generateURL("/misc/regex/server/query")).queryParam("p", "p1-p2").request().get(String.class);
        Assertions.assertEquals("p1-p2|", response);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    void doTestCookie(String conversion, String format, String separator, String clazz) {
        String t = tag(conversion);
        String response = client.target(generateURL("/cookie/" + conversion + "/" + format + "/" + clazz)).request()
                .cookie("c", "c1" + separator + "c2").get(String.class);
        Assertions.assertEquals(t + "c1|" + t + "c2|", response);
    }

    void doTestHeader(String conversion, String format, String separator, String clazz) {
        String t = tag(conversion);
        String response = client.target(generateURL("/header/" + conversion + "/" + format + "/" + clazz)).request()
                .header("h", "h1" + separator + "h2").get(String.class);
        Assertions.assertEquals(t + "h1|" + t + "h2|", response);
    }

    void doTestMatrix(String conversion, String format, String separator, String clazz) {
        String t = tag(conversion);
        String response = client.target(generateURL("/matrix/" + conversion + "/" + format + "/" + clazz))
                .matrixParam("m", "m1" + separator + "m2").request().get(String.class);
        Assertions.assertEquals(t + "m1|" + t + "m2|", response);
    }

    void doTestPath(String conversion, String format, String separator, String clazz) {
        String t = tag(conversion);
        String response = client
                .target(generateURL("/path/" + conversion + "/" + format + "/" + clazz + "/p1" + separator + "p2")).request()
                .get(String.class);
        Assertions.assertEquals(t + "p1|" + t + "p2|", response);
    }

    void doTestQuery(String conversion, String format, String separator, String clazz) {
        String t = tag(conversion);
        String response = client.target(generateURL("/query/" + conversion + "/" + format + "/" + clazz))
                .queryParam("q", "q1" + separator + "q2").request().get(String.class);
        Assertions.assertEquals(t + "q1|" + t + "q2|", response);
    }

    String tag(String conversion) {
        switch (conversion) {
            case "constructor":
                return "c";
            case "fromString":
                return "f";
            case "headerDelegate":
                return "h";
            case "paramConverter":
                return "p";
            case "valueOf":
                return "v";
            default:
                return "?";
        }
    }
}
