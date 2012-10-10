package org.jboss.resteasy.test.providers.multipart;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import junit.framework.Assert;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * RESTEASY-723
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 */
public class InputPartDefaultCharsetOverwriteTest
{
   protected static final String TEST_URI = TestPortProvider.generateURL("");
   protected static final String UTF_8 = "UTF-8";
   protected static final String UTF_16 = "UTF-16";
   protected static final String TEXT_PLAIN = "text/plain";
   protected static final String TEXT_HTTP = "text/http";
   protected static final String TEXT_PLAIN_WITH_CHARSET_US_ASCII = normalize("text/plain; charset=US-ASCII");
   protected static final String TEXT_PLAIN_WITH_CHARSET_UTF_16 = normalize("text/plain; charset=UTF-16");
   protected static final String TEXT_HTTP_WITH_CHARSET_US_ASCII = normalize("text/http; charset=US-ASCII");
   protected static final String TEXT_HTTP_WITH_CHARSET_UTF_8 = normalize("text/http; charset=UTF-8");
   protected static final String TEXT_HTTP_WITH_CHARSET_UTF_16 = normalize("text/http; charset=UTF-16");
   protected static final String APPLICATION_XML = normalize("application/xml");
   protected static final String APPLICATION_XML_WITH_CHARSET_US_ASCII = normalize("application/xml; charset=US-ASCII");
   protected static final String APPLICATION_XML_WITH_CHARSET_UTF_8 = normalize("application/xml; charset=UTF-8");
   protected static final String APPLICATION_XML_WITH_CHARSET_UTF_16 = normalize("application/xml; charset=UTF-16");
   protected static final String abc_us_ascii = "abc";
   protected static final byte[] abc_us_ascii_bytes = abc_us_ascii.getBytes(Charset.forName("us-ascii"));
   protected static final String abc_utf8 = new String("abc\u20AC");
   protected static final byte[] abc_utf8_bytes = abc_utf8.getBytes(Charset.forName("utf-8"));
   protected static final String abc_utf16 = new String("abc\u20AC");
   protected static final byte[] abc_utf16_bytes = abc_utf16.getBytes(Charset.forName("utf-16"));
   protected static final String TEXT_PLAIN_WITH_CHARSET_UTF_8 = normalize("text/plain; charset=UTF-8");

   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   @Path("")
   public static class MyService
   {

      @POST
      @Path("test")
      @Consumes(MediaType.MULTIPART_FORM_DATA)
      @Produces(MediaType.TEXT_PLAIN)
      public Response testDefaultContentType(MultipartInput input) throws IOException
      {
         List<InputPart> parts = input.getParts();
         InputPart part = parts.get(0);
         String s1 = part.getBody(String.class, null);
         String s2 = part.getBodyAsString();
         String result = part.getMediaType() + ":" + s1 + ":" + s2; 
         System.out.println("server response: " + result);
         return Response.ok(result, part.getMediaType()).build();
      }
   }

