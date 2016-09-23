/**
 *
 */
package org.jboss.resteasy.plugins.providers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.Logger.Level;

/**
 * A AbstractEntityProvider.
 *
 * @param <T>
 * @author <a href="ryan@damnhandy.com>Ryan J. McDonough</a>
 * @version $Revision: $
 */
public abstract class AbstractEntityProvider<T>
        implements MessageBodyReader<T>, MessageBodyWriter<T>
{

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider : org.jboss.resteasy.plugins.providers.AbstractEntityProvider , method call : getSize .")
   public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

}
