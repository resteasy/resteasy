/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;

import org.jboss.resteasy.plugins.providers.AbstractEntityProvider;
import org.jboss.resteasy.util.TypeConverter;

/**
 * A AbstractJAXBProvider.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 * @param <T>
 */
public abstract class AbstractJAXBProvider<T> extends AbstractEntityProvider<T>
{

   /**
    * An HTTP Header than can be passed in order to have the XML response
    * formatted.
    */
   public static final String FORMAT_XML_HEADER = "X-Xml-Formatted";


   /**
    * FIXME Comment this
    *
    * @param type
    * @param genericType
    * @param annotations
    * @param mediaType
    * @param httpHeaders
    * @return
    */
   protected Unmarshaller getUnmarshaller(Class<T> type,
                                          Type genericType,
                                          Annotation[] annotations,
                                          MediaType mediaType,
                                          MultivaluedMap<String, String> httpHeaders)
   {
      try
      {
         JAXBContext jaxb = findJAXBContext(type);
         Unmarshaller unmarshaller = jaxb.createUnmarshaller();
         return unmarshaller;
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
    * @param entityStream
    * @return
    * @throws JAXBException
    */
   protected JAXBElement<?> unmarshall(Class<?> type, InputStream entityStream)
           throws JAXBException
   {
      JAXBContext jaxb = findJAXBContext(type);
      Unmarshaller unmarshaller = jaxb.createUnmarshaller();
      JAXBElement<?> e = unmarshaller.unmarshal(new StreamSource(entityStream), type);
      return e;
   }

   protected JAXBContext findJAXBContext(Class<?> type)
           throws JAXBException
   {
      JAXBContext jaxb = JAXBCache.instance().getJAXBContext(type);
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
         JAXBContext jaxb = findJAXBContext(type);
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
         Marshaller marshaller = getMarshaller(type, mediaType, httpHeaders);
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
                                      MediaType mediaType,
                                      MultivaluedMap<String, Object> httpHeaders)
   {
      try
      {
         JAXBContext jaxb = findJAXBContext(type);
         Marshaller marshaller = jaxb.createMarshaller();
         String charset = getCharset(mediaType);
         // specify the character encoding if it is set on the media type
         if (charset != null)
         {
            marshaller.setProperty(Marshaller.JAXB_ENCODING, charset);
         }
         // Pretty Print the XML response.
         Object header = httpHeaders.getFirst(FORMAT_XML_HEADER);
         if (header != null)
         {
            Boolean value = TypeConverter.getBooleanValue(header.toString());
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
   
   /**
    * FIXME Comment this
    * 
    * @param entityStream
    * @return
    */
   protected XMLStreamReader getXMLStreamReader(InputStream entityStream)
   {
      return XMLStreamFactory.getXMLStreamReader(entityStream);
   }

   /**
    * FIXME Comment this
    * 
    * @param out
    * @return
    */
   protected XMLStreamWriter getXMLStreamWriter(OutputStream out)
   {
      return XMLStreamFactory.getXMLStreamWriter(out);
   }

}
