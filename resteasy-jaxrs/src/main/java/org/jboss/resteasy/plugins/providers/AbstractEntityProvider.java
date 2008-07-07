/**
 *
 */
package org.jboss.resteasy.plugins.providers;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * 
 * A AbstractEntityProvider.
 * 
 * @author <a href="ryan@damnhandy.com>Ryan J. McDonough</a>
 * @version $Revision: $
 * @param <T>
 */
public abstract class AbstractEntityProvider<T>
      implements  MessageBodyReader<T>, MessageBodyWriter<T>
{

   /**
    *
    */
   public long getSize(T t)
   {
      return -1;
   }
}
