package org.jboss.resteasy.test.providers.jettison;

import org.codehaus.jettison.json.JSONObject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jettison.resource.NamespaceMappingTestExtends;
import org.jboss.resteasy.test.providers.jettison.resource.ObjectFactory;
import org.jboss.resteasy.test.providers.jettison.resource.NamespaceMappingTestBase;
import org.jboss.resteasy.test.providers.jettison.resource.NamespaceMappingResource;
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
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jettison provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Namespace mapping test for jettison provider
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class NamespaceMappingTest {
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(NamespaceMappingTest.class.getSimpleName());
        war.addClasses(NamespaceMappingTestBase.class, NamespaceMappingTestExtends.class,
                NamespaceMappingResource.class, ObjectFactory.class);
        war.addAsManifestResource("jboss-deployment-structure-no-jackson.xml", "jboss-deployment-structure.xml");
        return TestUtil.finishContainerPrepare(war, null, NamespaceMappingResource.class);
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
        return PortProviderUtil.generateURL(path, NamespaceMappingTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test "application/*+json" media type
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJsonReqRes() throws Exception {
        String getData = getDataFromUrl();
        Assert.assertNotNull(getData);
        String postData = postDataToUrl(getData, "application/*+json");
        Assert.assertNotNull(postData);
        new JSONObject(postData);
    }

    private String postDataToUrl(String data, String contentType) throws Exception {
        WebTarget target = client.target(generateURL("/test/v1"));
        Response response = target.request().post(Entity.entity(data, contentType));
        return response.readEntity(String.class);
    }

    private String getDataFromUrl() throws Exception {
        WebTarget target = client.target(generateURL("/test/v1"));
        Response response = target.request().get();
        return response.readEntity(String.class);
    }
}
