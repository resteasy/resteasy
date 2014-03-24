package org.jboss.resteasy.test;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.util.ReadFromStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * <a href="https://jira.jboss.org/jira/browse/RESTEASY-351">RESTEASY-351</a>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProxyInputStreamTest extends BaseResourceTest
{
   @Path("/test")
   public static class MyResourceImpl
   {
      @GET
      @Produces("text/plain")
      public String get()
      {
         return "hello world";
      }

   }

   @Path("/test")
   public static interface MyResource
   {
      @GET
      @Produces("text/plain")
      public InputStream get();

   }

   @Override
   @Before
   public void before() throws Exception {
      super.before();
      addPerRequestResource(MyResourceImpl.class);
   }

   @Test
   public void testInputStream() throws Exception
   {
      MyResource proxy = ProxyFactory.create(MyResource.class, TestPortProvider.generateBaseUrl());
      InputStream is = proxy.get();
      byte[] bytes = ReadFromStream.readFromStream(100, is);
      is.close();
      String str = new String(bytes);
      Assert.assertEquals("hello world", str);
   }
}