package org.jboss.resteasy.plugins.providers;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.jboss.resteasy.plugins.i18n.*;

import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Produces("*/*")
public class StreamingOutputProvider implements MessageBodyWriter<StreamingOutput>
{
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.StreamingOutputProvider , method call : isWriteable .")
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return StreamingOutput.class.isAssignableFrom(type);
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.StreamingOutputProvider , method call : getSize .")
   public long getSize(StreamingOutput streamingOutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.StreamingOutputProvider , method call : writeTo .")
   public void writeTo(StreamingOutput streamingOutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
      streamingOutput.write(entityStream);
   }
}
