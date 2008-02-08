package org.resteasy.spi;

import org.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.resteasy.specimpl.ResponseBuilderImpl;
import org.resteasy.specimpl.UriBuilderImpl;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyProviderFactory extends RuntimeDelegate
{

   private List<MessageBodyReader> messageBodyReaders = new ArrayList<MessageBodyReader>();
   private List<MessageBodyWriter> messageBodyWriters = new ArrayList<MessageBodyWriter>();
   private Map<Class<?>, HeaderDelegate> headerDelegates = new HashMap<Class<?>, HeaderDelegate>();

   private static AtomicReference<ResteasyProviderFactory> pfr = new AtomicReference<ResteasyProviderFactory>();

   public static void setInstance(ResteasyProviderFactory factory)
   {
      pfr.set(factory);
      RuntimeDelegate.setDelegate(factory);
   }

   /**
    * Get an instance of ProviderFactory. The implementation of
    * ProviderFactory that will be instantiated is determined using the
    * Services API (as detailed in the JAR specification) to determine
    * the classname. The Services API will look for a classname in the file
    * META-INF/services/javax.ws.rs.ext.ProviderFactory in jars available
    * to the runtime.
    */
   public static ResteasyProviderFactory getInstance()
   {
      return pfr.get();
   }

   public static ResteasyProviderFactory initializeInstance()
   {
      setInstance(new ResteasyProviderFactory());
      return getInstance();
   }

   public ResteasyProviderFactory()
   {
      addHeaderDelegate(MediaType.class, new MediaTypeHeaderDelegate());
   }

   public UriBuilder createUriBuilder()
   {
      return new UriBuilderImpl();
   }

   public Response.ResponseBuilder createResponseBuilder()
   {
      return new ResponseBuilderImpl();
   }

   public Variant.VariantListBuilder createVariantListBuilder()
   {
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> tClass)
   {
      return headerDelegates.get(tClass);
   }

   public void addHeaderDelegate(Class clazz, HeaderDelegate header)
   {
      headerDelegates.put(clazz, header);
   }

   public void addMessageBodyReader(MessageBodyReader provider)
   {
      messageBodyReaders.add(provider);
   }

   public void addMessageBodyWriter(MessageBodyWriter provider)
   {
      messageBodyWriters.add(provider);
   }

   public <T> MessageBodyReader<T> createMessageBodyReader(Class<T> type, MediaType mediaType)
   {
      for (MessageBodyReader<T> factory : messageBodyReaders)
      {
         ConsumeMime consumeMime = factory.getClass().getAnnotation(ConsumeMime.class);
         boolean compatible = false;
         for (String consume : consumeMime.value())
         {
            if (mediaType.isCompatible(MediaType.parse(consume)))
            {
               compatible = true;
               break;
            }
         }
         if (!compatible) continue;

         if (factory.isReadable(type))
         {
            return factory;
         }
      }
      return null;
   }

   public <T> MessageBodyWriter<T> createMessageBodyWriter(Class<T> type, MediaType mediaType)
   {
      for (MessageBodyWriter<T> factory : messageBodyWriters)
      {
         ProduceMime produceMime = factory.getClass().getAnnotation(ProduceMime.class);
         boolean compatible = false;
         for (String produce : produceMime.value())
         {
            if (mediaType.isCompatible(MediaType.parse(produce)))
            {
               compatible = true;
               break;
            }
         }
         if (!compatible) continue;

         if (factory.isWriteable(type))
         {
            return factory;
         }
      }
      return null;
   }
}
