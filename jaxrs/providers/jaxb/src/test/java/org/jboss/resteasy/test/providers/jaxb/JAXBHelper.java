package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.resteasy.core.ExceptionAdapter;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

/**
 * A JAXBHelper.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public final class JAXBHelper
{

   /**
    * An HTTP Header than can be passed in order to have the XML response formatted.
    */
   public static final String FORMAT_XML_HEADER = "X-Xml-Formatted";

   private JAXBHelper()
   {

   }

   /**
    * FIXME Comment this
    *
    * @param type
    * @param entityStream
    * @return
    */
   public static <T> JAXBElement<T> unmarshall(Class<T> type, InputStream entityStream)
   {
      XMLStreamReader reader = XMLStreamFactory.getXMLStreamReader(entityStream);
      return unmarshall(type, entityStream, reader);
   }

   /**
    * FIXME Comment this
    *
    * @param type
    * @param entityStream
    * @param reader
    * @return
    */
   public static <T> JAXBElement<T> unmarshall(Class<T> type,
                                               InputStream entityStream,
                                               XMLStreamReader reader)
   {
      JAXBContext jaxb = JAXBCache.instance().getJAXBContext(type);
      return unmarshall(jaxb, type, entityStream, reader);
   }

   /**
    * FIXME Comment this
    *
    * @param <T>
    * @param jaxb
    * @param type
    * @param entityStream
    * @param reader
    * @return
    */
   public static <T> JAXBElement<T> unmarshall(JAXBContext jaxb,
                                               Class<T> type,
                                               InputStream entityStream,
                                               XMLStreamReader reader)
   {
      try
      {
         Unmarshaller unmarshaller = jaxb.createUnmarshaller();
         JAXBElement<T> e = unmarshaller.unmarshal(reader, type);
         return e;
      }
      catch (JAXBException e)
      {
         throw new ExceptionAdapter(e);
      }
   }

   /**
    * FIXME Comment this
    *
    * @param mediaType
    * @return
    */
   public static String getCharset(final MediaType mediaType)
   {
      if (mediaType != null)
      {
         return mediaType.getParameters().get("charset");
      }
      return null;
   }


}
