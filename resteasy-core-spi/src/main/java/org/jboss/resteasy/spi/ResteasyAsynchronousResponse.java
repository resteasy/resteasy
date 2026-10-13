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

    /**
     * Invoked when the asynchronous processing of the request has ended, either after the response has been written,
     * after a failure or because the client connection was closed. Implementations must invoke this when the request
     * ends, including when the connection is closed, and invoking it more than once must have no effect.
     * <p>
     * This notifies the registered {@link jakarta.ws.rs.container.CompletionCallback completion callbacks} and completes
     * the request, which notifies the {@link RequestListener}s. Completing the request is provided by
     * {@code org.jboss.resteasy.core.AbstractAsynchronousResponse}; implementations which do not extend it do not notify
     * the {@link RequestListener}s.
     * </p>
     *
     * @param throwable the failure which ended the request or {@code null} if the request completed successfully
     */
    void completionCallbacks(Throwable throwable);

}
