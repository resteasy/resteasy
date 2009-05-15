package org.jboss.resteasy.test.finegrain;

import junit.framework.Assert;

import org.jboss.resteasy.spi.touri.ObjectToURI;
import org.jboss.resteasy.spi.touri.URIResolver;
import org.jboss.resteasy.spi.touri.URITemplate;
import org.jboss.resteasy.spi.touri.URIable;
import org.junit.Test;

public class ToURITest
{

   @URITemplate("/foo/{id}")
   public static class URITemplateObject
   {
      private int id;

      public URITemplateObject(int id)
      {
         super();
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

   public class URIableObject implements URIable
   {
      @Override
      public String toURI()
      {
         return "/my-url";
      }
   }

   public class CustomURIableObject extends URIableObject
   {
   }

   @Test
   public void testDefaultResolvers()
   {
      ObjectToURI instance = ObjectToURI.getInstance();
      Assert.assertEquals("/foo/123", instance
            .resolveURI(new URITemplateObject(123)));
      Assert.assertEquals("/my-url", instance.resolveURI(new URIableObject()));
   }

   @Test
   public void testCustomResolver()
   {
      ObjectToURI instance = ObjectToURI.getInstance();
      instance.registerURIResolver(new URIResolver()
      {
         @Override
         public boolean handles(Class<?> type)
         {
            return type == CustomURIableObject.class;
         }

         @Override
         public String resolveURI(Object object)
         {
            return "/some-other-uri";
         }
      });

      Assert.assertEquals("/some-other-uri", instance.resolveURI(new CustomURIableObject()));
   }
}
