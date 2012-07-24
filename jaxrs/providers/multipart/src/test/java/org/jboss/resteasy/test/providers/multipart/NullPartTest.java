package org.jboss.resteasy.test.providers.multipart;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NullPartTest extends BaseResourceTest
{
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


   @Path("/mime")
   public static class MyService
   {

      @GET
      @Produces(MediaType.MULTIPART_FORM_DATA)
      @MultipartForm
      public MyBean createMyBean()
      {
         MyBean myBean = new MyBean();
         myBean.setSomeBinary(null);

         return myBean;
      }
   }

   @Path("/mime")
   public static interface MyServiceProxy
   {

      @GET
      @Produces(MediaType.MULTIPART_FORM_DATA)
      @MultipartForm
      public MyBean createMyBean();
   }


   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(MyService.class);
   }

   private static final String TEST_URI = generateURL("");

   @Test
   public void testNull() throws Exception
   {
      MyServiceProxy proxy = ProxyFactory.create(MyServiceProxy.class, TEST_URI);

      MyBean bean = proxy.createMyBean(); // should just be ok
      Assert.assertNotNull(bean);
      Assert.assertNull(bean.getSomeBinary());
   }


}
