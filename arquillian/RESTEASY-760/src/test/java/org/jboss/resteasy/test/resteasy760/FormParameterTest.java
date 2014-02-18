package org.jboss.resteasy.test.resteasy760;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy760.TestApplication;
import org.jboss.resteasy.resteasy760.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @author Achim Bitzer
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 3, 2012
 */
@RunWith(Arquillian.class)
@RunAsClient
public class FormParameterTest
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-760.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(FormParameterTest.class)
            .addAsWebInfResource("web.xml");
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testFormParamWithNoQueryParamPut() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-760/put/noquery/");
      request.formParameter("formParam", "abc xyz");
      request.header("Content-Type", "application/x-www-form-urlencoded");
      ClientResponse<String> response = request.put(String.class);
      assertTrue(response != null);
      System.out.println("response: " + response.getEntity());
      assertEquals("abc xyz", response.getEntity());
   }

   @Test
   public void testFormParamWithNoQueryParamPutEncoded() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-760/put/noquery/encoded");
      request.formParameter("formParam", "abc xyz");
      request.header("Content-Type", "application/x-www-form-urlencoded");
      ClientResponse<String> response = request.put(String.class);
      assertTrue(response != null);
      System.out.println("response: " + response.getEntity());
      assertEquals("abc%20xyz", response.getEntity());
   }

   @Test
   public void testFormParamWithNoQueryParamPost() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-760/post/noquery/");
      request.formParameter("formParam", "abc xyz");
      request.header("Content-Type", "application/x-www-form-urlencoded");
      ClientResponse<String> response = request.post(String.class);
      assertTrue(response != null);
      System.out.println("response: " + response.getEntity());
      assertEquals("abc xyz", response.getEntity());
   }

   @Test
   public void testFormParamWithNoQueryParamPostEncoded() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-760/post/noquery/encoded");
      request.formParameter("formParam", "abc xyz");
      request.header("Content-Type", "application/x-www-form-urlencoded");
      ClientResponse<String> response = request.post(String.class);
      assertTrue(response != null);
      System.out.println("response: " + response.getEntity());
      assertEquals("abc%20xyz", response.getEntity());
   }

   @Test
   public void testFormParamWithQueryParamPut() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-760/put/query?query=xyz");
      request.formParameter("formParam", "abc xyz");
      request.header("Content-Type", "application/x-www-form-urlencoded");
      ClientResponse<String> response = request.put(String.class);
      assertTrue(response != null);
      System.out.println("response: " + response.getEntity());
      assertEquals("abc xyz", response.getEntity());
   }

   @Test
   public void testFormParamWithQueryParamPutEncoded() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-760/put/query/encoded?query=xyz");
      request.formParameter("formParam", "abc xyz");
      request.header("Content-Type", "application/x-www-form-urlencoded");
      ClientResponse<String> response = request.put(String.class);
      assertTrue(response != null);
      System.out.println("response: " + response.getEntity());
      assertEquals("abc%20xyz", response.getEntity());
   }

   @Test
   public void testFormParamWithQueryParamPost() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-760/post/query?query=xyz");
      request.formParameter("formParam", "abc xyz");
      request.header("Content-Type", "application/x-www-form-urlencoded");
      ClientResponse<String> response = request.post(String.class);
      assertTrue(response != null);
      System.out.println("response: " + response.getEntity());
      assertEquals("abc xyz", response.getEntity());
   }

   @Test
   public void testFormParamWithQueryParamPostEncoded() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-760/post/query/encoded?query=xyz");
      request.formParameter("formParam", "abc xyz");
      request.header("Content-Type", "application/x-www-form-urlencoded");
      ClientResponse<String> response = request.post(String.class);
      assertTrue(response != null);
      System.out.println("response: " + response.getEntity());
      assertEquals("abc%20xyz", response.getEntity());
   }
}
