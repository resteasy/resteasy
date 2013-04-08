package org.jboss.resteasy.test.providers.multipart.regression;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Resteasy204Test extends BaseResourceTest
{
   @Path("/rest/zba")
   public static class Service
   {
      @GET
      @Produces(MediaType.MULTIPART_FORM_DATA)
      public
      @MultipartForm
      MyBean get()
      {
         MyBean myBean = new MyBean();
         myBean.setSomeBinary(new ByteArrayInputStream(new byte[0]));
         return myBean;
      }
   }

   public static class MyBean
   {
      @FormParam("someBinary")
      @PartType(MediaType.APPLICATION_OCTET_STREAM)
      private InputStream someBinary;

      public InputStream getSomeBinary()
      {
         return someBinary;
      }

      public void setSomeBinary(InputStream someBinary)
      {
         this.someBinary = someBinary;
      }
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(Service.class);
   }

   @Test
   public void test() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/rest/zba"));
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(200, response.getStatus());
      String string = response.getEntity();
      System.out.println(string);
      Assert.assertTrue(string.indexOf("Content-Length") > -1);      
   }

}
