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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.jboss.resteasy.core.ExceptionAdapter;

/**
 * <p>
 * A JAXB Provider which handles parameter and return types of {@link JAXBElement}. This
 * provider will be select when the resource is declared as:
 * </p>
 * <code>
 * @POST
 * @ConsumeMime\("applictaion/xml")
 * @ProduceMime\("applictaion/xml")
 * public JAXBElement&lt;Contact&gt; getContact(JAXBElement&lt;Contact&gt; value);
 * </code>
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@ProduceMime(
{"text/xml", "application/xml"})
@ConsumeMime(
{"text/xml", "application/xml"})
public class JAXBElementProvider extends AbstractJAXBProvider<JAXBElement<?>>
{ 

   @Override
   protected boolean isReadWritable(Class<?> type, Type genericType, Annotation[] annotations)
   {
     
      return JAXBElement.class.equals(type);
   }

   public JAXBElement<?> readFrom(Class<JAXBElement<?>> type,
                                  Type genericType,
                                  Annotation[] annotations,
                                  MediaType mediaType,
                                  MultivaluedMap<String, String> httpHeaders,
                                  InputStream entityStream) throws IOException
   {
      ParameterizedType parameterizedType = (ParameterizedType) genericType;
      Class<?> typeArg = (Class<?>) parameterizedType.getActualTypeArguments()[0];
      JAXBElement<?> element;
      try
      {
         element = unmarshall(typeArg, entityStream);
      }
      catch (JAXBException e)
      {
         throw new ExceptionAdapter(e);
      }
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
      ParameterizedType parameterizedType = (ParameterizedType) genericType;
      Class<?> typeArg = (Class<?>) parameterizedType.getActualTypeArguments()[0];
      super.writeTo(t, typeArg, genericType, annotations, mediaType, httpHeaders, outputStream);
   }
   
   

}
