package org.jboss.resteasy.spi;

import java.lang.annotation.Annotation;

import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.WriterInterceptor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResteasyAsynchronousResponse extends AsyncResponse {
    /**
     * Callback by the initial http request thread. It is used to help simulate suspend/resume asynchronous semantics
     * in containers that do not support asychronous HTTP. This method is a no-op in environments that support async HTTP.
     */
    void initialRequestThreadFinished();

    ContainerResponseFilter[] getResponseFilters();

    void setResponseFilters(ContainerResponseFilter[] responseFilters);

    WriterInterceptor[] getWriterInterceptors();

    void setWriterInterceptors(WriterInterceptor[] writerInterceptors);

    AsyncWriterInterceptor[] getAsyncWriterInterceptors();

    Annotation[] getAnnotations();

    void setAnnotations(Annotation[] annotations);

    void complete();

    void completionCallbacks(Throwable throwable);

}
