package org.jboss.resteasy.test.interception;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.interception.jaxrs.ContainerResponseContextImpl;
import org.jboss.resteasy.plugins.interceptors.MessageSanitizerContainerResponseFilter;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.Assert;
import org.junit.Test;


/**
 * @tpSubChapter Providers - MessageSanitizerContainerResponseFilter
 * @tpChapter Unit tests
 * @tpTestCaseDetails Regression test for RESTEASY-2033
 * @tpSince RESTEasy 4.0.0
 */
public class MessageSanitizerMediaTypeTest {

   static private final String input = "<html &lt;\"abc\" 'xyz'&gt;/>";
   static private final String output = "&lt;html &amp;lt;&quot;abc&quot; &#x27;xyz&#x27;&amp;gt;&#x2F;&gt;";

   public static class TestContainerResponseContext extends  ContainerResponseContextImpl {
      public TestContainerResponseContext(BuiltResponse builtResponse) {
         super(null, null, builtResponse, null, null, null, null);
      }
   }

   @Test
   public void testMessageSanitizerText() throws Exception {
      doTestMessageSanitizerMediaType("text/html");
   }

   @Test
   public void testMessageSanitizerMediaType() throws Exception {
      doTestMessageSanitizerMediaType(MediaType.TEXT_HTML_TYPE);
   }

   void doTestMessageSanitizerMediaType(Object mediaType) throws Exception {
      Headers<Object> headers = new Headers<Object>();
      headers.add("Content-Type", mediaType);
      BuiltResponse response = new BuiltResponse(HttpResponseCodes.SC_BAD_REQUEST, "", headers, input, null);
      ContainerResponseContext responseContext = new TestContainerResponseContext(response);
      MessageSanitizerContainerResponseFilter filter = new MessageSanitizerContainerResponseFilter();
      filter.filter(null, responseContext);
      Assert.assertEquals(output, responseContext.getEntity());
   }
}