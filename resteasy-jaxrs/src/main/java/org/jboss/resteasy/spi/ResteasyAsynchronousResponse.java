package org.jboss.resteasy.spi;

import org.jboss.resteasy.core.ResourceMethodInvoker;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.WriterInterceptor;
import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResteasyAsynchronousResponse extends AsyncResponse
{
   /**
     * Callback by the initial http request thread.  It is used to help simulate suspend/resume asynchronous semantics
     * in containers that do not support asychronous HTTP. This method is a no-op in environments that support async HTTP.
     */
    public void initialRequestThreadFinished();

    ContainerResponseFilter[] getResponseFilters();

    void setResponseFilters(ContainerResponseFilter[] responseFilters);

    WriterInterceptor[] getWriterInterceptors();

    void setWriterInterceptors(WriterInterceptor[] writerInterceptors);

    Annotation[] getAnnotations();

    void setAnnotations(Annotation[] annotations);

    ResourceMethodInvoker getMethod();

    void setMethod(ResourceMethodInvoker method);

    void complete();
    
    void completionCallbacks(Throwable throwable);
}
