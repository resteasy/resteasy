package org.jboss.resteasy.plugins.providers.html.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static org.hamcrest.CoreMatchers.containsString;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class TestHtmlClient
{

   private static Client client;

   @BeforeClass
   public static void setUpClass()
   {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void tearDownClass()
   {
      client.close();
   }

   @Test
   public void testRedirect() throws Exception
   {
      ClientRequest req = new ClientRequest("http://localhost:9095/test/hello/world").followRedirects(true);
      ClientResponse res = req.accept(MediaType.TEXT_HTML).get();

      String html = (String) res.getEntity(String.class);
      Assert.assertThat(html, containsString("<h2>Hello world!</h2>"));

      res.close();
   }

   @Test
   public void testText()
   {
      WebTarget target = client.target("http://localhost:9095/test/hello");
      String text = target.request(MediaType.TEXT_PLAIN).get(String.class);
      Assert.assertEquals("Hello world!", text);
   }

   @Test
   public void testHtml()
   {
      WebTarget target = client.target("http://localhost:9095/test/hello");
      String html = target.request(MediaType.TEXT_HTML).get(String.class);
      Assert.assertThat(html, containsString("<h2>Hello world!</h2>"));
   }

   @Test
   public void testXHtml()
   {
      WebTarget target = client.target("http://localhost:9095/test/hello");
      String xhtml = target.request(MediaType.APPLICATION_XHTML_XML).get(String.class);

      Assert.assertThat(xhtml, containsString("<h2>Hello world!</h2>"));
   }

   @Test
   public void testXHtmlViewable()
   {
      WebTarget target = client.target("http://localhost:9095/test/hello/viewable");
      Response response = target.request(MediaType.APPLICATION_XHTML_XML).get();

      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("X-VALUE", response.getHeaderString("X-KEY"));

      String xhtml = response.readEntity(String.class);
      Assert.assertThat(xhtml, containsString("<h2>Hello world!</h2>"));

      response.close();
   }
}
