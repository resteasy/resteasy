package org.jboss.resteasy.spi;

import javax.ws.rs.core.ExecutionContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResteasyAsynchronousContext extends ExecutionContext
{
   boolean isSuspended();
   ResteasyAsynchronousResponse getAsyncResponse();
}
