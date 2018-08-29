package org.jboss.resteasy.plugins.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.util.HttpHeaderNames;

/**
 * @author <a href="mailto:bill@burkecentral.com">BillBurke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("*/*")
@Consumes("*/*")
public class InputStreamProvider implements MessageBodyReader<InputStream>, MessageBodyWriter<InputStream>
{
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type.equals(InputStream.class);
   }

   public InputStream readFrom(Class<InputStream> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      return entityStream;
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return InputStream.class.isAssignableFrom(type);
   }

   public long getSize(InputStream inputStream, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public void writeTo(InputStream inputStream, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
           LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
	   try
	   {
		   int c = inputStream.read();
		   if (c == -1)
		   {
			   httpHeaders.putSingle(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(0));
			   entityStream.write(new byte[0]); // fix RESTEASY-204
			   return;
		   }
		   else
			   entityStream.write(c);
         ProviderHelper.writeTo(inputStream, entityStream);
	   }
	   finally
	   {
         inputStream.close();
	   }
   }
}
