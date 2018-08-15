package org.jboss.resteasy.test.client;
import java.net.URISyntaxException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.junit.Assert;
import org.junit.Test;

/**
 * @tpSubChapter Requests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Regression test for RESTEASY-1274
 * @tpSince RESTEasy 3.0.20
 */
public class ContentEncodingInvocationTest
{
   @Test
   public void test() throws URISyntaxException
   {
      Client client = ClientBuilder.newClient();
      WebTarget target = client.target("http://localhost:8081");
      Builder builder = target.request();
      Variant variant = new Variant(MediaType.TEXT_PLAIN_TYPE, "lang", "encoding");
      ClientInvocation invocation = null;
      for (int i = 0; i < 5; i++)
      {
         invocation = (ClientInvocation) builder.buildPost(Entity.entity("entity", variant));
      }
      String contentEncoding = invocation.getHeaders().getHeader("Content-Encoding");
      Assert.assertEquals(1, countEncoding(contentEncoding));
      
   }
   
   private int countEncoding(String s)
   {
      int i = 0;
      int count = 0;
      while (s.substring(i).indexOf("encoding") > -1)
      {
         count++;
         i += "encoding".length();
      }
      return count;
   }
}
