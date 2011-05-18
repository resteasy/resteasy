package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.ClientProxy;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ParamTest extends BaseResourceTest
{
   @Path("/test")
   public interface MyTest
   {
      @Path("matrix")
      @GET
      @Produces("text/plain")
      String getMatrix(@MatrixParam("param") String matrix);


      @Path("cookie")
      @GET
      @Produces("text/plain")
      String getCookie(@CookieParam("param") String cookie);

      @Path("header")
      @GET
      @Produces("text/plain")
      String getHeader(@HeaderParam("custom") String header);
   }

   public static class MyTestResource implements MyTest
   {
      public String getMatrix(String matrix)
      {
         if (matrix == null) return "null";
         return matrix;
      }

      public String getCookie(@CookieParam("param") String cookie)
      {
         if (cookie == null) return "null";
         return cookie;
      }

      public String getHeader(@CookieParam("custom") String header)
      {
         if (header == null) return "null";
         return header;
      }
   }

   @BeforeClass
   public static void setup() throws Exception
{
    addPerRequestResource(MyTestResource.class);
}

   /**
    * RESTEASY-423
    *
    * @throws Exception
    */
   @Test
   public void testNullMatrixParam() throws Exception
   {
      MyTest proxy = ProxyFactory.create(MyTest.class, generateBaseUrl());
      String rtn = proxy.getMatrix(null);
      Assert.assertEquals("null", rtn);
   }

   /**
    * RESTEASY-522
    *
    * @throws Exception
    */
   @Test
   public void testNullCookieParam() throws Exception
   {
      MyTest proxy = ProxyFactory.create(MyTest.class, generateBaseUrl());
      String rtn = proxy.getCookie(null);
      Assert.assertEquals("null", rtn);
   }

   @Test
   public void testNullHeaderParam() throws Exception
   {
      MyTest proxy = ProxyFactory.create(MyTest.class, generateBaseUrl());
      String rtn = proxy.getHeader(null);
      Assert.assertEquals("null", rtn);
   }
}
