/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.annotations.providers.jaxb.JAXBConfig;
import org.jboss.resteasy.plugins.providers.AbstractEntityProvider;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.TypeConverter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A AbstractJAXBProvider.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 * @param <T>
 */
public abstract class AbstractJAXBProvider<T> extends AbstractEntityProvider<T>
{

   private ConcurrentHashMap<Class<?>, JAXBContext> cache = new ConcurrentHashMap<Class<?>, JAXBContext>();

   @Context
   protected Providers providers;

   public JAXBContext findProvidedJAXBContext(Class<?> type, MediaType mediaType)
           throws JAXBException
   {
      JAXBContext jaxb = null;
      ContextResolver<JAXBContext> resolver = providers.getContextResolver(JAXBContext.class, mediaType);
      if (resolver != null)
      {
         jaxb = resolver.getContext(type);
         if (jaxb != null) return jaxb;
      }
      return jaxb;
   }

   protected JAXBContext createDefaultJAXBContext(Class<?> type, Annotation[] annotations) throws JAXBException
   {
      JAXBConfig config = FindAnnotation.findAnnotation(type, annotations, JAXBConfig.class);
      return new JAXBContextWrapper(config, type);
   }

   public JAXBContext findJAXBContext(Class<?> type, Annotation[] annotations, MediaType mediaType)
           throws JAXBException
   {

      JAXBContext jaxb = cache.get(type);
      if (jaxb != null) return jaxb;
      jaxb = findProvidedJAXBContext(type, mediaType);
      if (jaxb != null)
      {
         cache.putIfAbsent(type, jaxb);
         return jaxb;
      }
      jaxb = createDefaultJAXBContext(type, annotations);
      if (jaxb != null) cache.putIfAbsent(type, jaxb);
      return jaxb;
   }

   /**
    *
    */
   public T readFrom(Class<T> type,
                     Type genericType,
                     Annotation[] annotations,
                     MediaType mediaType,
                     MultivaluedMap<String, String> httpHeaders,
                     InputStream entityStream) throws IOException
   {
      try
      {
         JAXBContext jaxb = findJAXBContext(type, annotations, mediaType);
         Unmarshaller unmarshaller = jaxb.createUnmarshaller();
         JAXBElement<T> e = unmarshaller.unmarshal(new StreamSource(entityStream), type);
         return e.getValue();
      }
      catch (JAXBException e)
      {
         Response response = Response.serverError().build();
         throw new WebApplicationException(e, response);
      }
   }

   /**
    *
    */
   public void writeTo(T t,
                       Class<?> type,
                       Type genericType,
                       Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream outputStream) throws IOException
   {
      try
      {
         Marshaller marshaller = getMarshaller(type, annotations, mediaType);
         marshaller.marshal(t, outputStream);
      }
      catch (JAXBException e)
      {
         Response response = Response.serverError().build();
         throw new WebApplicationException(e, response);
      }
   }

   /**
    * FIXME Comment this
    *
    * @param type
    * @param mediaType
    * @param httpHeaders
    * @return
    */
   protected Marshaller getMarshaller(Class<?> type,
                                      Annotation[] annotations,
                                      MediaType mediaType)
   {
      try
      {
         JAXBContext jaxb = findJAXBContext(type, annotations, mediaType);
         Marshaller marshaller = jaxb.createMarshaller();
         String charset = getCharset(mediaType);
         // specify the character encoding if it is set on the media type
         if (charset != null)
         {
            marshaller.setProperty(Marshaller.JAXB_ENCODING, charset);
         }
         // Pretty Print the XML response.
         Object formatted = mediaType.getParameters().get("formatted");
         if (formatted != null)
         {
            Boolean value = TypeConverter.getBooleanValue(formatted.toString());
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, value);
         }
         return marshaller;
      }
      catch (JAXBException e)
      {
         Response response = Response.serverError().build();
         throw new WebApplicationException(e, response);
      }
   }

   /**
    * FIXME Comment this
    *
    * @param type
    * @param genericType
    * @param annotations
    * @return
    */
   protected abstract boolean isReadWritable(Class<?> type,
                                             Type genericType,
                                             Annotation[] annotations, MediaType mediaType);

   /**
    *
    */
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return isReadWritable(type, genericType, annotations, mediaType);
   }

   /**
    *
    */
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return isReadWritable(type, genericType, annotations, mediaType);
   }

   /**
    * FIXME Comment this
    *
    * @param mediaType
    * @return
    */
   public final String getCharset(final MediaType mediaType)
   {
      if (mediaType != null)
      {
         return mediaType.getParameters().get("charset");
      }
      return null;
   }


}
