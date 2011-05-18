package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.ClientProxy;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MatrixParamTest extends BaseResourceTest
{
   @Path("/test")
   public interface MyTest
   {
      @Path("matrix")
      @GET
      @Produces("text/plain")
      String getMatrix(@MatrixParam("param") String matrix);
   }

   public static class MyTestResource implements MyTest
   {
      public String getMatrix(String matrix)
      {
         if (matrix == null) return "null";
         return matrix;
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


}
