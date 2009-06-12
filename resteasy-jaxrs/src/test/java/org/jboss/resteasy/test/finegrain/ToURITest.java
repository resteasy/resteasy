package org.jboss.resteasy.test.finegrain;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import junit.framework.Assert;

import org.jboss.resteasy.spi.touri.MappedBy;
import org.jboss.resteasy.spi.touri.ObjectToURI;
import org.jboss.resteasy.spi.touri.URIResolver;
import org.jboss.resteasy.spi.touri.URITemplate;
import org.jboss.resteasy.spi.touri.URIable;
import org.junit.Test;

public class ToURITest
{

   public abstract static class AbstractURITemplateObject
   {
      private int id;

      public AbstractURITemplateObject(int id)
      {
         this.id = id;
      }

      public int getId()
      {
         return id;
      }

      public void setId(int id)
      {
         this.id = id;
      }
   }

   @URITemplate("/foo/{id}")
   public static class URITemplateObject extends AbstractURITemplateObject
   {
      public URITemplateObject(int id)
      {
         super(id);
      }
   }

   @MappedBy(resource = FooResouce.class, method = "getFoo")
   public static class MappedByObject extends AbstractURITemplateObject
   {
      public MappedByObject(int id)
      {
         super(id);
      }
   }

   public static class URIableObject implements URIable
   {
      public String toURI()
      {
         return "/my-url";
      }
   }

   public static class CustomURIableObject extends URIableObject
   {
   }

   @Path("/foo/")
   public static interface FooResouce
   {
      @Path("{id}")
      @GET
      AbstractURITemplateObject getFoo(@PathParam("id") Integer id);
   }

   @Test
   public void testDefaultResolvers()
   {
      ObjectToURI instance = ObjectToURI.getInstance();
      Assert.assertEquals("/foo/123", instance
            .resolveURI(new URITemplateObject(123)));
      Assert.assertEquals("/my-url", instance.resolveURI(new URIableObject()));
      Assert.assertEquals("/foo/123", instance.resolveURI(new MappedByObject(
            123)));
   }

   @Test
   public void testCustomResolver()
   {
      ObjectToURI instance = ObjectToURI.getInstance();
      CustomURIableObject custom = new CustomURIableObject();
      Assert.assertEquals("/my-url", instance.resolveURI(custom));
      
      instance.registerURIResolver(new URIResolver()
      {
         public boolean handles(Class<?> type)
         {
            return type == CustomURIableObject.class;
         }

         public String resolveURI(Object object)
         {
            return "/some-other-uri";
         }
      });

      Assert.assertEquals("/some-other-uri", instance.resolveURI(custom));
   }
}
