package org.jboss.resteasy.spi;

import junit.framework.Assert;
import org.junit.Test;

import java.net.URI;

public class ResteasyUriInfoTest {
   @Test
   public void testBaseDirTrailinSlash() throws Exception {
      ResteasyUriInfo uri = new ResteasyUriInfo(new URI("http://localhost:8081/"));

      Assert.assertEquals(uri.getBaseUri().toString(), "http://localhost:8081/");
      Assert.assertEquals("/", uri.getPath().toString());
   }

   @Test
   public void testBaseDirNoTrailinSlash() throws Exception {
      ResteasyUriInfo uri = new ResteasyUriInfo(new URI("http://localhost:8081"));

      Assert.assertEquals(uri.getBaseUri().toString(), "http://localhost:8081/");
      Assert.assertEquals("/", uri.getPath().toString());
   }

   @Test
   public void testWithNoTrailinSlash() throws Exception {
      ResteasyUriInfo uri = new ResteasyUriInfo(new URI("http://localhost:8081/"), new URI("test"));

      Assert.assertEquals(uri.getBaseUri().toString(), "http://localhost:8081/");
      Assert.assertEquals("/test", uri.getPath().toString());
   }

   @Test
   public void testWithTrailinSlash() throws Exception {
      ResteasyUriInfo uri = new ResteasyUriInfo(new URI("http://localhost:8081/"), new URI("test/"));

      Assert.assertEquals(uri.getBaseUri().toString(), "http://localhost:8081/");
      Assert.assertEquals("/test", uri.getPath().toString());
   }

   @Test
   public void testWithNoTrailingSlashAndParams() throws Exception {
      ResteasyUriInfo uri = new ResteasyUriInfo(new URI("http://localhost:8081/"), new URI("/test?param1=value"));

      Assert.assertEquals("http://localhost:8081/", uri.getBaseUri().toString());
      Assert.assertEquals("/test", uri.getPath().toString());
      Assert.assertEquals("{param1=[value]}", uri.getQueryParameters().toString());
   }

   @Test
   public void testWithTrailingSlashAndParams() throws Exception {
      ResteasyUriInfo uri = new ResteasyUriInfo(new URI("http://localhost:8081/"), new URI("/test/?param1=value"));

      Assert.assertEquals("http://localhost:8081/", uri.getBaseUri().toString());
      Assert.assertEquals("/test", uri.getPath().toString());
      Assert.assertEquals("{param1=[value]}", uri.getQueryParameters().toString());
   }

   @Test
   public void testAllConstructors() throws Exception {
      ResteasyUriInfo uri1 = new ResteasyUriInfo(new URI("http://localhost:8081/test?param1=value"));
      ResteasyUriInfo uri2 = new ResteasyUriInfo(new URI("http://localhost:8081/"), new URI("/test?param1=value"));
      ResteasyUriInfo uri3 = new ResteasyUriInfo("http://localhost:8081/test", "param1=value", "/");

      Assert.assertEquals(uri1.getAbsolutePath().toString(), uri2.getAbsolutePath().toString(), uri3.getAbsolutePath().toString());
      Assert.assertEquals(uri1.getBaseUri().toString(), uri2.getBaseUri().toString(), uri3.getBaseUri().toString());
      Assert.assertEquals(uri1.getPath(), uri2.getPath(), uri3.getPath());
   }
}
