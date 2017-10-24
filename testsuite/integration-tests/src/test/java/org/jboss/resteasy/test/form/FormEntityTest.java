package org.jboss.resteasy.test.form;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.form.resource.FormEntityResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class FormEntityTest {

    private static Client client;
    
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(FormEntityTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, FormEntityResource.class);
    }

    private String generateURL(String path) {
       return PortProviderUtil.generateURL(path, FormEntityTest.class.getSimpleName());
    }

    @BeforeClass
    public static void before() throws Exception {
       client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
       client.close();
    }

    /**
     * @tpTestDetails Retrieve form param and form entity
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testWithEqualsAndEmptyString() throws Exception
    {
       Invocation.Builder request = client.target(generateURL("/test/form")).request();
       Response response = request.post(Entity.entity("fp=abc&fp2=\"\"", "application/x-www-form-urlencoded"));
       String s = response.readEntity(String.class);
       Assert.assertEquals(200, response.getStatus());
       Assert.assertTrue(s.equals("abc|fp=abc&fp2=\"\"") || s.equals("abc|fp2=\"\"&fp=abc"));
    }
    
    /**
     * @tpTestDetails Retrieve form param and form entity
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testWithEquals() throws Exception
    {
       Invocation.Builder request = client.target(generateURL("/test/form")).request();
       Response response = request.post(Entity.entity("fp=abc&fp2=", "application/x-www-form-urlencoded"));
       String s = response.readEntity(String.class);
       Assert.assertEquals(200, response.getStatus());
       Assert.assertTrue(s.equals("abc|fp=abc&fp2") || s.equals("abc|fp2&fp=abc"));
    }
    
    /**
     * @tpTestDetails Retrieve form param and form entity
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testWithoutEquals() throws Exception
    {
       Invocation.Builder request = client.target(generateURL("/test/form")).request();
       Response response = request.post(Entity.entity("fp=abc&fp2", "application/x-www-form-urlencoded"));
       String s = response.readEntity(String.class);
       Assert.assertEquals(200, response.getStatus());
       Assert.assertTrue(s.equals("abc|fp=abc&fp2") || s.equals("abc|fp2&fp=abc"));
    }
}
