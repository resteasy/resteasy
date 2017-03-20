package org.jboss.resteasy.test.providers.jettison;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jettison.resource.TypeMappingBean;
import org.jboss.resteasy.test.providers.jettison.resource.TypeMappingResource;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter Jettison provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Type mapping test
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class TypeMappingJettisonTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(TypeMappingJettisonTest.class.getSimpleName());
        war.addAsManifestResource("jboss-deployment-structure-no-jackson.xml", "jboss-deployment-structure.xml");
        war.addClass(TypeMappingBean.class);

        Map<String, String> params = new HashMap<>();
        params.put("resteasy.media.type.mappings", "xml : application/xml, json : application/json");
        return TestUtil.finishContainerPrepare(war, params, TypeMappingResource.class);
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
        return PortProviderUtil.generateURL(path, TypeMappingJettisonTest.class.getSimpleName());
    }

    private void requestAndAssert(String path, String extension, String accept,
                                  String expectedContentType) throws Exception {
        String url = generateURL("/test/" + path);
        if (extension != null) {
            url = url + "." + extension;
        }
        Response response;
        if (accept != null) {
            response = client.target(url).request().header(HttpHeaderNames.ACCEPT, accept).get();
        } else {
            response = client.target(url).request().get();
        }
        assertEquals("Request for " + url + " returned a non-200 status", 200, response.getStatus());
        assertEquals("Request for " + url + " returned an unexpected content type",
                expectedContentType, response.getStringHeaders().getFirst("Content-type"));
        response.close();
    }

    /**
     * @tpTestDetails Test for extensions: xml -> application/xml, json -> application/json
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void extensionTest() throws Exception {
        // acceptXMLOnlyRequestNoProducesNoExtension() throws Exception {
        requestAndAssert("noproduces", null, "application/xml", "application/xml;charset=UTF-8");

        // acceptJSONOnlyRequestNoProducesNoExtension() throws Exception {
        requestAndAssert("noproduces", null, "application/json", "application/json");

        // acceptNullRequestNoProducesJSONExtension() throws Exception {
        requestAndAssert("noproduces", "json", null, "application/json");

        // acceptNullRequestNoProducesXMLExtension() throws Exception {
        requestAndAssert("noproduces", "xml", null, "application/xml;charset=UTF-8");


        // acceptJSONOnlyRequestNoProducesJSONExtension() throws Exception {
        requestAndAssert("noproduces", "json", "application/json", "application/json");

        // acceptJSONOnlyRequestNoProducesXMLExtension() throws Exception {
        requestAndAssert("noproduces", "xml", "application/json", "application/xml;charset=UTF-8");

        // acceptJSONAndXMLRequestNoProducesJSONExtension() throws Exception {
        requestAndAssert("noproduces", "json", "application/json, application/xml",
                "application/json");

        // acceptXMLAndJSONRequestNoProducesJSONExtension() throws Exception {
        requestAndAssert("noproduces", "json", "application/xml, application/json",
                "application/json");

        // acceptXMLOnlyRequestNoProducesXMLExtension() throws Exception {
        requestAndAssert("noproduces", "xml", "application/xml", "application/xml;charset=UTF-8");

        // acceptXMLOnlyRequestNoProducesJSONExtension() throws Exception {
        requestAndAssert("noproduces", "json", "application/xml", "application/json");

        // acceptJSONAndXMLRequestNoProducesXMLExtension() throws Exception {
        requestAndAssert("noproduces", "xml", "application/json, application/xml",
                "application/xml;charset=UTF-8");

        // acceptXMLAndJSONRequestNoProducesXMLExtension() throws Exception {
        requestAndAssert("noproduces", "xml", "application/xml, application/json",
                "application/xml;charset=UTF-8");
    }

}
