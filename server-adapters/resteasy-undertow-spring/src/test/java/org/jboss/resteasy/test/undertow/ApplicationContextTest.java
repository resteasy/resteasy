package org.jboss.resteasy.test.undertow;

import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.plugins.server.undertow.spring.UndertowJaxrsSpringServer;
import org.jboss.resteasy.springmvc.test.client.BasicSpringTest;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApplicationContextTest {

   @Test
   public void testContextLoading() {
      ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-test-async-server.xml");
      AsynchronousDispatcher dispatcher = (AsynchronousDispatcher) context.getBean("resteasy.dispatcher");
      assertNotNull(dispatcher);
   }

   @Test
   public void testServer() throws Exception {
      UndertowJaxrsSpringServer server = new UndertowJaxrsSpringServer();
      server.start();
      try {
         DeploymentInfo deployment = server.undertowDeployment("classpath:spring-test-async-server.xml", null);
         deployment.setDeploymentName(BasicSpringTest.class.getName());
         deployment.setContextPath("/");
         deployment.setClassLoader(BasicSpringTest.class.getClassLoader());

         server.deploy(deployment);

         DispatcherServlet servlet = (DispatcherServlet) server.getManager().getDeployment().getServlets().getManagedServlet("ResteasyServlet").getServlet().getInstance();
         assertNotNull(servlet);

         DispatcherServlet servlet2 = (DispatcherServlet) server.getManager().getDeployment().getServlets().getManagedServlet("ResteasyServlet").getServlet().getInstance();
         assertEquals(servlet, servlet2);

         AsynchronousDispatcher dispatcher = (AsynchronousDispatcher) servlet.getWebApplicationContext().getBean("resteasy.dispatcher");
         assertNotNull(dispatcher);
      } finally {
         server.stop();
      }
   }

}
