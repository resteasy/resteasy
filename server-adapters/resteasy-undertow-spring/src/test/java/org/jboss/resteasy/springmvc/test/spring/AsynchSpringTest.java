package org.jboss.resteasy.springmvc.test.spring;

import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.plugins.server.undertow.spring.UndertowJaxrsSpringServer;
import org.jboss.resteasy.springmvc.test.client.BasicSpringTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AsynchSpringTest {
   private static CountDownLatch latch;

   UndertowJaxrsSpringServer server;
   Client client;

   @Before
   public void before() {
      server = new UndertowJaxrsSpringServer();

      server.start();

      DeploymentInfo deployment = server.undertowDeployment("classpath:spring-test-async-server.xml", null);
      deployment.setDeploymentName(BasicSpringTest.class.getName());
      deployment.setContextPath("/");
      deployment.setClassLoader(BasicSpringTest.class.getClassLoader());

      server.deploy(deployment);
      client = ClientBuilder.newClient();
   }

   @After
   public void after() {
      server.stop();
      client.close();
   }

   @Test
   public void testOneway() throws Exception {
      latch = new CountDownLatch(1);
      WebTarget target = client.target("http://localhost:" + TestPortProvider.getPort() + "?oneway=true");
      long start = System.currentTimeMillis();
      Response response = target.request().put(Entity.entity("content", "text/plain"));
      long end = System.currentTimeMillis() - start;
      Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
      Assert.assertTrue(end < 1000);
      Assert.assertTrue(latch.await(2, TimeUnit.SECONDS));
      response.close();
   }

   @Test
   public void testasync1() throws Exception {
      Response response = null;

      latch = new CountDownLatch(1);
      long start = System.currentTimeMillis();
      response = client.target("http://localhost:" + TestPortProvider.getPort() + "?asynch=true").request().post(Entity.entity("content", "text/plain"));
      long end = System.currentTimeMillis() - start;
      Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
      String jobUrl = response.getHeaderString(HttpHeaders.LOCATION);
      response.close();

      Builder jobBuilder = client.target(jobUrl).request();
      response = jobBuilder.get();
      Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
      Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
      response.close();
      // there's a lag between when the latch completes and the executor
      // registers the completion of the call
      URI oldUri = new URI(jobUrl);
      String existingQueryString = oldUri.getQuery();
      String newQuery = (existingQueryString == null ? "" : "&") + "wait=1000";
      URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(), oldUri.getPath(), newQuery, oldUri.getFragment());
      response = client.target(newUri).request().get();
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      Assert.assertEquals("content", response.readEntity(String.class));

      // test its still there
      response = jobBuilder.get();
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      Assert.assertEquals("content", response.readEntity(String.class));

      // delete and test delete
      response = jobBuilder.delete();
      Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

      response = jobBuilder.get();
      Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
      response.close();

   }

   @Test
   public void testasync2() throws Exception {
      Response response = null;
      AsynchronousDispatcher dispatcher = getDispatcher(server);
      dispatcher.setMaxCacheSize(1);
      latch = new CountDownLatch(1);
      Builder builder = client.target("http://localhost:" + TestPortProvider.getPort() + "?asynch=true").request();
      response = builder.post(Entity.entity("content", "text/plain"));
      Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
      String jobUrl1 = response.getHeaderString(HttpHeaders.LOCATION);
      Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
      response.close();

      latch = new CountDownLatch(1);
      response = builder.post(Entity.entity("content", "text/plain"));
      Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
      String jobUrl2 = response.getHeaderString(HttpHeaders.LOCATION);
      Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
      Assert.assertTrue(!jobUrl1.equals(jobUrl2));
      response.close();

      builder = client.target(jobUrl1).request();
      response = builder.get();
      Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
      response.close();

      // test its still there
      Thread.sleep(1000);
      builder = client.target(jobUrl2).request();
      response = builder.get();
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      Assert.assertEquals("content", response.readEntity(String.class));

      // delete and test delete
      response = builder.delete();
      Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

      response = builder.get();
      Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
      response.close();
   }

   private AsynchronousDispatcher getDispatcher(UndertowJaxrsSpringServer server) throws Exception {
      DispatcherServlet servlet = (DispatcherServlet) server.getManager().getDeployment().getServlets().getManagedServlet(UndertowJaxrsSpringServer.SERVLET_NAME).getServlet().getInstance();
      AsynchronousDispatcher dispatcher = (AsynchronousDispatcher) servlet.getWebApplicationContext().getBean("resteasy.dispatcher");
      return dispatcher;
   }

   @Test
   public void testasync3() throws Exception {
      Response response = null;

      // test readAndRemove
      getDispatcher(server).setMaxCacheSize(10);
      latch = new CountDownLatch(1);
      Builder builder = client.target("http://localhost:" + TestPortProvider.getPort() + "?asynch=true").request();
      response = builder.post(Entity.entity("content", "text/plain"));
      Assert.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
      String jobUrl2 = response.getHeaderString(HttpHeaders.LOCATION);
      Assert.assertTrue(latch.await(3, TimeUnit.SECONDS));
      response.close();

      Thread.sleep(1000);

      // test its still there
      builder = client.target(jobUrl2).request();
      response = builder.post(Entity.entity("content", "text/plain"));
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      Assert.assertEquals("content", response.readEntity(String.class));

      builder = client.target(jobUrl2).request();
      response = builder.get();
      Assert.assertEquals(HttpServletResponse.SC_GONE, response.getStatus());
      response.close();

   }

   @Path("/")
   public static class MyResource {
      @POST
      public String post(String content) throws Exception {
         Thread.sleep(1500);
         latch.countDown();

         return content;
      }

      @PUT
      public void put(String content) throws Exception {
         Assert.assertEquals("content", content);
         Thread.sleep(500);
         latch.countDown();
      }
   }
}
