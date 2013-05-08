package org.jboss.resteasy.test.client;

import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RequestFilterTest
{
   private static final MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;

   public static class PostFilter1 implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         System.out.println("*** filter 1 ***");
         requestContext.setEntity("test", null, mediaType);
      }
   }

   public static class PostFilter2 implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         System.out.println("*** filter 2 ***");
         Object entity = requestContext.getEntity();
         Assert.assertNotNull(entity);
         Assert.assertEquals(entity, "test");
         MediaType mt = requestContext.getMediaType();
         Assert.assertNotNull(mt);
         Assert.assertEquals(mediaType, mt);
         requestContext.abortWith(Response.ok().build());

      }
   }

   @Provider
   public static class AnnotationFilter implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         System.out.println("  ** ANnotation Filter");
         Annotation[] annotations = requestContext.getEntityAnnotations();
         Assert.assertNotNull(annotations);
         requestContext.abortWith(Response.ok(annotations[0].annotationType().getName()).build());
      }
   }

   public static class AcceptLanguageFilter implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         List<Locale> locales = requestContext.getAcceptableLanguages();
         StringBuilder builder = new StringBuilder();
         for (Locale locale : locales) builder.append(locale.toString()).append(",");
         Response r = Response.ok(builder.toString()).build();
         requestContext.abortWith(r);
      }
   }

   static Client client;

   @BeforeClass
   public static void setupClient()
   {
      client = ClientBuilder.newClient();

   }

   @AfterClass
   public static void close()
   {
      client.close();
   }

   @Test
   public void testAcceptLanguages()
   {
      Response response = client.target("foo").register(AcceptLanguageFilter.class).request()
              .acceptLanguage(Locale.CANADA_FRENCH)
              .acceptLanguage(Locale.PRC).get();
      String str = response.readEntity(String.class);
      System.out.println(str);
      System.out.println(Locale.CANADA_FRENCH.toString());
      Assert.assertTrue(str.contains(Locale.CANADA_FRENCH.toString()));

   }


   @Test
   public void testFilters()
   {
      Entity<ByteArrayInputStream> entity = Entity.entity(new ByteArrayInputStream(
              "test".getBytes()), MediaType.WILDCARD_TYPE);
      Response response = client.target("http://nowhere").register(PostFilter1.class).register(PostFilter2.class).request().post(entity);
      Entity<String> post = Entity.entity("test", MediaType.WILDCARD_TYPE,
              AnnotationFilter.class.getAnnotations());
      response = client.target("nada").register(AnnotationFilter.class).request().post(post);
      Assert.assertEquals(Provider.class.getName(), response.readEntity(String.class));




   }
}
