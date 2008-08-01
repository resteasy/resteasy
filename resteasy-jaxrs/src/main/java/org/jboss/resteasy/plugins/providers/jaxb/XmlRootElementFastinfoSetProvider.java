/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import org.jboss.resteasy.core.ExceptionAdapter;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A XmlRootElementFastinfoSetProvider.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@Produces("application/fastinfoset")
@Consumes("application/fastinfoset")
public class XmlRootElementFastinfoSetProvider extends JAXBXmlRootElementProvider
{

   /**
    *
    */
   @Override
   public Object readFrom(Class<Object> type,
                          Type genericType,
                          Annotation[] annotations,
                          MediaType mediaType,
                          MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream) throws IOException
   {
      InputStream in = new BufferedInputStream(entityStream, 2048);
      XMLStreamReader streamReader = new StAXDocumentParser(in);
      Unmarshaller u = getUnmarshaller(type, genericType, annotations, mediaType, httpHeaders);
      try
      {
         return u.unmarshal(streamReader);
      }
      catch (JAXBException e)
      {
         throw new ExceptionAdapter(e);
      }
   }

   /**
    *
    */
   @Override
   public void writeTo(Object t,
                       Class<?> type,
                       Type genericType,
                       Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream outputStream) throws IOException
   {
      try
      {
         BufferedOutputStream out = new BufferedOutputStream(outputStream, 2048);
         XMLStreamWriter writer = new StAXDocumentSerializer(out);
         Marshaller marshaller = getMarshaller(type, mediaType, httpHeaders);
         marshaller.marshal(t, writer);
      }
      catch (JAXBException e)
      {
         throw new ExceptionAdapter(e);
      }
   }

}
