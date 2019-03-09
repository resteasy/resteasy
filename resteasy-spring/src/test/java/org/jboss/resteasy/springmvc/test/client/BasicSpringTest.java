package org.jboss.resteasy.springmvc.test.client;

import org.apache.commons.httpclient.HttpException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.plugins.server.undertow.spring.UndertowJaxrsSpringServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class BasicSpringTest {

    UndertowJaxrsSpringServer server;

    @Before
    public void before() {
        server = new UndertowJaxrsSpringServer();
        server.undertowDeployment("classpath:spring-servlet.xml", null);
        server.start();
    }

    @After
    public void after() {
        server.stop();
    }

    @Test
    public void testBasic() throws HttpException, IOException {
        ResteasyClient client = new ResteasyClientBuilderImpl().build();
        ResteasyWebTarget target = client.target(TestPortProvider.generateURL("/basic/"));

        BasicResource basicResource = target.proxy(BasicResource.class);



//      ClientResponse<BasicJaxbObject> result = br.getWrongContentTypeBasicObject();
//      Assert.assertEquals(-1, result.getStatus());
//        Assert.assertEquals("/basic/url", br.getURL());
//
//        Assert.assertEquals("test", br.getBasicString());
//        Assert.assertEquals("something", br.getBasicObject().getSomething());
//
//        Assert.assertEquals("Hi, I'm custom!", br.getSpringMvcValue());
//
//        Assert.assertEquals(1, br.getSingletonCount().intValue());
//        Assert.assertEquals(2, br.getSingletonCount().intValue());
//
//        Assert.assertEquals(1, br.getPrototypeCount().intValue());
//        Assert.assertEquals(1, br.getPrototypeCount().intValue());
//
//        Assert.assertEquals("text/plain", br.getContentTypeHeader());
//
//        Integer interceptorCount = br
//                .getSpringInterceptorCount("afterCompletion");
//
//        Assert.assertEquals(new Integer(9), interceptorCount);
//        Assert.assertEquals("text/plain", br.getContentTypeHeader());
//        Assert.assertEquals("springSomething", br.testSpringXml().getSomething());
//      br.testBogusUrl();
    }
}
