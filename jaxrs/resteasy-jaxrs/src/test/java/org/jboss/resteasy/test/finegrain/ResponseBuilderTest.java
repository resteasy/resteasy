package org.jboss.resteasy.test.finegrain;

import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class ResponseBuilderTest
{
   private static final URI BASE_URI = URI.create("http://localhost/");
   private static final URI REQUEST_URI = URI
           .create("http://localhost/path/to/resource");

   private Response.ResponseBuilder builder;

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
   public void testAllowed()
   {
      Response response = Response.status(Response.Status.OK).allow("GET", "POST", "DELETE").build();
      Set<String> allowedMethods = response.getAllowedMethods();
      Assert.assertEquals(allowedMethods.size(), 3);
      Assert.assertTrue(allowedMethods.contains("GET"));
      Assert.assertTrue(allowedMethods.contains("POST"));
      Assert.assertTrue(allowedMethods.contains("DELETE"));
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

   @Test
   public void testReplace()
   {
      String[] headers = { "header1", "header2", "header3" };
      Response response = Response.ok().header(headers[0], headers[0])
              .header(headers[1], headers[1]).header(headers[2], headers[2])
              .replaceAll(null).build();
      for (String header : headers)
         Assert.assertTrue(response.getHeaderString(header) == null);

   }

   protected enum Request {
      GET, PUT, POST, HEAD, OPTIONS, DELETE, TRACE
   }
   @Test
   public void allowStringArrayTruncateDuplicatesTest()
   {
      String[] methods = { Request.OPTIONS.name(), Request.OPTIONS.name() };
      Response.ResponseBuilder rb = RuntimeDelegate.getInstance()
              .createResponseBuilder();
      Response response = rb.allow(methods).build();
      Set<String> set = response.getAllowedMethods();
      Assert.assertEquals(1, set.size());
      Assert.assertEquals(set.iterator().next(), Request.OPTIONS.name());
   }


}
