package org.jboss.resteasy.test.finegrain;

import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

public class ResponseBuilderTest
{
   private static final URI BASE_URI = URI.create("http://localhost/");
   private static final URI REQUEST_URI = URI
           .create("http://localhost/path/to/resource");

   private ResponseBuilderImpl builder;

   @Before
   public void before() throws URISyntaxException
   {
      HttpRequest httpRequest = MockHttpRequest.create("GET", REQUEST_URI,
              BASE_URI);

      ResteasyProviderFactory.getContextDataMap().put(HttpRequest.class,
              httpRequest);

      builder = new ResponseBuilderImpl();
   }

   @After
   public void after() throws Exception
   {
      ResteasyProviderFactory.removeContextDataLevel();
   }

   @Test
   public void testLocationSimple()
   {
      Response r = builder.location(URI.create("/res")).build();
      String actualUri = r.getMetadata().getFirst("Location").toString();

      Assert.assertEquals("http://localhost/res", actualUri);
   }

   @Test
   public void testLocationPath()
   {
      Response r = builder.location(URI.create("/a/res")).build();
      String actualUri = r.getMetadata().getFirst("Location").toString();

      Assert.assertEquals("http://localhost/a/res", actualUri);
   }

   @Test
   public void testLocationQueryString()
   {
      Response r = builder.location(URI.create("/res?query")).build();
      String actualUri = r.getMetadata().getFirst("Location").toString();

      Assert.assertEquals("http://localhost/res?query", actualUri);
   }

   @Test
   public void testLocationFragment()
   {
      Response r = builder.location(URI.create("/res#frag")).build();
      String actualUri = r.getMetadata().getFirst("Location").toString();

      Assert.assertEquals("http://localhost/res#frag", actualUri);
   }

   @Test
   public void testContentLocationSimple()
   {
      Response r = builder.contentLocation(URI.create("/res")).build();
      String actualUri = r.getMetadata().getFirst("Content-Location").toString();

      Assert.assertEquals("http://localhost/res", actualUri);
   }

   @Test
   public void testContentLocationPath()
   {
      Response r = builder.contentLocation(URI.create("/a/res")).build();
      String actualUri = r.getMetadata().getFirst("Content-Location").toString();

      Assert.assertEquals("http://localhost/a/res", actualUri);
   }

   @Test
   public void testContentLocationQueryString()
   {
      Response r = builder.location(URI.create("/res?query")).build();
      String actualUri = r.getMetadata().getFirst("Location").toString();

      Assert.assertEquals("http://localhost/res?query", actualUri);
   }

   @Test
   public void testContentLocationFragment()
   {
      Response r = builder.contentLocation(URI.create("/res#frag")).build();
      String actualUri = r.getMetadata().getFirst("Content-Location").toString();

      Assert.assertEquals("http://localhost/res#frag", actualUri);
   }
}
