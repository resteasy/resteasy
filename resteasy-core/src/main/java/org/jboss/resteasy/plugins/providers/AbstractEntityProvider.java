/**
 *
 */
package org.jboss.resteasy.plugins.providers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.MessageBodyReader;

import org.jboss.resteasy.spi.AsyncMessageBodyWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A AbstractEntityProvider.
 *
 * @param <T> type
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: $
 */
public abstract class AbstractEntityProvider<T>
      implements MessageBodyReader<T>, AsyncMessageBodyWriter<T>
{

   public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

}
