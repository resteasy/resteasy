package org.jboss.resteasy.spi;

import org.jboss.resteasy.core.ResourceMethod;

import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.WriterInterceptor;
import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResteasyAsynchronousContext extends ExecutionContext
{
   boolean isSuspended();
   ResteasyAsynchronousResponse getAsyncResponse();
}
