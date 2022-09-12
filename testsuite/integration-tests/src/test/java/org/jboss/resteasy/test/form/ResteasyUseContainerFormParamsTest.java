package org.jboss.resteasy.test.form;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.form.resource.ResteasyUseContainerFormParamsFilter;
import org.jboss.resteasy.test.form.resource.ResteasyUseContainerFormParamsResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * @tpSubChapter Configuration switches
 * @tpChapter Installation/Configuration
 * @tpSince RESTEasy 4.5.3
 * Show use of context parameter resteasy.use.container.form.params
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResteasyUseContainerFormParamsTest {
    private static Client client;
    private static String testSimpleName = ResteasyUseContainerFormParamsTest.class.getSimpleName();
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(testSimpleName);
        war.addClasses(ResteasyUseContainerFormParamsResource.class,
                ResteasyUseContainerFormParamsFilter.class);
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put("resteasy.use.container.form.params","true");
        return TestUtil.finishContainerPrepare(war, contextParam, null);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, testSimpleName);
    }

    @BeforeClass
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    @Test
    public void testForm() throws Exception {
        Builder builder = client.target(generateURL("/form")).request();
        Response response = builder.post(Entity.form(
                new Form("hello", "world").param("yo", "mama")));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
    }
    @Test
    public void testMap() throws Exception {
        Builder builder = client.target(generateURL("/map"))
                .request();
        Response response = builder.post(Entity.form(
                new Form("hello", "world").param("yo", "mama")));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
    }
}
