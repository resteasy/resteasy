package org.jboss.resteasy.test.providers.jackson2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonViewService;
import org.jboss.resteasy.test.providers.jackson2.resource.Something;
import org.jboss.resteasy.test.providers.jackson2.resource.TestJsonView;
import org.jboss.resteasy.test.providers.jackson2.resource.TestJsonView2;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.annotation.JsonView;

@RunWith(Arquillian.class)
@RunAsClient
public class JacksonJsonViewTest {

   @Path("/json_view")
   public interface JacksonViewProxy
   {

      @GET
      @Produces("application/json")
      @Path("/something")
      Something getSomething();

      @GET
      @Produces("application/json")
      @JsonView(TestJsonView.class)
      @Path("/something_w_view")
      Something getSomethingWithView();

      @GET
      @Produces("application/json")
      @JsonView(TestJsonView2.class)
      @Path("/something_w_view2")
      Something getSomethingWithView2();

   }
   
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JacksonJsonViewTest.class.getSimpleName());
        war.addClass(JacksonJsonViewTest.class);
        return TestUtil.finishContainerPrepare(war, null, Something.class, TestJsonView.class, TestJsonView2.class, JacksonViewService.class);
    }

    
    private static ResteasyClient client;

    @Test
    public void testJacksonProxyJsonViewTest() throws Exception {
        JacksonViewProxy proxy = client.target(generateURL("")).proxy(JacksonViewProxy.class);
        Something p = proxy.getSomething();
        Assert.assertEquals(JacksonViewService.SOMETHING.getAnnotatedValue(), p.getAnnotatedValue());
        Assert.assertEquals(JacksonViewService.SOMETHING.getAnnotatedValue2(), p.getAnnotatedValue2());
        Assert.assertEquals(JacksonViewService.SOMETHING.getNotAnnotatedValue(), p.getNotAnnotatedValue());
    }

    @Test
    public void testJacksonProxyJsonViewWithJasonViewTest() throws Exception {
        JacksonViewProxy proxy = client.target(generateURL("")).proxy(JacksonViewProxy.class);
        Something p = proxy.getSomethingWithView();
        Assert.assertEquals(JacksonViewService.SOMETHING.getAnnotatedValue(), p.getAnnotatedValue());
        Assert.assertEquals(JacksonViewService.SOMETHING.getAnnotatedValue2(), p.getAnnotatedValue2());
        Assert.assertEquals(JacksonViewService.SOMETHING.getNotAnnotatedValue(), p.getNotAnnotatedValue());
    }

    @Test
    public void testJacksonProxyJsonView2WithJasonViewTest() throws Exception {
        JacksonViewProxy proxy = client.target(generateURL("")).proxy(JacksonViewProxy.class);
        Something p = proxy.getSomethingWithView2();
        Assert.assertNull(p.getAnnotatedValue());
        Assert.assertEquals(JacksonViewService.SOMETHING.getAnnotatedValue2(), p.getAnnotatedValue2());
        Assert.assertEquals(JacksonViewService.SOMETHING.getNotAnnotatedValue(), p.getNotAnnotatedValue());
    }
    
    
    
    
    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JacksonJsonViewTest.class.getSimpleName());
    }

//    /**
//     * @tpTestDetails Tests usage of proxied subresource
//     * @tpPassCrit The resource returns Success response
//     * @tpSince RESTEasy 3.0.16
//     */
//    @Test
//    public void testProxyWithGenericReturnType() throws Exception {
//        WebTarget target = client.target(generateURL("/test/one/"));
//        logger.info("Sending request");
//        Response response = target.request().get();
//        String entity = response.readEntity(String.class);
//        logger.info("Received response: " + entity);
//        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
//        Assert.assertTrue("Type property is missing.", entity.contains("type"));
//        response.close();
//
//        target = client.target(generateURL("/test/list/"));
//        logger.info("Sending request");
//        response = target.request().get();
//        entity = response.readEntity(String.class);
//        logger.info("Received response: " + entity);
//        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
//        Assert.assertTrue("Type property is missing.", entity.contains("type"));
//        response.close();
//    }
}
