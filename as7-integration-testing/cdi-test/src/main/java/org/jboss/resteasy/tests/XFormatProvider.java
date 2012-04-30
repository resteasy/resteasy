package org.jboss.resteasy.tests;

import org.jboss.resteasy.util.ReadFromStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Tests to make sure that a CDI bean was injected and that this provider overrides the default XML provider
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
//@Produces("application/x-format")
//@Consumes("application/x-format")
@Produces("application/xml")
@Consumes("application/xml")
public class XFormatProvider implements MessageBodyReader<XFormat>, MessageBodyWriter<XFormat>
{
   @Inject
   MyConfigBean bean;

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return XFormat.class.isAssignableFrom(type);
   }

   @Override
   public XFormat readFrom(Class<XFormat> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
   {
      System.out.println("********** readFrom ********");
      if (bean != null)
      {
         System.out.println("XFormatProvider: MyConfigBean version: " + bean.version());
      }
      byte[] bytes = ReadFromStream.readFromStream(1024, entityStream);
      String val = new String(bytes);
      String[] split = val.split(" ");
      return new XFormat(split[0], split[1]);
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return XFormat.class.isAssignableFrom(type);
   }

   @Override
   public long getSize(XFormat xFormat, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   @Override
   public void writeTo(XFormat xFormat, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      System.out.println("********** writeTo ********");
      String message = "";
      if (bean != null)
      {
         System.out.println("XFormatProvider: MyConfigBean version: " + bean.version());
         message += xFormat.getId() + " " + bean.version();
      }
      else
      {
         message += xFormat.getId() + " 0";
      }
      entityStream.write(message.getBytes());
   }
}
