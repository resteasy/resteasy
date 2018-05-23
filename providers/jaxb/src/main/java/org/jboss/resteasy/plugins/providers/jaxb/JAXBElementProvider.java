package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.util.NoContent;
import org.jboss.resteasy.util.Types;
import org.jboss.resteasy.resteasy_jaxrs.i18n.*;
import org.xml.sax.InputSource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * A JAXB Provider which handles parameter and return types of {@link JAXBElement}. This provider will be
 * selected when the resource is declared as:
 * </p>
 * <code>
 * &#064;POST<br>
 * &#064;Consumes("applictaion/xml")<br>
 * &#064;Produces("applictaion/xml")<br>
 * public JAXBElement&lt;Contact&gt; getContact(JAXBElement&lt;Contact&gt; value);<br>
 * </code>
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@Produces({"application/xml", "application/*+xml", "text/xml", "text/*+xml"})
@Consumes({"application/xml", "application/*+xml", "text/xml", "text/*+xml"})
public class JAXBElementProvider extends AbstractJAXBProvider<JAXBElement<?>>
{

   @Override
   protected boolean isReadWritable(Class<?> type,
                                    Type genericType,
                                    Annotation[] annotations,
                                    MediaType mediaType)
   {

      return JAXBElement.class.equals(type);
   }

   /**
    *
    */
   public JAXBElement<?> readFrom(Class<JAXBElement<?>> type,
                                  Type genericType,
                                  Annotation[] annotations,
                                  MediaType mediaType,
                                  MultivaluedMap<String, String> httpHeaders,
                                  InputStream entityStream) throws IOException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      NoContent.contentLengthCheck(httpHeaders);
      Class<?> typeArg = Object.class;
      if (genericType != null) typeArg = Types.getTypeArgument(genericType);
      JAXBContext jaxb = null;
      try
      {
         jaxb = findJAXBContext(typeArg, annotations, mediaType, true);
      }
      catch (JAXBException e)
      {
         throw new JAXBUnmarshalException(e);
      }
      JAXBElement<?> result;
      try
      {
         Unmarshaller unmarshaller = jaxb.createUnmarshaller();
         unmarshaller = decorateUnmarshaller(type, annotations, mediaType, unmarshaller);
         
         if (needsSecurity())
         {
            unmarshaller = new SecureUnmarshaller(unmarshaller, isDisableExternalEntities(), isEnableSecureProcessingFeature(), isDisableDTDs());
            SAXSource source = null;
            if (getCharset(mediaType) == null)
            {
               source = new SAXSource(new InputSource(new InputStreamReader(entityStream, StandardCharsets.UTF_8)));
            }
            else
            {
               source = new SAXSource(new InputSource(entityStream));
            }
            result = unmarshaller.unmarshal(source, (Class<?>) typeArg);
         }
         else
         {
            if (getCharset(mediaType) == null)
            {
               InputSource is = new InputSource(entityStream);
               is.setEncoding(StandardCharsets.UTF_8.name());
               StreamSource source = new StreamSource(new InputStreamReader(entityStream, StandardCharsets.UTF_8));
               source.setInputStream(entityStream);
               result = unmarshaller.unmarshal(source, (Class<?>) typeArg);
            }
            else
            {
               JAXBElement<?> e = unmarshaller.unmarshal(new StreamSource(entityStream), (Class<?>) typeArg);
               result = e;
            }
         };
      }
      catch (JAXBException e)
      {
         throw new JAXBUnmarshalException(e);
      }
      JAXBElement<?> element = result;
      return element;
   }

   @Override
   public void writeTo(JAXBElement<?> t,
                       Class<?> type,
                       Type genericType,
                       Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream outputStream) throws IOException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
      Class<?> typeArg = Object.class;
      if (genericType != null) typeArg = Types.getTypeArgument(genericType);
      super.writeTo(t, typeArg, genericType, annotations, mediaType, httpHeaders, outputStream);
   }

}
