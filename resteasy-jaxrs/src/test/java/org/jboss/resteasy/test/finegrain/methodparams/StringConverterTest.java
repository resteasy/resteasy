package org.jboss.resteasy.test.finegrain.methodparams;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.spi.StringConverter;
import org.jboss.resteasy.test.BaseResourceTest;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StringConverterTest extends BaseResourceTest
{
   public static class POJO
   {
      private String name;

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }

   @Provider
   public static class POJOConverter implements StringConverter<POJO>
   {
      public POJO fromString(String str)
      {
         System.out.println("FROM STRNG: " + str);
         POJO pojo = new POJO();
         pojo.setName(str);
         return pojo;
      }

      public String toString(POJO value)
      {
         return value.getName();
      }
   }

   @Path("/")
   public static class MyResource
   {
      @Path("{pojo}")
      @PUT
      public void put(@QueryParam("pojo") POJO q, @PathParam("pojo") POJO pp, @MatrixParam("pojo") POJO mp,
                      @HeaderParam("pojo") POJO hp)
      {
         Assert.assertEquals(q.getName(), "pojo");
         Assert.assertEquals(pp.getName(), "pojo");
         Assert.assertEquals(mp.getName(), "pojo");
         Assert.assertEquals(hp.getName(), "pojo");
      }
   }

   @Path("/")
   public static class MyDefaultResource
   {
      @PUT
      public void putDefault(@QueryParam("pojo") @DefaultValue("default") POJO q,
                             @MatrixParam("pojo") @DefaultValue("default") POJO mp, @DefaultValue("default") @HeaderParam("pojo") POJO hp)
      {
         Assert.assertEquals(q.getName(), "default");
         Assert.assertEquals(mp.getName(), "default");
         Assert.assertEquals(hp.getName(), "default");
      }
   }

   @Before
   public void setUp() throws Exception
   {
      deployment.getProviderFactory().addStringConverter(POJOConverter.class);
      deployment.getRegistry().addPerRequestResource(MyResource.class);
      deployment.getRegistry().addPerRequestResource(MyDefaultResource.class);
   }

   /**
    * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
    * @version $Revision: 1 $
    */
   @Path("/")
   public static interface MyClient
   {
      @Path("{pojo}")
      @PUT
      void put(@QueryParam("pojo") POJO q, @PathParam("pojo") POJO pp, @MatrixParam("pojo") POJO mp,
               @HeaderParam("pojo") POJO hp);
   }

   @Test
   public void testIt() throws Exception
   {
      MyClient client = ProxyFactory.create(MyClient.class, generateBaseUrl());
      POJO pojo = new POJO();
      pojo.setName("pojo");
      client.put(pojo, pojo, pojo, pojo);
   }

   @Path("/")
   public static interface MyDefaultClient
   {
      @PUT
      void put();
   }

   @Test
   public void testDefault() throws Exception
   {
      final MyDefaultClient client = ProxyFactory.create(MyDefaultClient.class, generateBaseUrl());
      client.put();
   }
}
