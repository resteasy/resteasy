package org.jboss.resteasy.test.resteasy903;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.resteasy903.ForwardServlet;
import org.jboss.resteasy.resteasy903.TestApplication;
import org.jboss.resteasy.resteasy903.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RESTEASY-1049
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Apr 5, 2014
 */
@RunWith(Arquillian.class)
public class FilterTest
{
   private static final Logger log = LoggerFactory.getLogger(FilterTest.class);

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-903.war")
            .addClasses(TestApplication.class, TestResource.class, ForwardServlet.class)
            .addAsWebInfResource("test.html")
            .addAsWebInfResource("web.xml", "web.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }

//   @Test
   public void testDispatchStatic() throws Exception
   {
      log.info("starting testFilter()");
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-903/test/dispatch/static").request();
      Response response = request.get();
      log.info("Status: " + response.getStatus());
      log.info("result: " + response.readEntity(String.class));
      assertEquals(200, response.getStatus());
   }

   @Test
   public void testDispatchDynamic() throws Exception
   {
      log.info("starting testFilter()");
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target("http://localhost:8080/RESTEASY-903/test/dispatch/dynamic").request();
      Response response = request.get();
      String result = response.readEntity(String.class);
      log.info("Status: " + response.getStatus());
      log.info("result: " + result);
      assertEquals(200, response.getStatus());
      assertEquals("forward", result);
   }
}
