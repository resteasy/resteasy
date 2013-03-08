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
   public void testTemplate() throws Exception
   {
      UriBuilder builder = UriBuilder.fromUri("http://{host}/x/y/{path}?{q}={qval}");
      String template = builder.toTemplate();
      Assert.assertEquals(template, "http://{host}/x/y/{path}?{q}={qval}");
      builder = builder.resolveTemplate("host", "localhost");
      template = builder.toTemplate();
      Assert.assertEquals(template, "http://localhost/x/y/{path}?{q}={qval}");

      builder = builder.resolveTemplate("q", "name");
      template = builder.toTemplate();
      Assert.assertEquals(template, "http://localhost/x/y/{path}?name={qval}");
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("path", "z");
      values.put("qval", new Integer(42));
      builder = builder.resolveTemplates(values);
      template = builder.toTemplate();
      Assert.assertEquals(template, "http://localhost/x/y/z?name=42");


   }

   @Test
   public void test587() throws Exception
   {
      System.out.println(UriBuilder.fromPath("/{p}").build("$a"));
   }

   @Test
   public void test443() throws Exception
   {
      // test for RESTEASY-443

      UriBuilderImpl.fromUri("?param=").replaceQueryParam("otherParam", "otherValue");

   }

   @Test
   public void testEmoji()
   {
      UriBuilder builder = UriBuilder.fromPath("/my/url");
      builder.queryParam("msg", "emoji stuff %EE%81%96%EE%90%8F");
      URI uri = builder.build();
      System.out.println(uri);
      Assert.assertEquals("/my/url?msg=emoji+stuff+%EE%81%96%EE%90%8F", uri.toString());

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
         Assert.assertEquals("/foo/something%20%25%2520something", uri.toString());
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

   /**
    * Regression from TCK 1.1
    */
   @Test
   public void testEncodedMap1() throws Exception
   {
      StringBuffer sb = new StringBuffer();
      boolean pass = true;
      URI uri;

      Map maps = new HashMap();
      maps.put("x", "x%20yz");
      maps.put("y", "/path-absolute/%test1");
      maps.put("z", "fred@example.com");
      maps.put("w", "path-rootless/test2");

      String expected_path =
              "path-rootless/test2/x%20yz//path-absolute/%25test1/fred@example.com/x%20yz";

      uri = UriBuilder.fromPath("").path("{w}/{x}/{y}/{z}/{x}").
              buildFromEncodedMap(maps);
      if (uri.getRawPath().compareToIgnoreCase(expected_path) != 0)
      {
         pass = false;
         sb.append("Test failed for expected path: " + expected_path +
                 " Got " + uri.getRawPath() + " instead\n");
      }
      else
      {
         sb.append("Got expected path: " + uri.getRawPath() + "\n");
      }

      if (!pass)
      {
         System.out.println(sb.toString());
      }
      Assert.assertTrue(pass);
   }

   /**
    * from TCK 1.1
    */
   @Test
   public void testEncodedMapTest3() throws Exception
   {
      Map maps = new HashMap();
      maps.put("x", null);
      maps.put("y", "/path-absolute/test1");
      maps.put("z", "fred@example.com");
      maps.put("w", "path-rootless/test2");
      maps.put("u", "extra");

      String expected_path =
              "path-rootless/test2/x%yz//path-absolute/test1/fred@example.com/x%yz";

      try
      {
         URI uri = UriBuilder.fromPath("").path("{w}/{x}/{y}/{z}/{x}").
                 buildFromEncodedMap(maps);
         throw new Exception("Test Failed: expected IllegalArgumentException not thrown");
      }
      catch (IllegalArgumentException ex)
      {
      }
   }

   @Test
   public void testEncodedMapTest4() throws Exception
   {
      StringBuffer sb = new StringBuffer();
      boolean pass = true;
      URI uri;

      Map maps = new HashMap();
      maps.put("x", "x%yz");
      maps.put("y", "/path-absolute/test1");
      maps.put("z", "fred@example.com");
      maps.put("w", "path-rootless/test2");
      maps.put("u", "extra");

      String expected_path =
              "path-rootless/test2/x%yz//path-absolute/test1/fred@example.com/x%yz";

      try
      {
         uri = UriBuilder.fromPath("").path("{w}/{v}/{x}/{y}/{z}/{x}").
                 buildFromEncodedMap(maps);
         throw new Exception("Test Failed: expected IllegalArgumentException not thrown");
      }
      catch (IllegalArgumentException ex)
      {
      }
   }

   @Test
   public void testBuildFromMapTest1() throws Exception
   {
      StringBuffer sb = new StringBuffer();
      boolean pass = true;
      URI uri;

      Map maps = new HashMap();
      maps.put("x", "x%yz");
      maps.put("y", "/path-absolute/test1");
      maps.put("z", "fred@example.com");
      maps.put("w", "path-rootless/test2");

      String expected_path =
              "path-rootless%2Ftest2/x%25yz/%2Fpath-absolute%2Ftest1/fred@example.com/x%25yz";

      try
      {
         uri = UriBuilder.fromPath("").path("{w}/{x}/{y}/{z}/{x}").
                 buildFromMap(maps);
         if (uri.getRawPath().compareToIgnoreCase(expected_path) != 0)
         {
            pass = false;
            sb.append("Test failed for expected path: " + expected_path +
                    " Got " + uri.getRawPath() + " instead\n");
         }
         else
         {
            sb.append("Got expected path: " + uri.getRawPath() + "\n");
         }
      }
      catch (Exception ex)
      {
         pass = false;
         sb.append("Unexpected exception thrown: " + ex.getMessage() +
                 "\n");
      }

      if (!pass)
      {
         throw new Exception("At least one assertion failed: " + sb.toString());
      }
   }

   @Test
   public void testBuildFromMapTest2() throws Exception
   {
      StringBuffer sb = new StringBuffer();
      boolean pass = true;
      URI uri;

      Map maps = new HashMap();
      maps.put("x", "x%yz");
      maps.put("y", "/path-absolute/test1");
      maps.put("z", "fred@example.com");
      maps.put("w", "path-rootless/test2");
      maps.put("u", "extra");

      String expected_path =
              "path-rootless%2Ftest2/x%25yz/%2Fpath-absolute%2Ftest1/fred@example.com/x%25yz";

      try
      {
         uri = UriBuilder.fromPath("").path("{w}/{x}/{y}/{z}/{x}").
                 buildFromMap(maps);
         if (uri.getRawPath().compareToIgnoreCase(expected_path) != 0)
         {
            pass = false;
            sb.append("Test failed for expected path: " + expected_path +
                    " Got " + uri.getRawPath() + " instead" + "\n");
         }
         else
         {
            sb.append("Got expected path: " + uri.getRawPath() + "\n");
         }
      }
      catch (Exception ex)
      {
         pass = false;
         sb.append("Unexpected exception thrown: " + ex.getMessage() +
                 "\n");
      }

      if (!pass)
      {
         throw new Exception("At least one assertion failed: " + sb.toString());
      }
   }

   @Test
   public void testBuildFromMapTest3() throws Exception
   {
      StringBuffer sb = new StringBuffer();
      boolean pass = true;
      URI uri;

      Map maps = new HashMap();
      maps.put("x", null);
      maps.put("y", "/path-absolute/test1");
      maps.put("z", "fred@example.com");
      maps.put("w", "path-rootless/test2");
      maps.put("u", "extra");

      String expected_path =
              "path-rootless/test2/x%yz//path-absolute/test1/fred@example.com/x%yz";

      try
      {
         uri = UriBuilder.fromPath("").path("{w}/{x}/{y}/{z}/{x}").
                 buildFromMap(maps);
         throw new Exception("Test Failed: expected IllegalArgumentException not thrown");
      }
      catch (IllegalArgumentException ex)
      {
      }
   }

   @Test
   public void testBuildFromMapTest4() throws Exception
   {
      StringBuffer sb = new StringBuffer();
      boolean pass = true;
      URI uri;

      Map maps = new HashMap();
      maps.put("x", "x%yz");
      maps.put("y", "/path-absolute/test1");
      maps.put("z", "fred@example.com");
      maps.put("w", "path-rootless/test2");
      maps.put("u", "extra");

      String expected_path =
              "path-rootless/test2/x%yz//path-absolute/test1/fred@example.com/x%yz";

      try
      {
         uri = UriBuilder.fromPath("").path("{w}/{v}/{x}/{y}/{z}/{x}").
                 buildFromMap(maps);
         throw new Exception("Test Failed: expected IllegalArgumentException not thrown");
      }
      catch (IllegalArgumentException ex)
      {
      }
   }

   @Test
   public void testBuildFromMapTest5() throws Exception
   {
      StringBuffer sb = new StringBuffer();
      boolean pass = true;
      URI uri;
      UriBuilder ub;

      Map maps = new HashMap();
      maps.put("x", "x%yz");
      maps.put("y", "/path-absolute/test1");
      maps.put("z", "fred@example.com");
      maps.put("w", "path-rootless/test2");

      Map maps1 = new HashMap();
      maps1.put("x", "x%20yz");
      maps1.put("y", "/path-absolute/test1");
      maps1.put("z", "fred@example.com");
      maps1.put("w", "path-rootless/test2");

      Map maps2 = new HashMap();
      maps2.put("x", "x%yz");
      maps2.put("y", "/path-absolute/test1");
      maps2.put("z", "fred@example.com");
      maps2.put("w", "path-rootless/test2");
      maps2.put("v", "xyz");

      String expected_path =
              "path-rootless%2Ftest2/x%25yz/%2Fpath-absolute%2Ftest1/fred@example.com/x%25yz";

      String expected_path_1 =
              "path-rootless%2Ftest2/x%2520yz/%2Fpath-absolute%2Ftest1/fred@example.com/x%2520yz";

      String expected_path_2 =
              "path-rootless%2Ftest2/x%25yz/%2Fpath-absolute%2Ftest1/fred@example.com/x%25yz";

      try
      {
         ub = UriBuilder.fromPath("").path("{w}/{x}/{y}/{z}/{x}");

         uri = ub.buildFromMap(maps);

         if (uri.getRawPath().compareToIgnoreCase(expected_path) != 0)
         {
            pass = false;
            sb.append("Test failed for expected path: " + expected_path +
                    " Got " + uri.getRawPath() + " instead" + "\n");
         }
         else
         {
            sb.append("Got expected path: " + uri.getRawPath() + "\n");
         }

         uri = ub.buildFromMap(maps1);

         if (uri.getRawPath().compareToIgnoreCase(expected_path_1) != 0)
         {
            pass = false;
            sb.append("Test failed for expected path: " + expected_path_1 +
                    " Got " + uri.getRawPath() + " instead" + "\n");
         }
         else
         {
            sb.append("Got expected path: " + uri.getRawPath() + "\n");
         }

         uri = ub.buildFromMap(maps2);

         if (uri.getRawPath().compareToIgnoreCase(expected_path_2) != 0)
         {
            pass = false;
            sb.append("Test failed for expected path: " + expected_path_2 +
                    " Got " + uri.getRawPath() + " instead" + "\n");
         }
         else
         {
            sb.append("Got expected path: " + uri.getRawPath() + "\n");
         }
      }
      catch (Exception ex)
      {
         pass = false;
         sb.append("Unexpected exception thrown: " + ex.getMessage() +
                 "\n");
      }

      if (!pass)
      {
         throw new Exception("At least one assertion failed: " + sb.toString());
      }
   }

   @Test
   public void testFromEncodedTest1() throws Exception
   {
      StringBuffer sb = new StringBuffer();
      boolean pass = true;
      String expected_value_1 = "http://localhost:8080/a/%25/=/%25G0/%25/=";
      String expected_value_2 = "http://localhost:8080/xy/%20/%25/xy";
      URI uri = null;

      uri = UriBuilder.fromPath("http://localhost:8080").path("/{v}/{w}/{x}/{y}/{z}/{x}").
              buildFromEncoded("a", "%25", "=", "%G0", "%", "23");

      if (uri.toString().compareToIgnoreCase(expected_value_1) != 0)
      {
         pass = false;
         sb.append("Incorrec URI returned: " + uri.toString() +
                 ", expecting " + expected_value_1 + "\n");
      }
      else
      {
         sb.append("Got expected return: " + expected_value_1 + "\n");
      }

      uri = UriBuilder.fromPath("http://localhost:8080").path("/{x}/{y}/{z}/{x}").
              buildFromEncoded("xy", " ", "%");

      if (uri.toString().compareToIgnoreCase(expected_value_2) != 0)
      {
         pass = false;
         sb.append("Incorrec URI returned: " + uri.toString() +
                 ", expecting " + expected_value_2 + "\n");
      }
      else
      {
         sb.append("Got expected return: " + expected_value_2 + "\n");
      }


      if (!pass)
      {
         throw new Exception("At least one assertion failed: " + sb.toString());
      }
   }

   @Test
   public void testQueryParamTest1() throws Exception
   {
      String name = null;

      try
      {
         UriBuilder.fromPath("http://localhost:8080").queryParam(name, "x",
                 "y");
         throw new Exception("Expected IllegalArgumentException Not thrown");
      }
      catch (IllegalArgumentException ilex)
      {
      }
   }

   @Test
   public void QueryParamTest5() throws Exception
   {
      Boolean pass = true;
      String name = "name";
      StringBuffer sb = new StringBuffer();
      String expected_value =
              "http://localhost:8080?name=x%3D&name=y?&name=x+y&name=%26";
      URI uri;

      try
      {
         uri = UriBuilder.fromPath("http://localhost:8080").queryParam(name,
                 "x=", "y?", "x y", "&").build();
         if (uri.toString().compareToIgnoreCase(expected_value) != 0)
         {
            pass = false;
            sb.append("Incorrec URI returned: " + uri.toString() +
                    ", expecting " + expected_value + "\n");
         }
         else
         {
            sb.append("Got expected return: " + expected_value + "\n");
         }
      }
      catch (Exception ex)
      {
         pass = false;
         sb.append("Unexpected Exception thrown" + ex.getMessage());
      }

      if (!pass)
      {
         throw new Exception("At least one assertion failed: " + sb.toString());
      }
   }

   @Test
   public void testReplaceQueryTest3() throws Exception
   {
      Boolean pass = true;
      String name = "name";
      StringBuffer sb = new StringBuffer();
      String expected_value =
              "http://localhost:8080?name1=x&name2=%20&name3=x+y&name4=23&name5=x%20y";
      URI uri;

      uri = UriBuilder.fromPath("http://localhost:8080").queryParam(name,
              "x=", "y?", "x y", "&").replaceQuery("name1=x&name2=%20&name3=x+y&name4=23&name5=x y").
              build();
      if (uri.toString().compareToIgnoreCase(expected_value) != 0)
      {
         pass = false;
         sb.append("Incorrec URI returned: " + uri.toString() +
                 ", expecting " + expected_value + "\n");
      }
      else
      {
         sb.append("Got expected return: " + expected_value + "\n");
      }
      if (!pass)
      {
         throw new Exception("At least one assertion failed: " + sb.toString());
      }
   }

   @Test
   public void testReplaceQueryParamTest2() throws Exception
   {
      Boolean pass = true;
      String name = "name";
      StringBuffer sb = new StringBuffer();
      String expected_value = "http://localhost:8080";
      URI uri;

      uri =
              UriBuilder.fromPath("http://localhost:8080").queryParam(name,
                      "x=", "y?", "x y", "&").replaceQueryParam(name, null).build();
      if (uri.toString().compareToIgnoreCase(expected_value) != 0)
      {
         pass = false;
         sb.append("Incorrec URI returned: " + uri.toString() +
                 ", expecting " + expected_value + "\n");
      }
      else
      {
         sb.append("Got expected return: " + expected_value + "\n");
      }

      if (!pass)
      {
         throw new Exception("At least one assertion failed: " + sb.toString());
      }
   }

   @Test
   public void testPathEncoding() throws Exception
   {
      UriBuilder builder = UriBuilder.fromUri("http://{host}");
      builder.path("{d}");

      URI uri = builder.build("A/B", "C/D");
      Assert.assertEquals("http://A%2FB/C%2FD", uri.toString());

      uri = builder.buildFromEncoded("A/B", "C/D");
      Assert.assertEquals("http://A/B/C/D", uri.toString());
      Object[] params = {"A/B", "C/D"};
      uri = builder.build(params, false);
      Assert.assertEquals("http://A/B/C/D", uri.toString());

      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put("host", "A/B");
      map.put("d", "C/D");

      uri = builder.buildFromMap(map);
      Assert.assertEquals("http://A%2FB/C%2FD", uri.toString());
      uri = builder.buildFromEncodedMap(map);
      Assert.assertEquals("http://A/B/C/D", uri.toString());
      uri = builder.buildFromMap(map, false);
      Assert.assertEquals("http://A/B/C/D", uri.toString());

   }


}
