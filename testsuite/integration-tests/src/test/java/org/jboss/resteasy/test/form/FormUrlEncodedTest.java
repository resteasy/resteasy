package org.jboss.resteasy.test.form;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.test.form.resource.FormUrlEncodedResource;
import org.jboss.resteasy.util.HttpResponseCodes;
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
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class FormUrlEncodedTest {

    private static Client client;
    
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(FormUrlEncodedTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, FormUrlEncodedResource.class);
    }

    private String generateURL(String path) {
       return PortProviderUtil.generateURL(path, FormUrlEncodedTest.class.getSimpleName());
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
     * @tpTestDetails Get form parameter from resource using InputStream and StreamingOutput
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPost() {
        WebTarget base = client.target(generateURL("/simple"));
        Response response = base.request().post(Entity.form(new Form().param("hello", "world")));

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String body = response.readEntity(String.class);
        Assert.assertEquals("Wrong response content", "hello=world", body);

        response.close();
    }
    
    /**
     * @tpTestDetails Send form with an empty parameter value.
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testResteasy109() {
       Builder builder = client.target(generateURL("/RESTEASY-109")).request();
       Response response = null;
       try
       {
          response = builder.post(Entity.entity("name=jon&address1=123+Main+St&address2=&zip=12345", MediaType.APPLICATION_FORM_URLENCODED));
          Assert.assertEquals(204, response.getStatus());
       }
       catch (Exception e)
       {
          throw new RuntimeException(e);
       }
       finally
       {
          response.close();
       }
    }

    /**
     * @tpTestDetails Send form with a missing query parameter.
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testQueryParamIsNull() {
       Builder builder = client.target(generateURL("/simple")).request();
       try
       {
          Response response = builder.post(Entity.form(new Form("hello", "world")));
          Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
          Assert.assertEquals("hello=world", response.readEntity(String.class));
       }
       catch (Exception e)
       {
          throw new RuntimeException(e);
       }
    }
    
    /**
     * @tpTestDetails Send form with two parameters.
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testPostTwoParameters() {
       Builder builder = client.target(generateURL("/form/twoparams")).request();
       try
       {
          Response response = builder.post(Entity.form(new Form("hello", "world").param("yo", "mama")));
          Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
          String body = response.readEntity(String.class);
          Assert.assertTrue(body.indexOf("hello=world") != -1);
          Assert.assertTrue(body.indexOf("yo=mama") != -1);
       }
       catch (Exception e)
       {
          throw new RuntimeException(e);
       }
    }
    
    @Path("/")
    public interface TestProxy {
       @Path("/form")
       @POST
       @Produces("application/x-www-form-urlencoded")
       @Consumes("application/x-www-form-urlencoded")
       String post(MultivaluedMap<String, String> form);

       @Path("/form")
       @POST
       @Produces("application/x-www-form-urlencoded")
       @Consumes("application/x-www-form-urlencoded")
       MultivaluedMap<String, String> post2(MultivaluedMap<String, String> form);
    }

    /**
     * @tpTestDetails Send form by proxy.
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testProxy() {
       ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateURL(""));
       TestProxy proxy = target.proxy(TestProxy.class);
       MultivaluedMapImpl<String, String> form = new MultivaluedMapImpl<String, String>();
       form.add("hello", "world");
       String body = proxy.post(form);
       Assert.assertEquals("hello=world", body);

       MultivaluedMap<String, String> rtn = proxy.post2(form);
       Assert.assertEquals(rtn.getFirst("hello"), "world");
    }
}
