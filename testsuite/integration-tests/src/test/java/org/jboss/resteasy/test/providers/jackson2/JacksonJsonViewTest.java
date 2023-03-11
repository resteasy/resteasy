package org.jboss.resteasy.test.providers.jackson2;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
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

/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.1.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JacksonJsonViewTest {

    private final String ERROR_MESSAGE = "The response entity doesn't contain correctly serialized value";

    @Path("/json_view")
    public interface JacksonViewProxy {

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

    @Before
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JacksonJsonViewTest.class.getSimpleName());
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JacksonJsonViewTest.class.getSimpleName());
        war.addClass(JacksonJsonViewTest.class);
        return TestUtil.finishContainerPrepare(war, null, Something.class, TestJsonView.class, TestJsonView2.class,
                JacksonViewService.class);
    }

    private static ResteasyClient client;

    /**
     * @tpTestDetails Tests Jackson JsonView with jaxr-rs
     * @tpPassCrit The response entity contains correctly serialized values. Jax-rs resource doesn't contain JsonView annotation
     * @tpInfo RESTEASY-1366, JBEAP-5435
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testJacksonProxyJsonViewTest() throws Exception {
        JacksonViewProxy proxy = client.target(generateURL("")).proxy(JacksonViewProxy.class);
        Something p = proxy.getSomething();
        Assert.assertEquals(ERROR_MESSAGE, JacksonViewService.SOMETHING.getAnnotatedValue(), p.getAnnotatedValue());
        Assert.assertEquals(ERROR_MESSAGE, JacksonViewService.SOMETHING.getAnnotatedValue2(), p.getAnnotatedValue2());
        Assert.assertEquals(ERROR_MESSAGE, JacksonViewService.SOMETHING.getNotAnnotatedValue(), p.getNotAnnotatedValue());
    }

    /**
     * @tpTestDetails Tests Jackson JsonView with jaxr-rs
     * @tpPassCrit The response entity contains correctly serialized values. Jax-rs resource contains JsonView annotation
     *             with TestJsonView interface
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testJacksonProxyJsonViewWithJasonViewTest() throws Exception {
        JacksonViewProxy proxy = client.target(generateURL("")).proxy(JacksonViewProxy.class);
        Something p = proxy.getSomethingWithView();
        Assert.assertEquals(ERROR_MESSAGE, JacksonViewService.SOMETHING.getAnnotatedValue(), p.getAnnotatedValue());
        Assert.assertEquals(ERROR_MESSAGE, JacksonViewService.SOMETHING.getAnnotatedValue2(), p.getAnnotatedValue2());
        Assert.assertEquals(ERROR_MESSAGE, JacksonViewService.SOMETHING.getNotAnnotatedValue(), p.getNotAnnotatedValue());
    }

    /**
     * @tpTestDetails Tests Jackson JsonView with jaxr-rs
     * @tpPassCrit The response entity contains correctly serialized values. Jax-rs resource contains JsonView annotation
     *             with TestJsonView2 interface
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testJacksonProxyJsonView2WithJasonViewTest() throws Exception {
        JacksonViewProxy proxy = client.target(generateURL("")).proxy(JacksonViewProxy.class);
        Something p = proxy.getSomethingWithView2();
        Assert.assertNull(ERROR_MESSAGE, p.getAnnotatedValue());
        Assert.assertEquals(ERROR_MESSAGE, JacksonViewService.SOMETHING.getAnnotatedValue2(), p.getAnnotatedValue2());
        Assert.assertEquals(ERROR_MESSAGE, JacksonViewService.SOMETHING.getNotAnnotatedValue(), p.getNotAnnotatedValue());
    }

}
