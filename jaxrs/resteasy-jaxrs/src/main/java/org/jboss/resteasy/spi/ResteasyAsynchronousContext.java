package org.jboss.resteasy.spi;

import javax.ws.rs.container.AsyncResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResteasyAsynchronousContext
{
   boolean isSuspended();
   ResteasyAsynchronousResponse getAsyncResponse();

   AsyncResponse suspend() throws IllegalStateException;

   AsyncResponse suspend(long millis) throws IllegalStateException;

   AsyncResponse suspend(long time, TimeUnit unit) throws IllegalStateException;
}