   @Provider
   @ServerInterceptor
   public static class PreProcessorInterceptorContentTypeNoCharsetUTF8 implements PreProcessInterceptor
   {
      public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException
      {
         request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY, TEXT_HTTP_WITH_CHARSET_UTF_8);
         return null;
      }
   }
   
   @Provider
   @ServerInterceptor
   public static class PreProcessorInterceptorContentTypeNoCharsetUTF16 implements PreProcessInterceptor
   {
      public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException
      {
         request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY, TEXT_HTTP_WITH_CHARSET_UTF_16);
         return null;
      }
   }
   
   @Provider
   @ServerInterceptor
   public static class PreProcessorInterceptorNoContentTypeCharsetUTF8 implements PreProcessInterceptor
   {
      public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException
      {
         request.setAttribute(InputPart.DEFAULT_CHARSET_PROPERTY, UTF_8);
         return null;
      }
   }
   
   @Provider
   @ServerInterceptor
   public static class PreProcessorInterceptorNoContentTypeCharsetUTF16 implements PreProcessInterceptor
   {
      public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException
      {
         request.setAttribute(InputPart.DEFAULT_CHARSET_PROPERTY, UTF_16);
         return null;
      }
   }
   
   @Provider
   @ServerInterceptor
   public static class PreProcessorInterceptorContentTypeCharsetUTF8 implements PreProcessInterceptor
   {
      public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException
      {

         request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY, TEXT_HTTP_WITH_CHARSET_US_ASCII);
         request.setAttribute(InputPart.DEFAULT_CHARSET_PROPERTY, UTF_8);
         return null;
      }
   }
   
   @Provider
   @ServerInterceptor
   public static class PreProcessorInterceptorContentTypeCharsetUTF16 implements PreProcessInterceptor
   {
      public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException
      {

         request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY, TEXT_HTTP_WITH_CHARSET_US_ASCII);
         request.setAttribute(InputPart.DEFAULT_CHARSET_PROPERTY, UTF_16);
         return null;
      }
   }

   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
   }
   
   public void setUp(Class<?> providerClass) throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(MyService.class);
      if (providerClass != null)
      {
         dispatcher.getProviderFactory().registerProvider(providerClass, false);
      }
   }
   
   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      deployment = null;
   }
   
   @Test
   public void testUTF8ContentTypeNoCharsetPreprocessorWithNoContentTypeCharset() throws Exception
   {
      setUp(PreProcessorInterceptorNoContentTypeCharsetUTF8.class);
      doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_PLAIN, TEXT_PLAIN_WITH_CHARSET_UTF_8);
   }
   
   ////////////////////////////////////////////////////////
   // The following tests use no interceptor.
   ////////////////////////////////////////////////////////
   @Test
   public void testNoContentTypeDefault() throws Exception
   {
      setUp(null);
      doTestNoContentTypeInMessage(abc_us_ascii_bytes, abc_us_ascii, TEXT_PLAIN_WITH_CHARSET_US_ASCII);
   }
   
   @Test
   public void testContentTypeNoCharsetDefault() throws Exception
   {
      setUp(null);
      doTestWithContentTypeInMessage(abc_us_ascii_bytes, abc_us_ascii, TEXT_HTTP, TEXT_HTTP_WITH_CHARSET_US_ASCII);
   }

   @Test
   public void testContentTypeCharsetDefaultUTF8() throws Exception
   {
      setUp(null);
      doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_HTTP_WITH_CHARSET_UTF_8, TEXT_HTTP_WITH_CHARSET_UTF_8);
   }
   
   @Test
   public void testContentTypeCharsetDefaultUTF16() throws Exception
   {
      setUp(null);
      doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_HTTP_WITH_CHARSET_UTF_16, TEXT_HTTP_WITH_CHARSET_UTF_16);
   }
   
   //////////////////////////////////////////////////////////////////////////////////////
   // The following tests use an interceptor that installs a content-type but no charset.
   //////////////////////////////////////////////////////////////////////////////////////
   @Test
   public void testNoContentTypePreprocessorWithContentTypeNoCharsetUTF8() throws Exception
   {
      setUp(PreProcessorInterceptorContentTypeNoCharsetUTF8.class);
      doTestNoContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_HTTP_WITH_CHARSET_UTF_8);
   }
   
   @Test
   public void testNoContentTypePreprocessorWithContentTypeNoCharsetUTF16() throws Exception
   {
      setUp(PreProcessorInterceptorContentTypeNoCharsetUTF16.class);
      doTestNoContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_HTTP_WITH_CHARSET_UTF_16);
   }
   
   @Test
   public void testContentTypeNoCharsetPreprocessorWithContentTypeNoCharsetUTF8() throws Exception
   {
      setUp(PreProcessorInterceptorContentTypeNoCharsetUTF8.class);
      doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_PLAIN, TEXT_PLAIN_WITH_CHARSET_UTF_8);
   }
   
   @Test
   public void testContentTypeNoCharsetPreprocessorWithContentTypeNoCharsetUTF16() throws Exception
   {
      setUp(PreProcessorInterceptorContentTypeNoCharsetUTF16.class);
      doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_PLAIN, TEXT_PLAIN_WITH_CHARSET_UTF_16);
   }
   
   @Test
   public void testContentTypeCharsetPreprocessorWithContentTypeNoCharsetUTF8() throws Exception
   {
      setUp(PreProcessorInterceptorContentTypeNoCharsetUTF16.class); // Should be ignored.
      doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_PLAIN_WITH_CHARSET_UTF_8, TEXT_PLAIN_WITH_CHARSET_UTF_8);
   }
   
   @Test
   public void testContentTypeCharsetPreprocessorWithContentTypeNoCharsetUTF16() throws Exception
   {
      setUp(PreProcessorInterceptorContentTypeNoCharsetUTF8.class); // Should be ignored.
      doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_PLAIN_WITH_CHARSET_UTF_16, TEXT_PLAIN_WITH_CHARSET_UTF_16);
   }
   
   //////////////////////////////////////////////////////////////////////////////////////
   // The following tests use an interceptor that installs a charset but no content-type.
   //////////////////////////////////////////////////////////////////////////////////////
   @Test
   public void testNoContentTypePreprocessorWithNoContentTypeCharsetUTF8() throws Exception
   {
      setUp(PreProcessorInterceptorNoContentTypeCharsetUTF8.class);
      doTestNoContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_PLAIN_WITH_CHARSET_UTF_8);
   }
   
   @Test
   public void testNoContentTypePreprocessorWithNoContentTypeCharsetUTF16() throws Exception
   {
      setUp(PreProcessorInterceptorNoContentTypeCharsetUTF16.class);
      doTestNoContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_PLAIN_WITH_CHARSET_UTF_16);
   }

   @Test
   public void testContentTypeNoCharsetPreprocessorWithNoContentTypeCharsetUTF8() throws Exception
   {
      setUp(PreProcessorInterceptorNoContentTypeCharsetUTF8.class);
      doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_HTTP, TEXT_HTTP_WITH_CHARSET_UTF_8);
   }
   
   @Test
   public void testContentTypeNoCharsetPreprocessorWithNoContentTypeCharsetUTF16() throws Exception
   {
      setUp(PreProcessorInterceptorNoContentTypeCharsetUTF16.class);
      doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_HTTP, TEXT_HTTP_WITH_CHARSET_UTF_16);
   }

   @Test
   public void testContentTypeCharsetPreprocessorWithNoContentTypeCharset8() throws Exception
   {
      setUp(PreProcessorInterceptorNoContentTypeCharsetUTF16.class); // Should be ignored.
      doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_PLAIN_WITH_CHARSET_UTF_8, TEXT_PLAIN_WITH_CHARSET_UTF_8);
   }
   
   @Test
   public void testContentTypeCharsetPreprocessorWithNoContentTypeCharset16() throws Exception
   {
      setUp(PreProcessorInterceptorNoContentTypeCharsetUTF8.class); // Should be ignored.
      doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_PLAIN_WITH_CHARSET_UTF_16, TEXT_PLAIN_WITH_CHARSET_UTF_16);
   }
   
   //////////////////////////////////////////////////////////////////////////////////////////
   // The following tests use an interceptor that installs both a content-type and a charset.
   //////////////////////////////////////////////////////////////////////////////////////////
   @Test
   public void testNoContentTypePreprocessorWithContentTypeCharset8() throws Exception
   {
      setUp(PreProcessorInterceptorContentTypeCharsetUTF8.class);
      doTestNoContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_HTTP_WITH_CHARSET_UTF_8);
   }
   
   @Test
   public void testNoContentTypePreprocessorWithContentTypeCharset16() throws Exception
   {
      setUp(PreProcessorInterceptorContentTypeCharsetUTF16.class);
      doTestNoContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_HTTP_WITH_CHARSET_UTF_16);
   }

   @Test
   public void testContentTypeNoCharsetPreprocessorWithContentTypeCharset8() throws Exception
   {
      setUp(PreProcessorInterceptorContentTypeCharsetUTF8.class);
      doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_PLAIN, TEXT_PLAIN_WITH_CHARSET_UTF_8);
   }
   
   @Test
   public void testContentTypeNoCharsetPreprocessorWithContentTypeCharset16() throws Exception
   {
      setUp(PreProcessorInterceptorContentTypeCharsetUTF16.class);
      doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_PLAIN, TEXT_PLAIN_WITH_CHARSET_UTF_16);
   }
   
   @Test
   public void testContentTypeCharsetPreprocessorWithContentTypeCharset8() throws Exception
   {
      setUp(PreProcessorInterceptorContentTypeCharsetUTF16.class); // Should be ignored.
      doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_PLAIN_WITH_CHARSET_UTF_8, TEXT_PLAIN_WITH_CHARSET_UTF_8);
   }  
   
   @Test
   public void testContentTypeCharsetPreprocessorWithContentTypeCharset16() throws Exception
   {
      setUp(PreProcessorInterceptorContentTypeCharsetUTF8.class); // Should be ignored.
      doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_PLAIN_WITH_CHARSET_UTF_16, TEXT_PLAIN_WITH_CHARSET_UTF_16);
   }   
   
   //////////////////////////////////////////////////////////////////////////////////////////
   // The following tests use a non-text media type, which causes mime4j to use a BinaryBody
   // instead of a TextBody.
   //////////////////////////////////////////////////////////////////////////////////////////
   @Test
   public void testApplicationXmlUSAscii() throws Exception
   {
      setUp(null);
      doTestWithContentTypeInMessage(abc_us_ascii_bytes, abc_us_ascii, APPLICATION_XML, APPLICATION_XML_WITH_CHARSET_US_ASCII);
   }

   @Test
   public void testApplicationXmlUTF8() throws Exception
   {
      setUp(null);
      doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, APPLICATION_XML_WITH_CHARSET_UTF_8, APPLICATION_XML_WITH_CHARSET_UTF_8);
   }
   
   @Test
   public void testApplicationXmlUTF16() throws Exception
   {
      setUp(null);
      doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, APPLICATION_XML_WITH_CHARSET_UTF_16, APPLICATION_XML_WITH_CHARSET_UTF_16);
   }
   
   static private void doTestNoContentTypeInMessage(byte[] body, String expectedBody, String expectedContentType) throws Exception
   {
      byte[] start = ("--boo\r\nContent-Disposition: form-data; name=\"foo\"\r\nContent-Transfer-Encoding: 8bit\r\n\r\n").getBytes();
      byte[] end = "\r\n--boo--\r\n".getBytes();
      byte[] buf = new byte[start.length + body.length + end.length];
      int pos0 = 0;
      int pos1 = pos0 + start.length;
      int pos2 = pos1 + body.length;
      System.arraycopy(start, 0, buf, pos0, start.length);
      System.arraycopy(body,  0, buf, pos1, body.length);
      System.arraycopy(end,   0, buf, pos2, end.length);
      ClientRequest request = new ClientRequest(TEST_URI + "/test/");
      request.body("multipart/form-data; boundary=boo", buf);
      ClientResponse<String> response = request.post(String.class);
      System.out.println("status: " + response.getStatus());
      System.out.println("client response: " + response.getEntity());
      Assert.assertEquals("Status code is wrong.", 20, response.getStatus() / 10);
      String[] answer = response.getEntity().split(":");
      Assert.assertEquals(3, answer.length);
      System.out.println("response charset: " + answer[0]);
      Assert.assertEquals(normalize(expectedContentType), normalize(answer[0]));
      Assert.assertEquals(expectedBody, answer[1]);
      Assert.assertEquals(expectedBody, answer[2]);
   }

   static private void doTestWithContentTypeInMessage(byte[] body, String expectedBody, String inputContentType, String expectedContentType) throws Exception
   {
      byte[] start = ("--boo\r\nContent-Disposition: form-data; name=\"foo\"\r\nContent-Type: ").getBytes();
      byte[] middle = (inputContentType + "\r\n\r\n").getBytes();
      byte[] end = "\r\n--boo--\r\n".getBytes();
      byte[] buf = new byte[start.length + middle.length + body.length + end.length];
      int pos0 = 0;
      int pos1 = pos0 + start.length;
      int pos2 = pos1 + middle.length;
      int pos3 = pos2 + body.length;
      System.arraycopy(start,  0, buf, pos0, start.length);
      System.arraycopy(middle, 0, buf, pos1, middle.length);
      System.arraycopy(body,   0, buf, pos2, body.length);
      System.arraycopy(end,    0, buf, pos3, end.length);
      ClientRequest request = new ClientRequest(TEST_URI + "/test/");
      request.body("multipart/form-data; boundary=boo", buf);
      ClientResponse<String> response = request.post(String.class);
      System.out.println("status: " + response.getStatus());
      System.out.println("client response: " + response.getEntity());
      Assert.assertEquals("Status code is wrong.", 20, response.getStatus() / 10);
      String[] answer = response.getEntity().split(":");
      Assert.assertEquals(3, answer.length);
      System.out.println("response charset: " + answer[0]);
      Assert.assertEquals(normalize(expectedContentType), normalize(answer[0]));
      Assert.assertEquals(expectedBody, answer[1]);
      Assert.assertEquals(expectedBody, answer[2]);
   }

   static private String normalize(String s)
   {
      String sl = s.toLowerCase();
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < s.length(); i++)
      {
         if (sl.charAt(i) != ' ' && sl.charAt(i) != '"')
         {
            sb.append(sl.charAt(i));
         }
      }
      return sb.toString();
   }
}
