package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WriterInterceptorRegistry extends JaxrsInterceptorRegistry<WriterInterceptor>
{
   protected LegacyPrecedence precedence;

   public WriterInterceptorRegistry(ResteasyProviderFactory providerFactory, LegacyPrecedence precedence)
   {
      super(providerFactory, WriterInterceptor.class);
      this.precedence = precedence;
   }

   public WriterInterceptorRegistry clone(ResteasyProviderFactory factory)
   {
      WriterInterceptorRegistry clone = new WriterInterceptorRegistry(factory, precedence);
      clone.interceptors.addAll(interceptors);
      return clone;
   }

   private static class MessageBodyWriterContextFacade implements MessageBodyWriterContext
   {
      protected final WriterInterceptorContext writerInterceptorContext;

      private MessageBodyWriterContextFacade(WriterInterceptorContext writerInterceptorContext)
      {
         this.writerInterceptorContext = writerInterceptorContext;
      }

      @Override
      public Class getType()
      {
         return writerInterceptorContext.getType();
      }

      @Override
      public void setType(Class type)
      {
         writerInterceptorContext.setType(type);
      }

      @Override
      public Type getGenericType()
      {
         return writerInterceptorContext.getGenericType();
      }

      @Override
      public void setGenericType(Type genericType)
      {
         writerInterceptorContext.setGenericType(genericType);
      }

      @Override
      public Annotation[] getAnnotations()
      {
         return writerInterceptorContext.getAnnotations();
      }

      @Override
      public void setAnnotations(Annotation[] annotations)
      {
         writerInterceptorContext.setAnnotations(annotations);
      }

      @Override
      public MediaType getMediaType()
      {
         return writerInterceptorContext.getMediaType();
      }

      @Override
      public void setMediaType(MediaType mediaType)
      {
         writerInterceptorContext.setMediaType(mediaType);
      }

      @Override
      public MultivaluedMap<String, Object> getHeaders()
      {
         return writerInterceptorContext.getHeaders();
      }

      @Override
      public Object getAttribute(String attribute)
      {
         return writerInterceptorContext.getProperty(attribute);
      }

      @Override
      public void setAttribute(String name, Object value)
      {
         writerInterceptorContext.setProperty(name, value);
      }

      @Override
      public void removeAttribute(String name)
      {
         writerInterceptorContext.removeProperty(name);
      }

      @Override
      public Object getEntity()
      {
         return writerInterceptorContext.getEntity();
      }

      @Override
      public void setEntity(Object entity)
      {
         writerInterceptorContext.setEntity(entity);
      }

      @Override
      public OutputStream getOutputStream()
      {
         return writerInterceptorContext.getOutputStream();
      }

      @Override
      public void setOutputStream(OutputStream os)
      {
         writerInterceptorContext.setOutputStream(os);
      }

      @Override
      public void proceed() throws IOException, WebApplicationException
      {
         writerInterceptorContext.proceed();
      }
   }

   public static class WriterInterceptorFacade implements WriterInterceptor
   {
      protected final MessageBodyWriterInterceptor interceptor;

      public WriterInterceptorFacade(MessageBodyWriterInterceptor interceptor)
      {
         this.interceptor = interceptor;
      }

      public MessageBodyWriterInterceptor getInterceptor()
      {
         return interceptor;
      }

      @Override
      public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException
      {
         MessageBodyWriterContextFacade facade = new MessageBodyWriterContextFacade(writerInterceptorContext);
         interceptor.write(facade);
      }
   }


   public void registerLegacy(Class<? extends MessageBodyWriterInterceptor> decl)
   {
      register(new LegacyPerMethodInterceptorFactory(decl, precedence)
      {
         @Override
         public Match postMatch(Class declaring, AccessibleObject target)
         {
            Object obj = getLegacyMatch(declaring, target);
            if (obj == null) return null;
            MessageBodyWriterInterceptor interceptor = (MessageBodyWriterInterceptor)obj;
            return new Match(new WriterInterceptorFacade(interceptor), order);
         }

      });
   }

   public void registerLegacy(MessageBodyWriterInterceptor interceptor)
   {
      register(new LegacySingletonInterceptorFactory(interceptor.getClass(), interceptor, precedence)
      {
         @Override
         public Match postMatch(Class declaring, AccessibleObject target)
         {
            Object obj = getLegacyMatch(declaring, target);
            if (obj == null) return null;
            MessageBodyWriterInterceptor interceptor = (MessageBodyWriterInterceptor)obj;
            return new Match(new WriterInterceptorFacade(interceptor), order);
         }
      });

   }
}
