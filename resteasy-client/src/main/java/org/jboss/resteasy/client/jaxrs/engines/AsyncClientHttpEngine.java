package org.jboss.resteasy.client.jaxrs.engines;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import jakarta.ws.rs.client.InvocationCallback;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;

/**
 * Interface for an async HttpClientEngine
 *
 * @author Markus Kull
 */
public interface AsyncClientHttpEngine extends ClientHttpEngine {

    /**
     * Interface for extracting a result out of a ClientResponse
     *
     * @param <T> Result-Type
     */
    public interface ResultExtractor<T> {
        /**
         * Extracts a result out of a Response
         *
         * @param response Response
         * @return result
         */
        T extractResult(ClientResponse response);
    }

    /**
     * Submits an asynchronous request.
     *
     * @param <T>       type
     * @param request   Request
     * @param buffered  buffer the response?
     * @param callback  Optional callback receiving the result, which is run inside the io-thread. may be null.
     * @param extractor ResultExtractor for extracting a result out of a ClientResponse. Is run inside the io-thread
     * @return Future with the result or Exception
     */
    <T> Future<T> submit(ClientInvocation request, boolean buffered, InvocationCallback<T> callback,
            ResultExtractor<T> extractor);

    /**
     * Submits an asynchronous request.
     *
     * @param <T>       type
     * @param request   Request
     * @param buffered  buffer the response?
     * @param extractor ResultExtractor for extracting a result out of a ClientResponse. Is run inside the io-thread
     * @return {@link CompletableFuture} with the result or Exception
     */
    default <T> CompletableFuture<T> submit(ClientInvocation request, boolean buffered, ResultExtractor<T> extractor) {
        return submit(request, buffered, extractor, null);
    }

    /**
     * Submits an asynchronous request.
     *
     * @param <T>             type
     * @param request         Request
     * @param buffered        buffer the response?
     * @param extractor       ResultExtractor for extracting a result out of a ClientResponse. Is run inside the io-thread
     * @param executorService the executor to use for asynchronous execution
     * @return {@link CompletableFuture} with the result or Exception
     */
    <T> CompletableFuture<T> submit(ClientInvocation request, boolean buffered, ResultExtractor<T> extractor,
            ExecutorService executorService);

}
