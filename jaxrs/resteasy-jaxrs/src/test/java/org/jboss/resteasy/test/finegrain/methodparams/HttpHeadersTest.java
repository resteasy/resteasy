package org.jboss.resteasy.test.finegrain.methodparams;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Spec requires that HEAD and OPTIONS are handled in a default manner
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpHeadersTest extends BaseResourceTest
{

   @Path(value = "/HeadersTest")
   public static class Resource
   {

      @Context
      HttpHeaders hs;
      StringBuffer sb;
      String lst;

      @GET
      @Path("/headers")
      public String headersGet()
      {
         sb = new StringBuffer();
         List<String> myHeaders = Arrays.asList("Accept", "Content-Type");

         try
         {
            MultivaluedMap<String, String> rqhdrs = hs.getRequestHeaders();
            Set<String> keys = rqhdrs.keySet();
            sb.append("getRequestHeaders= ");
            for (String header : myHeaders)
            {
               System.out.println("Header: " + header);
               if (keys.contains(header))
               {
                  sb.append("Found " + header + ": " +
                          hs.getRequestHeader(header) + "; ");
               }
            }
         }
         catch (Throwable ex)
         {
            sb.append("Unexpected exception thrown in getRequestHeaders: " +
                    ex.getMessage());
            ex.printStackTrace();
         }
         return sb.toString();
      }

      @GET
      @Path("/acl")
      public String aclGet()
      {
         sb = new StringBuffer();
         try
         {
            sb.append("Accept-Language");

            List<Locale> acl = hs.getAcceptableLanguages();
            sb.append("getLanguage= ");
            for (Locale tmp : acl)
            {
               sb.append(tmp.toString() + "; ");
            }
         }
         catch (Throwable ex)
         {
            sb.append("Unexpected exception thrown in getAcceptableLanguages: " +
                    ex.getMessage());
            ex.printStackTrace();
         }
         return sb.toString();
      }

      @GET
      @Path("/amt")
      public String amtGet()
      {
         sb = new StringBuffer();
         try
         {
            sb.append("getAcceptableMediaTypes");
            List<MediaType> acmts = hs.getAcceptableMediaTypes();

            for (MediaType mt : acmts)
            {
               sb.append(mt.getType());
               sb.append("/");
               sb.append(mt.getSubtype());
            }
         }
         catch (Throwable ex)
         {
            sb.append("Unexpected exception thrown: " + ex.getMessage());
            ex.printStackTrace();
         }
         return sb.toString();
      }

      @GET
      @Path("/mt")
      public String mtGet()
      {
         sb = new StringBuffer();

         try
         {
            sb.append("getMediaType");
            MediaType mt = hs.getMediaType();
            if (mt != null)
            {
               sb.append(mt.getType());
               sb.append("/");
               sb.append(mt.getSubtype());
               sb.append(" ");

               java.util.Map<java.lang.String, java.lang.String> pmap =
                       mt.getParameters();

               sb.append("MediaType size=" + pmap.size());

               for (Map.Entry<String, String> entry : pmap.entrySet())
               {
                  sb.append("Key " + entry.getKey() + "; Value " + entry.getValue());
               }

               sb.append(mt.toString());

               sb.append("MediaType= " + mt.toString() + "; ");
            }
            else
            {
               sb.append("MediaType= null; ");
            }
         }
         catch (Throwable ex)
         {
            sb.append("Unexpected exception thrown: " + ex.getMessage());
            ex.printStackTrace();
         }
         return sb.toString();
      }

      @GET
      @Path("/cookie")
      public String cookieGet()
      {
         sb = new StringBuffer();

         try
         {
            sb.append("getCookies= ");
            Map<String, Cookie> cookies = hs.getCookies();
            sb.append("Cookie Size=" + cookies.size());

            for (Map.Entry<String, Cookie> tmp : cookies.entrySet())
            {
               sb.append(tmp.getKey() + ": " + tmp.getValue() + "; ");
               Cookie c = cookies.get("name1");
               sb.append("Cookie Name=" + c.getName());
               sb.append("Cookie Value=" + c.getValue());
               sb.append("Cookie Path=" + c.getPath());
               sb.append("Cookie Domain=" + c.getDomain());
               sb.append("Cookie Version=" + c.getVersion());

            }
         }
         catch (Throwable ex)
         {
            sb.append("Unexpected exception thrown: " + ex.getMessage());
            ex.printStackTrace();
         }

         return sb.toString();
      }

      @PUT
      public String headersPlainPut()
      {
         sb = new StringBuffer();
         sb.append("Content-Language");
         sb.append(hs.getLanguage());
         return sb.toString();
      }
   }

   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(Resource.class);
   }

   /*
    * Client invokes PUT request on root resource at /HeadersTest with Language Header set;
    *                 Verify that HttpHeaders got the property set by the request
    */
   /* Challenge to TCK impending
   @Test
   public void ContentLanguageTest() throws Exception
   {
      HttpClient client = new HttpClient();
      PutMethod method = new PutMethod(TestPortProvider.generateURL("/HeadersTest"));
      method.addRequestHeader("Accept", "text/plain");
      method.addRequestHeader("Content-Language", "en-US");
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);
      String content = method.getResponseBodyAsString();
      Assert.assertEquals(content, "Content-Language|en-US");
   }
   */

   /*
    * Client invokes GET request on a sub resource at /HeadersTest/sub2
    *                 with Accept MediaType and Content-Type Headers set;
    *                 Verify that HttpHeaders got the property set by the request
    *
    *
    * The problem was that CaseInsentiveMap.keySet() and KeySetWrapper was buggy.
    */

   @Test
   public void RequestHeadersTest() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(TestPortProvider.generateURL("/HeadersTest/headers"));
      method.addRequestHeader("Accept", "text/plain, text/html, text/html;level=1, */*");
      method.addRequestHeader("Content-Type", "application/xml;charset=utf8");
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);
      String content = method.getResponseBodyAsString();

      Assert.assertTrue(-1 < content.indexOf("Accept:"));
      Assert.assertTrue(-1 < content.indexOf("Content-Type:"));
      Assert.assertTrue(-1 < content.indexOf("application/xml"));
      Assert.assertTrue(-1 < content.indexOf("charset=utf8"));
      Assert.assertTrue(-1 < content.indexOf("text/html"));
      Assert.assertTrue(-1 < content.indexOf("*/*"));

      System.out.println(content);
   }


}