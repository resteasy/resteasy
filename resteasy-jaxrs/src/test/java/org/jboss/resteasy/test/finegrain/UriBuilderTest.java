package org.jboss.resteasy.test.finegrain;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriBuilderTest
{
   @BeforeClass
   public static void before()
   {
      ResteasyProviderFactory.setInstance(new ResteasyProviderFactory());
   }

   @Test
   public void testEmoji()
   {
      UriBuilder builder = UriBuilder.fromPath("/my/url");
      builder.queryParam("msg", "emoji stuff %EE%81%96%EE%90%8F");
      URI uri = builder.build();
      System.out.println(uri);
      Assert.assertEquals("/my/url?msg=emoji%20stuff%20%EE%81%96%EE%90%8F", uri.toString());

   }

   @Test
   public void testQuery()
   {
      UriBuilder builder = UriBuilder.fromPath("/foo");
      builder.queryParam("mama", "   ");
      Assert.assertEquals(builder.build().toString(), "/foo?mama=+++");
   }

   @Test
   public void testQuery2()
   {
      UriBuilder builder = UriBuilder.fromUri("http://localhost/test");
      builder.replaceQuery("a={b}");
      URI uri = builder.build("=");
      Assert.assertEquals(uri.toString(), "http://localhost/test?a=%3D");
   }

   @Test
   public void testReplaceScheme()
   {
      URI bu = UriBuilder.fromUri("http://localhost:8080/a/b/c").
              scheme("https").build();
      Assert.assertEquals(URI.create("https://localhost:8080/a/b/c"), bu);
   }

   @Test
   public void testReplaceUserInfo()
   {
      URI bu = UriBuilder.fromUri("http://bob@localhost:8080/a/b/c").
              userInfo("sue").build();
      Assert.assertEquals(URI.create("http://sue@localhost:8080/a/b/c"), bu);
   }

   @Test
   public void testReplaceHost()
   {
      URI bu = UriBuilder.fromUri("http://localhost:8080/a/b/c").
              host("a.com").build();
      Assert.assertEquals(URI.create("http://a.com:8080/a/b/c"), bu);
   }

   @Test
   public void testReplacePort()
   {
      URI bu = UriBuilder.fromUri("http://localhost:8080/a/b/c").
              port(9090).build();
      Assert.assertEquals(URI.create("http://localhost:9090/a/b/c"), bu);

      bu = UriBuilder.fromUri("http://localhost:8080/a/b/c").
              port(-1).build();
      Assert.assertEquals(URI.create("http://localhost/a/b/c"), bu);
   }

   @Test
   public void testReplacePath()
   {
      URI bu = UriBuilder.fromUri("http://localhost:8080/a/b/c").
              replacePath("/x/y/z").build();
      Assert.assertEquals(URI.create("http://localhost:8080/x/y/z"), bu);
   }

   @Test
   public void testReplaceMatrixParam()
   {
      URI bu = UriBuilder.fromUri("http://localhost:8080/a/b/c;a=x;b=y").
              replaceMatrix("x=a;y=b").build();
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c;x=a;y=b"), bu);
      UriBuilder builder = UriBuilder.fromUri("http://localhost:8080/a").path("/{b:A{0:10}}/c;a=x;b=y");
      builder.replaceMatrixParam("a", "1", "2");
      bu = builder.build("b");
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c;b=y;a=1;a=2"), bu);
   }

   @Test
   public void testReplaceQueryParams()
   {
      URI bu = UriBuilder.fromUri("http://localhost:8080/a/b/c?a=x&b=y").
              replaceQuery("x=a&y=b").build();
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c?x=a&y=b"), bu);

      UriBuilder builder = UriBuilder.fromUri("http://localhost:8080/a/b/c?a=x&b=y");
      builder.replaceQueryParam("a", "1", "2");
      bu = builder.build();
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c?b=y&a=1&a=2"), bu);

   }

   @Test
   public void testReplaceFragment()
   {
      URI bu = UriBuilder.fromUri("http://localhost:8080/a/b/c?a=x&b=y#frag").
              fragment("ment").build();
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c?a=x&b=y#ment"), bu);
   }


   @Test
   public void testReplaceUri()
   {
      URI u = URI.create("http://bob@localhost:8080/a/b/c?a=x&b=y#frag");

      URI bu = UriBuilder.fromUri(u).
              uri(URI.create("https://bob@localhost:8080")).build();
      Assert.assertEquals(URI.create("https://bob@localhost:8080/a/b/c?a=x&b=y#frag"), bu);

      bu = UriBuilder.fromUri(u).
              uri(URI.create("https://sue@localhost:8080")).build();
      Assert.assertEquals(URI.create("https://sue@localhost:8080/a/b/c?a=x&b=y#frag"), bu);

      bu = UriBuilder.fromUri(u).
              uri(URI.create("https://sue@localhost:9090")).build();
      Assert.assertEquals(URI.create("https://sue@localhost:9090/a/b/c?a=x&b=y#frag"), bu);

      bu = UriBuilder.fromUri(u).
              uri(URI.create("/x/y/z")).build();
      Assert.assertEquals(URI.create("http://bob@localhost:8080/x/y/z?a=x&b=y#frag"), bu);

      bu = UriBuilder.fromUri(u).
              uri(URI.create("?x=a&b=y")).build();
      Assert.assertEquals(URI.create("http://bob@localhost:8080/a/b/c?x=a&b=y#frag"), bu);

      bu = UriBuilder.fromUri(u).
              uri(URI.create("#ment")).build();
      Assert.assertEquals(URI.create("http://bob@localhost:8080/a/b/c?a=x&b=y#ment"), bu);
   }

   @Test
   public void testSchemeSpecificPart()
   {
      URI u = URI.create("http://bob@localhost:8080/a/b/c?a=x&b=y#frag");

      URI bu = UriBuilder.fromUri(u).
              schemeSpecificPart("//sue@remotehost:9090/x/y/z?x=a&y=b").build();
      Assert.assertEquals(URI.create("http://sue@remotehost:9090/x/y/z?x=a&y=b#frag"), bu);
   }

   @Test
   public void testAppendPath()
   {
      URI bu = UriBuilder.fromUri("http://localhost:8080/a/b/c/").
              path("/").build();
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c/"), bu);

      bu = UriBuilder.fromUri("http://localhost:8080/a/b/c/").
              path("/x/y/z").build();
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c/x/y/z"), bu);

      bu = UriBuilder.fromUri("http://localhost:8080/a/b/c").
              path("/x/y/z").build();
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c/x/y/z"), bu);

      bu = UriBuilder.fromUri("http://localhost:8080/a/b/c").
              path("x/y/z").build();
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c/x/y/z"), bu);

      bu = UriBuilder.fromUri("http://localhost:8080/a/b/c").
              path("/").build();
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c/"), bu);

      bu = UriBuilder.fromUri("http://localhost:8080/a/b/c").
              path("").build();
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c"), bu);

      bu = UriBuilder.fromUri("http://localhost:8080/a%20/b%20/c%20").
              path("/x /y /z ").build();
      Assert.assertEquals(URI.create("http://localhost:8080/a%20/b%20/c%20/x%20/y%20/z%20"), bu);
   }

   @Test
   public void testAppendQueryParams()
   {
      URI bu = UriBuilder.fromUri("http://localhost:8080/a/b/c?a=x&b=y").
              queryParam("c", "z").build();
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c?a=x&b=y&c=z"), bu);
   }

   @Test
   public void testQueryParamsEncoding()
   {
      URI bu = UriBuilder.fromUri("http://localhost:8080/a/b/c?a=x&b=y").
              queryParam("c", "z=z/z").build();
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c?a=x&b=y&c=z%3Dz%2Fz"), bu);
   }

   @Test
   public void testAppendMatrixParams()
   {
      URI bu = UriBuilder.fromUri("http://localhost:8080/a/b/c;a=x;b=y").
              matrixParam("c", "z").build();
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c;a=x;b=y;c=z"), bu);
   }

   public void testAppendPathAndMatrixParams()
   {
      URI bu = UriBuilder.fromUri("http://localhost:8080/").
              path("a").matrixParam("x", "foo").matrixParam("y", "bar").
              path("b").matrixParam("x", "foo").matrixParam("y", "bar").build();
      Assert.assertEquals(URI.create("http://localhost:8080/a;x=foo;y=bar/b;x=foo;y=bar"), bu);
   }

   @Path("resource")
   class Resource
   {
      @Path("method")
      public
      @GET
      String get()
      {
         return "";
      }

      @Path("locator")
      public Object locator()
      {
         return null;
      }
   }

   @Test
   public void testResourceAppendPath() throws NoSuchMethodException
   {
      URI ub = UriBuilder.fromUri("http://localhost:8080/base").
              path(Resource.class).build();
      Assert.assertEquals(URI.create("http://localhost:8080/base/resource"), ub);

      ub = UriBuilder.fromUri("http://localhost:8080/base").
              path(Resource.class, "get").build();
      Assert.assertEquals(URI.create("http://localhost:8080/base/method"), ub);

      Method get = Resource.class.getMethod("get");
      Method locator = Resource.class.getMethod("locator");
      ub = UriBuilder.fromUri("http://localhost:8080/base").
              path(get).path(locator).build();
      Assert.assertEquals(URI.create("http://localhost:8080/base/method/locator"), ub);
   }

   @Test
   public void testTemplates()
   {
      URI bu = UriBuilder.fromUri("http://localhost:8080/a/b/c").
              path("/{foo}/{bar}/{baz}/{foo}").build("x", "y", "z");
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c/x/y/z/x"), bu);

      Map<String, Object> m = new HashMap<String, Object>();
      m.put("foo", "x");
      m.put("bar", "y");
      m.put("baz", "z");
      bu = UriBuilder.fromUri("http://localhost:8080/a/b/c").
              path("/{foo}/{bar}/{baz}/{foo}").buildFromMap(m);
      Assert.assertEquals(URI.create("http://localhost:8080/a/b/c/x/y/z/x"), bu);
   }

   @Test
   public void testClone()
   {
      UriBuilder ub = UriBuilder.fromUri("http://user@localhost:8080/?query#fragment").path("a");
      URI full = ub.clone().path("b").build();
      URI base = ub.build();

      Assert.assertEquals(URI.create("http://user@localhost:8080/a?query#fragment"), base);
      Assert.assertEquals(URI.create("http://user@localhost:8080/a/b?query#fragment"), full);
   }

   /**
    * Regression test for RESTEASY-102
    */
   @Test
   public void testResteasy102()
   {
      UriBuilder ub = UriBuilder.fromPath("foo+bar");
      Assert.assertEquals("foo%2Bbar", ub.build().toString());

   }

   /*
   * Create an UriBuilder instance using
   *                 uriBuilder.fromUri(String)
   */
   @Test
   public void FromUriTest3() throws Exception
   {
      StringBuffer sb = new StringBuffer();
      boolean pass = true;
      URI uri;

      String[] uris = {
              "mailto:java-net@java.sun.com",
              "ftp://ftp.is.co.za/rfc/rfc1808.txt", "news:comp.lang.java",
              "urn:isbn:096139210x",
              "http://www.ietf.org/rfc/rfc2396.txt",
              "ldap://[2001:db8::7]/c=GB?objectClass?one",
              "tel:+1-816-555-1212",
              "telnet://192.0.2.16:80/",
              "foo://example.com:8042/over/there?name=ferret#nose"
              ,
      };

      int j = 0;
      while (j < 9)
      {
         uri = UriBuilder.fromUri(uris[j]).build();
         if (uri.toString().trim().compareToIgnoreCase(uris[j]) != 0)
         {
            pass = false;
            sb.append("Test failed for expected uri: " + uris[j] +
                    " Got " + uri.toString() + " instead");
            throw new Exception(sb.toString());
         }
         j++;
      }
   }

   @Test
   public void testEncoding() throws Exception
   {
      HashMap<String, Object> map = new HashMap<String, Object>();

      {
         map.clear();
         UriBuilderImpl impl = (UriBuilderImpl) UriBuilder.fromPath("/foo/{id}");
         map.put("id", "something %%20something");

         URI uri = impl.buildFromMap(map);
         Assert.assertEquals("/foo/something%20%25%20something", uri.toString());
      }
      {
         UriBuilderImpl impl = (UriBuilderImpl) UriBuilder.fromPath("/foo/{id}");
         map.clear();
         map.put("id", "something something");
         URI uri = impl.buildFromMap(map);
         Assert.assertEquals("/foo/something%20something", uri.toString());
      }
      {
         UriBuilderImpl impl = (UriBuilderImpl) UriBuilder.fromPath("/foo/{id}");
         map.clear();
         map.put("id", "something%20something");
         URI uri = impl.buildFromEncodedMap(map);
         Assert.assertEquals("/foo/something%20something", uri.toString());
      }


      {
         UriBuilderImpl impl = (UriBuilderImpl) UriBuilder.fromPath("/foo/{id}");

         impl.substitutePathParam("id", "something %%20something", false);
         URI uri = impl.build();
         Assert.assertEquals("/foo/something%20%25%20something", uri.toString());
      }
      {
         UriBuilderImpl impl = (UriBuilderImpl) UriBuilder.fromPath("/foo/{id}");

         impl.substitutePathParam("id", "something something", false);
         URI uri = impl.build();
         Assert.assertEquals("/foo/something%20something", uri.toString());
      }
      {
         UriBuilderImpl impl = (UriBuilderImpl) UriBuilder.fromPath("/foo/{id}");

         impl.substitutePathParam("id", "something%20something", true);
         URI uri = impl.build();
         Assert.assertEquals("/foo/something%20something", uri.toString());
      }
   }

   @Test
   public void testQueryParamSubstitution() throws Exception
   {
      UriBuilder.fromUri("http://localhost/test").queryParam("a", "{b}").build("c");
   }


}
