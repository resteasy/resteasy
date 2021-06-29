package org.jboss.resteasy.springmvc.test.client;

import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.plugins.server.undertow.spring.UndertowJaxrsSpringServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class BasicSpringTest {

   UndertowJaxrsSpringServer server;
   ResteasyClient client;

   @Before
   public void before() throws Exception {
      server = new UndertowJaxrsSpringServer();

      server.start();

      DeploymentInfo deployment = server.undertowDeployment("classpath:spring-servlet.xml", null);
      deployment.setDeploymentName(BasicSpringTest.class.getName());
      deployment.setContextPath("/");
      deployment.setClassLoader(BasicSpringTest.class.getClassLoader());

      server.deploy(deployment);

      client = new ResteasyClientBuilderImpl().build();
   }

   @After
   public void after() {
      server.stop();
      client.close();
   }

   @Test
   public void testBasic() throws IOException {

      ResteasyWebTarget target = client.target(TestPortProvider.generateURL("/"));

      BasicResource br = target.proxy(BasicResource.class);
      Assert.assertEquals("org/jboss/resteasy/springmvc/test", br.getBasicString());

      Assert.assertEquals("something", br.getBasicObject().getSomething());

      Assert.assertEquals("Hi, I'm custom!", br.getSpringMvcValue());

      Assert.assertEquals(1, br.getSingletonCount().intValue());
      Assert.assertEquals(2, br.getSingletonCount().intValue());

      Assert.assertEquals(1, br.getPrototypeCount().intValue());
      Assert.assertEquals(1, br.getPrototypeCount().intValue());

      Assert.assertEquals("text/plain", br.getContentTypeHeader());

      Integer interceptorCount = br
            .getSpringInterceptorCount("afterCompletion");

      Assert.assertEquals(Integer.valueOf(8), interceptorCount);
      Assert.assertEquals("text/plain", br.getContentTypeHeader());
      Assert.assertEquals("springSomething", br.testSpringXml().getSomething());
   }
}
