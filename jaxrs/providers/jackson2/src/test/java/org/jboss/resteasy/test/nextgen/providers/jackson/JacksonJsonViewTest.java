package org.jboss.resteasy.test.nextgen.providers.jackson;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonView;

public class JacksonJsonViewTest extends BaseResourceTest {

    public interface TestJsonView {

    }

    public interface TestJsonView2 {

    }

    public static class Something {

        @JsonView(TestJsonView.class)
        private String annotatedValue;

        @JsonView({ TestJsonView.class, TestJsonView2.class })
        private String annotatedValue2;

        public Something(){}
        
        public Something(String annotatedValue, String annotatedValue2, String notAnnotatedValue) {
            this.annotatedValue = annotatedValue;
            this.annotatedValue2 = annotatedValue2;
            this.notAnnotatedValue = notAnnotatedValue;
        }

        private String notAnnotatedValue;

        public String getAnnotatedValue() {
            return annotatedValue;
        }

        public void setAnnotatedValue(String annotatedValue) {
            this.annotatedValue = annotatedValue;
        }

        public String getNotAnnotatedValue() {
            return notAnnotatedValue;
        }

        public void setNotAnnotatedValue(String notAnnotatedValue) {
            this.notAnnotatedValue = notAnnotatedValue;
        }

        public String getAnnotatedValue2() {
            return annotatedValue2;
        }

        public void setAnnotatedValue2(String annotatedValue2) {
            this.annotatedValue2 = annotatedValue2;
        }

    }

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

    public static class JacksonViewService implements JacksonViewProxy {

        public static Something SOMETHING = new Something("annotated value", "annotated value 2", "not annotated value");

        @Override
        public Something getSomething() {
            return SOMETHING;
        }

        @Override
        public Something getSomethingWithView() {
            return SOMETHING;

        }

        @Override
        public Something getSomethingWithView2() {
            return SOMETHING;

        }

    }

    private static ResteasyClient client;

    @BeforeClass
    public static void setUp() throws Exception {
        dispatcher.getRegistry().addPerRequestResource(JacksonViewService.class);
        client = new ResteasyClientBuilder().build();
    }

    @AfterClass
    public static void cleanup() {
        client.close();
    }

    @Test
    public void testJacksonProxyJsonViewTest() throws Exception {
        JacksonViewProxy proxy = client.target(generateBaseUrl()).proxy(JacksonViewProxy.class);
        Something p = proxy.getSomething();
        Assert.assertEquals(JacksonViewService.SOMETHING.getAnnotatedValue(), p.getAnnotatedValue());
        Assert.assertEquals(JacksonViewService.SOMETHING.getAnnotatedValue2(), p.getAnnotatedValue2());
        Assert.assertEquals(JacksonViewService.SOMETHING.getNotAnnotatedValue(), p.getNotAnnotatedValue());
    }

    @Test
    public void testJacksonProxyJsonViewWithJasonViewTest() throws Exception {
        JacksonViewProxy proxy = client.target(generateBaseUrl()).proxy(JacksonViewProxy.class);
        Something p = proxy.getSomethingWithView();
        Assert.assertEquals(JacksonViewService.SOMETHING.getAnnotatedValue(), p.getAnnotatedValue());
        Assert.assertEquals(JacksonViewService.SOMETHING.getAnnotatedValue2(), p.getAnnotatedValue2());
        Assert.assertEquals(JacksonViewService.SOMETHING.getNotAnnotatedValue(), p.getNotAnnotatedValue());
    }

    @Test
    public void testJacksonProxyJsonView2WithJasonViewTest() throws Exception {
        JacksonViewProxy proxy = client.target(generateBaseUrl()).proxy(JacksonViewProxy.class);
        Something p = proxy.getSomethingWithView2();
        Assert.assertNull(p.getAnnotatedValue());
        Assert.assertEquals(JacksonViewService.SOMETHING.getAnnotatedValue2(), p.getAnnotatedValue2());
        Assert.assertEquals(JacksonViewService.SOMETHING.getNotAnnotatedValue(), p.getNotAnnotatedValue());
    }

}
