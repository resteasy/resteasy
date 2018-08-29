package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.core.interception.DecoratorMatcher;
import org.jboss.resteasy.plugins.providers.AbstractEntityProvider;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.LogMessages;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.NoContent;
import org.jboss.resteasy.util.TypeConverter;
import org.xml.sax.InputSource;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * A AbstractJAXBProvider.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 * @param <T> type
 */
public abstract class AbstractJAXBProvider<T> extends AbstractEntityProvider<T>
{
   @Context
   protected Providers providers;

   private boolean disableExternalEntities = true;
   private boolean enableSecureProcessingFeature = true;
   private boolean disableDTDs = true;
   
   public AbstractJAXBProvider()
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : AbstractJAXBProvider", getClass().getName());
      ResteasyConfiguration context = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
      if (context != null)
      {
         String s = context.getParameter("resteasy.document.expand.entity.references");
         if (s != null)
         {
            setDisableExternalEntities(!Boolean.parseBoolean(s));
         }
         s = context.getParameter("resteasy.document.secure.processing.feature");
         if (s != null)
         {
            setEnableSecureProcessingFeature(Boolean.parseBoolean(s));
         }
         s = context.getParameter("resteasy.document.secure.disableDTDs");
         if (s != null)
         {
            setDisableDTDs(Boolean.parseBoolean(s));
         }
      }
   }
   
   public JAXBContext findJAXBContext(Class<?> type, Annotation[] annotations, MediaType mediaType, boolean reader)
           throws JAXBException
   {
      ContextResolver<JAXBContextFinder> resolver = providers.getContextResolver(JAXBContextFinder.class, mediaType);
      JAXBContextFinder finder = resolver.getContext(type);
      if (finder == null)
      {
         if (reader) throw new JAXBUnmarshalException(Messages.MESSAGES.couldNotFindJAXBContextFinder(mediaType));
         else throw new JAXBMarshalException(Messages.MESSAGES.couldNotFindJAXBContextFinder(mediaType));
      }
      return finder.findCachedContext(type, mediaType, annotations);
   }

   public static Marshaller decorateMarshaller(Class type, Annotation[] annotations, MediaType mediaType, Marshaller marshaller) throws JAXBException
   {
      DecoratorMatcher processor = new DecoratorMatcher();
      return processor.decorate(Marshaller.class, marshaller, type, annotations, mediaType);
   }

   public static Unmarshaller decorateUnmarshaller(Class type, Annotation[] annotations, MediaType mediaType, Unmarshaller marshaller) throws JAXBException
   {
      DecoratorMatcher processor = new DecoratorMatcher();
      return processor.decorate(Unmarshaller.class, marshaller, type, annotations, mediaType);
   }
   
   @SuppressWarnings("unchecked")
   public T readFrom(Class<T> type,
                     Type genericType,
                     Annotation[] annotations,
                     MediaType mediaType,
                     MultivaluedMap<String, String> httpHeaders,
                     InputStream entityStream) throws IOException
   {
      try
      {
         LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
         NoContent.contentLengthCheck(httpHeaders);
         JAXBContext jaxb = findJAXBContext(type, annotations, mediaType, true);
         Unmarshaller unmarshaller = jaxb.createUnmarshaller();
         unmarshaller = decorateUnmarshaller(type, annotations, mediaType, unmarshaller);
         
         if (needsSecurity())
         {
            return processWithSecureProcessing(unmarshaller, entityStream, getCharset(mediaType));
         }
         
         if (getCharset(mediaType) == null)
         {
            InputSource is = new InputSource(entityStream);
            is.setEncoding(StandardCharsets.UTF_8.name());
            StreamSource source = new StreamSource(new InputStreamReader(entityStream, StandardCharsets.UTF_8));
            source.setInputStream(entityStream);
            return (T) unmarshaller.unmarshal(source);
         }
         else
         {
            return (T) unmarshaller.unmarshal(new StreamSource(entityStream));  
         }
      }
      catch (JAXBException e)
      {
         throw new JAXBUnmarshalException(e);
      }
   }

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
         LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
         Marshaller marshaller = getMarshaller(type, annotations, mediaType);
         marshaller = decorateMarshaller(type, annotations, mediaType, marshaller);
         marshaller.marshal(t, outputStream);
      }
      catch (JAXBException e)
      {
         throw new JAXBMarshalException(e);
      }
   }

   /**
    * Retrieve JAXB marshaller.
    *
    * @param type type
    * @param annotations annotations
    * @param mediaType media type
    * @return jaxb marshaller
    */
   protected Marshaller getMarshaller(Class<?> type,
                                      Annotation[] annotations,
                                      MediaType mediaType)
   {
      try
      {
         JAXBContext jaxb = findJAXBContext(type, annotations, mediaType, false);
         Marshaller marshaller = jaxb.createMarshaller();
         setCharset(mediaType, marshaller);
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
         throw new JAXBMarshalException(e);
      }
   }

   public static void setCharset(MediaType mediaType, Marshaller marshaller)
           throws PropertyException
   {
      String charset = getCharset(mediaType);
      // specify the character encoding if it is set on the media type
      if (charset != null)
      {
         marshaller.setProperty(Marshaller.JAXB_ENCODING, charset);
      }
      else
      {
         marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
      }
   }

   protected abstract boolean isReadWritable(Class<?> type,
                                             Type genericType,
                                             Annotation[] annotations, MediaType mediaType);

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
    * Gets the character set from media type.
    *
    * @param mediaType media type
    * @return charset
    */
   public static String getCharset(final MediaType mediaType)
   {
      if (mediaType != null)
      {
         return mediaType.getParameters().get("charset");
      }
      return null;
   }
   
   public boolean isDisableExternalEntities()
   {
      return disableExternalEntities;
   }

   public void setDisableExternalEntities(boolean disableExternalEntities)
   {
      this.disableExternalEntities = disableExternalEntities;
   }

   public boolean isEnableSecureProcessingFeature()
   {
      return enableSecureProcessingFeature;
   }

   public void setEnableSecureProcessingFeature(boolean enableSecureProcessingFeature)
   {
      this.enableSecureProcessingFeature = enableSecureProcessingFeature;
   }

   public boolean isDisableDTDs()
   {
      return disableDTDs;
   }

   public void setDisableDTDs(boolean disableDTDs)
   {
      this.disableDTDs = disableDTDs;
   }

   protected boolean needsSecurity()
   {
      return true;
   }
   
   @SuppressWarnings("unchecked")
   protected T processWithSecureProcessing(Unmarshaller unmarshaller, InputStream entityStream, String charset) throws JAXBException
   {  
      unmarshaller = new SecureUnmarshaller(unmarshaller, disableExternalEntities, enableSecureProcessingFeature, disableDTDs);
      if (charset == null)
      {
         InputSource is = new InputSource(entityStream);
         is.setEncoding(StandardCharsets.UTF_8.name());
         return (T) unmarshaller.unmarshal(is);
      }
      else
      {
         return (T) unmarshaller.unmarshal(entityStream); 
      }
   }
}
