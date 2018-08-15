package org.jboss.resteasy.test.resource.param;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class MatrixPathParamTest
{

   @Deployment
   public static Archive<?> deploy() throws Exception {
       WebArchive war = TestUtil.prepareArchive(MatrixPathParamTest.class.getSimpleName());
       return TestUtil.finishContainerPrepare(war, null, TestResourceServer.class, TestSubResourceServer.class);
   }

   private String generateBaseUrl() {
       return PortProviderUtil.generateBaseUrl(MatrixPathParamTest.class.getSimpleName());
   }
   
   @Path("/")
   static public class TestResourceServer
   {
      @Path("matrix1")
      public TestSubResourceServer getM1(@MatrixParam("m1") String m1)
      {
         return new TestSubResourceServer(m1);
      }
   }

   static public class TestSubResourceServer
   {
      protected String m1;

      TestSubResourceServer(String m1)
      {
         this.m1 = m1;
      }

      @GET
      @Path("matrix2")
      public String getM2(@MatrixParam("m2") String m2)
      {
         return m1 + m2;
      }
   }

   @Path("/")
   public interface TestInterfaceClient
   {
      @Path("matrix1")
      TestSubInterfaceClient getM1(@MatrixParam("m1") String m1);
   }

   public interface TestSubInterfaceClient
   {
      @GET
      @Path("matrix2")
      String getM2(@MatrixParam("m2") String m2);
   }

   @Test
   public void testSingleAcceptHeader() throws Exception
   {
      ResteasyWebTarget target = (ResteasyWebTarget) ClientBuilder.newClient().target(generateBaseUrl());
      TestInterfaceClient proxy = target.proxy(TestInterfaceClient.class);
      
      String result = proxy.getM1("a").getM2("b");
      Assert.assertEquals("ab", result);
   }

}
