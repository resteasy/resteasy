package org.jboss.resteasy.tests;

import junit.framework.Assert;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApplicationTest
{

   @Test
   public void testExplicitA() throws Exception
   {
      Client client = ClientBuilder.newClient();
      String value = null;
      WebTarget base = client.target("http://localhost:8080/application-test/a/explicit");

      value = base.path("resources/a").request().get(String.class);
      Assert.assertEquals("a", value);

      Response response = base.path("resources/b").request().get();
      Assert.assertEquals(404, response.getStatus());
      client.close();
   }

   @Test
   public void testExplicitB() throws Exception
   {
      Client client = ClientBuilder.newClient();
      String value = null;
      WebTarget base = client.target("http://localhost:8080/application-test/b/explicit");

      value = base.path("resources/b").request().get(String.class);
      Assert.assertEquals("b", value);

      Response response = base.path("resources/a").request().get();
      Assert.assertEquals(404, response.getStatus());
      client.close();
   }

   @Test
   public void testScanned() throws Exception
   {
      Client client = ClientBuilder.newClient();
      String value = null;
      WebTarget base = client.target("http://localhost:8080/application-test/scanned");

      value = base.path("resources/a").request().get(String.class);
      Assert.assertEquals("a", value);
      value = base.path("resources/b").request().get(String.class);
      Assert.assertEquals("b", value);
   }

   @Test
   public void testMapped() throws Exception
   {
      Client client = ClientBuilder.newClient();
      String value = null;
      WebTarget base = client.target("http://localhost:8080/application-test/mapped");

      value = base.path("resources/a").request().get(String.class);
      Assert.assertEquals("a", value);
      value = base.path("resources/b").request().get(String.class);
      Assert.assertEquals("b", value);
   }



}
