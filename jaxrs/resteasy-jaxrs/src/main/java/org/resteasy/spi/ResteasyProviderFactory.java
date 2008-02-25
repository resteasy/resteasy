package org.resteasy.spi;

import org.resteasy.MediaTypeMap;
import org.resteasy.plugins.delegates.CookieHeaderDelegate;
import org.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.resteasy.plugins.delegates.NewCookieHeaderDelegate;
import org.resteasy.plugins.delegates.UriHeaderDelegate;
import org.resteasy.specimpl.ResponseBuilderImpl;
import org.resteasy.specimpl.UriBuilderImpl;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
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

   private MediaTypeMap<MessageBodyReader> messageBodyReaders = new MediaTypeMap<MessageBodyReader>();
   private MediaTypeMap<MessageBodyWriter> messageBodyWriters = new MediaTypeMap<MessageBodyWriter>();
   private Map<Class<?>, HeaderDelegate> headerDelegates = new HashMap<Class<?>, HeaderDelegate>();

   private static AtomicReference<ResteasyProviderFactory> pfr = new AtomicReference<ResteasyProviderFactory>();

   public static void setInstance(ResteasyProviderFactory factory)
   {
      pfr.set(factory);
      RuntimeDelegate.setDelegate(factory);
   }

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
      addHeaderDelegate(NewCookie.class, new NewCookieHeaderDelegate());
      addHeaderDelegate(Cookie.class, new CookieHeaderDelegate());
      addHeaderDelegate(URI.class, new UriHeaderDelegate());
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
      ConsumeMime consumeMime = provider.getClass().getAnnotation(ConsumeMime.class);
      if (consumeMime != null)
      {
         for (String consume : consumeMime.value())
         {
            MediaType mime = MediaType.parse(consume);
            messageBodyReaders.add(mime, provider);
         }
      }
      else
      {
         messageBodyReaders.add(new MediaType("*", "*"), provider);
      }
   }

   public void addMessageBodyWriter(MessageBodyWriter provider)
   {
      ProduceMime produceMime = provider.getClass().getAnnotation(ProduceMime.class);
      if (produceMime != null)
      {
         for (String produce : produceMime.value())
         {
            MediaType mime = MediaType.parse(produce);
            messageBodyWriters.add(mime, provider);
         }
      }
      else
      {
         messageBodyWriters.add(new MediaType("*", "*"), provider);
      }
   }

   public <T> MessageBodyReader<T> createMessageBodyReader(Class<T> type, MediaType mediaType)
   {
      List<MessageBodyReader> readers = messageBodyReaders.getPossible(mediaType);
      for (MessageBodyReader reader : readers)
      {
         if (reader.isReadable(type))
         {
            return (MessageBodyReader<T>) reader;
         }
      }
      return null;
   }

   public <T> MessageBodyWriter<T> createMessageBodyWriter(Class<T> type, MediaType mediaType)
   {
      List<MessageBodyWriter> writers = messageBodyWriters.getPossible(mediaType);
      for (MessageBodyWriter writer : writers)
      {
         if (writer.isWriteable(type))
         {
            return (MessageBodyWriter<T>) writer;
         }
      }
      return null;
   }
}
