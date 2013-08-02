/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package javax.ws.rs.client;

import java.util.Locale;
import java.util.concurrent.Future;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * A client request invocation.
 *
 * An invocation is a request that has been prepared and is ready for execution.
 * Invocations provide a generic (command) interface that enables a separation of
 * concerns between the creator and the submitter. In particular, the submitter
 * does not need to know how the invocation was prepared, but only how it should
 * be executed (synchronously or asynchronously) and when.
 *
 * @author Marek Potociar
 * @see Invocation.Builder Invocation.Builder
 */
public interface Invocation {

    /**
     * A client request invocation builder.
     *
     * The builder, obtained via a call to one of the {@code request(...)}
     * methods on a {@link WebTarget resource target}, provides methods for
     * preparing a client request invocation. Once the request is prepared
     * the invocation builder can be either used to build an {@link Invocation}
     * with a generic execution interface:
     * <pre>
     *   Client client = ClientBuilder.newClient();
     *   WebTarget resourceTarget = client.target("http://examples.jaxrs.com/");
     *
     *   // Build a HTTP GET request that accepts "text/plain" response type
     *   // and contains a custom HTTP header entry "Foo: bar".
     *   Invocation invocation = resourceTarget.request("text/plain")
     *           .header("Foo", "bar").buildGet();
     *
     *   // Invoke the request using generic interface
     *   String response = invocation.invoke(String.class);
     * </pre>
     * Alternatively, one of the inherited {@link SyncInvoker synchronous invocation
     * methods} can be used to invoke the prepared request and return the server
     * response in a single step, e.g.:
     * <pre>
     *   Client client = ClientBuilder.newClient();
     *   WebTarget resourceTarget = client.target("http://examples.jaxrs.com/");
     *
     *   // Build and invoke the get request in a single step
     *   String response = resourceTarget.request("text/plain")
     *           .header("Foo", "bar").get(String.class);
     * </pre>
     * Once the request is fully prepared for invoking, switching to an
     * {@link AsyncInvoker asynchronous invocation} mode is possible by
     * calling the {@link #async() } method on the builder, e.g.:
     * <pre>
     *   Client client = ClientBuilder.newClient();
     *   WebTarget resourceTarget = client.target("http://examples.jaxrs.com/");
     *
     *   // Build and invoke the get request asynchronously in a single step
     *   Future<String> response = resourceTarget.request("text/plain")
     *           .header("Foo", "bar").async().get(String.class);
     * </pre>
     */
    public static interface Builder extends SyncInvoker {

        // Invocation builder methods

        /**
         * Build a request invocation using an arbitrary request method name.
         *
         * @param method request method name.
         * @return invocation encapsulating the built request.
         */
        public Invocation build(String method);

        /**
         * Build a request invocation using an arbitrary request method name and
         * request entity.
         *
         * @param method request method name.
         * @param entity request entity, including it's full {@link javax.ws.rs.core.Variant} information.
         *               Any variant-related HTTP headers previously set (namely {@code Content-Type},
         *               {@code Content-Language} and {@code Content-Encoding}) will be overwritten using
         *               the entity variant information.
         * @return invocation encapsulating the built request.
         */
        public Invocation build(String method, Entity<?> entity);

        /**
         * Build a GET request invocation.
         *
         * @return invocation encapsulating the built GET request.
         */
        public Invocation buildGet();

        /**
         * Build a DELETE request invocation.
         *
         * @return invocation encapsulating the built DELETE request.
         */
        public Invocation buildDelete();

        /**
         * Build a POST request invocation.
         *
         * @param entity request entity, including it's full {@link javax.ws.rs.core.Variant} information.
         *               Any variant-related HTTP headers previously set (namely {@code Content-Type},
         *               {@code Content-Language} and {@code Content-Encoding}) will be overwritten using
         *               the entity variant information.
         * @return invocation encapsulating the built POST request.
         */
        public Invocation buildPost(Entity<?> entity);

        /**
         * Build a PUT request invocation.
         *
         * @param entity request entity, including it's full {@link javax.ws.rs.core.Variant} information.
         *               Any variant-related HTTP headers previously set (namely {@code Content-Type},
         *               {@code Content-Language} and {@code Content-Encoding}) will be overwritten using
         *               the entity variant information.
         * @return invocation encapsulating the built PUT request.
         */
        public Invocation buildPut(Entity<?> entity);

        /**
         * Access the asynchronous uniform request invocation interface to
         * asynchronously invoke the built request.
         *
         * @return asynchronous uniform request invocation interface.
         */
        public AsyncInvoker async();

        /**
         * Add the accepted response media types.
         *
         * @param mediaTypes accepted response media types.
         * @return the updated builder.
         */
        public Builder accept(String... mediaTypes);

        /**
         * Add the accepted response media types.
         *
         * @param mediaTypes accepted response media types.
         * @return the updated builder.
         */
        public Builder accept(MediaType... mediaTypes);

        /**
         * Add acceptable languages.
         *
         * @param locales an array of the acceptable languages.
         * @return the updated builder.
         */
        public Builder acceptLanguage(Locale... locales);

        /**
         * Add acceptable languages.
         *
         * @param locales an array of the acceptable languages.
         * @return the updated builder.
         */
        public Builder acceptLanguage(String... locales);

        /**
         * Add acceptable encodings.
         *
         * @param encodings an array of the acceptable encodings.
         * @return the updated builder.
         */
        public Builder acceptEncoding(String... encodings);

        /**
         * Add a cookie to be set.
         *
         * @param cookie to be set.
         * @return the updated builder.
         */
        public Builder cookie(Cookie cookie);

        /**
         * Add a cookie to be set.
         *
         * @param name  the name of the cookie.
         * @param value the value of the cookie.
         * @return the updated builder.
         */
        public Builder cookie(String name, String value);

        /**
         * Set the cache control data of the message.
         *
         * @param cacheControl the cache control directives, if {@code null}
         *                     any existing cache control directives will be removed.
         * @return the updated builder.
         */
        public Builder cacheControl(CacheControl cacheControl);

        /**
         * Add an arbitrary header.
         *
         * @param name  the name of the header
         * @param value the value of the header, the header will be serialized
         *              using a {@link javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate} if
         *              one is available via {@link javax.ws.rs.ext.RuntimeDelegate#createHeaderDelegate(java.lang.Class)}
         *              for the class of {@code value} or using its {@code toString} method
         *              if a header delegate is not available. If {@code value} is {@code null}
         *              then all current headers of the same name will be removed.
         * @return the updated builder.
         */
        public Builder header(String name, Object value);

        /**
         * Replaces all existing headers with the newly supplied headers.
         *
         * @param headers new headers to be set, if {@code null} all existing
         *                headers will be removed.
         * @return the updated builder.
         */
        public Builder headers(MultivaluedMap<String, Object> headers);

        /**
         * Set a new property in the context of a request represented by this invocation builder.
         * <p>
         * The property is available for a later retrieval via {@link ClientRequestContext#getProperty(String)}
         * or {@link javax.ws.rs.ext.InterceptorContext#getProperty(String)}.
         * If a property with a given name is already set in the request context,
         * the existing value of the property will be updated.
         * Setting a {@code null} value into a property effectively removes the property
         * from the request property bag.
         * </p>
         *
         * @param name  property name.
         * @param value (new) property value. {@code null} value removes the property
         *              with the given name.
         * @return the updated builder.
         * @see Invocation#property(String, Object)
         */
        public Builder property(String name, Object value);
    }

    /**
     * Set a new property in the context of a request represented by this invocation.
     * <p>
     * The property is available for a later retrieval via {@link ClientRequestContext#getProperty(String)}
     * or {@link javax.ws.rs.ext.InterceptorContext#getProperty(String)}.
     * If a property with a given name is already set in the request context,
     * the existing value of the property will be updated.
     * Setting a {@code null} value into a property effectively removes the property
     * from the request property bag.
     * </p>
     *
     * @param name  property name.
     * @param value (new) property value. {@code null} value removes the property
     *              with the given name.
     * @return the updated invocation.
     * @see Invocation.Builder#property(String, Object)
     */
    public Invocation property(String name, Object value);

    /**
     * Synchronously invoke the request and receive a response back.
     *
     * @return {@link Response response} object as a result of the request
     *         invocation.
     * @throws ResponseProcessingException in case processing of a received HTTP response fails (e.g. in a filter
     *                                     or during conversion of the response entity data to an instance
     *                                     of a particular Java type).
     * @throws ProcessingException         in case the request processing or subsequent I/O operation fails.
     */
    public Response invoke();

    /**
     * Synchronously invoke the request and receive a response of the specified
     * type back.
     *
     * @param <T>          response type
     * @param responseType Java type the response should be converted into.
     * @return response object of the specified type as a result of the request
     *         invocation.
     * @throws ResponseProcessingException in case processing of a received HTTP response fails (e.g. in a filter
     *                                     or during conversion of the response entity data to an instance
     *                                     of a particular Java type).
     * @throws ProcessingException         in case the request processing or subsequent I/O operation fails.
     * @throws WebApplicationException     in case the response status code of the response
     *                                     returned by the server is not
     *                                     {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                     successful}.
     */
    public <T> T invoke(Class<T> responseType);

    /**
     * Synchronously invoke the request and receive a response of the specified
     * generic type back.
     *
     * @param <T>          generic response type
     * @param responseType type literal representing a generic Java type the
     *                     response should be converted into.
     * @return response object of the specified generic type as a result of the
     *         request invocation.
     * @throws ResponseProcessingException in case processing of a received HTTP response fails (e.g. in a filter
     *                                     or during conversion of the response entity data to an instance
     *                                     of a particular Java type).
     * @throws ProcessingException         in case the request processing or subsequent I/O operation fails.
     * @throws WebApplicationException     in case the response status code of the response
     *                                     returned by the server is not
     *                                     {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                     successful}.
     */
    public <T> T invoke(GenericType<T> responseType);

    /**
     * Submit the request for an asynchronous invocation and receive a future
     * response back.
     * <p>
     * Note that calling the {@link java.util.concurrent.Future#get()} method on the returned
     * {@code Future} instance may throw an {@link java.util.concurrent.ExecutionException}
     * that wraps a {@link ProcessingException} thrown in case of an invocation processing
     * failure.
     * In case a processing of a properly received response fails, the wrapped processing exception
     * will be of {@link ResponseProcessingException} type and will contain the {@link Response}
     * instance whose processing has failed.
     * </p>
     *
     * @return future {@link Response response} object as a result of the request
     *         invocation.
     */
    public Future<Response> submit();

    /**
     * Submit the request for an asynchronous invocation and receive a future
     * response of the specified type back.
     * <p>
     * Note that calling the {@link java.util.concurrent.Future#get()} method on the returned
     * {@code Future} instance may throw an {@link java.util.concurrent.ExecutionException}
     * that wraps either a {@link ProcessingException} thrown in case of an invocation processing
     * failure or a {@link WebApplicationException} or one of its subclasses thrown in case the
     * received response status code is not {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     * successful} and the specified response type is not {@link javax.ws.rs.core.Response}.
     * In case a processing of a properly received response fails, the wrapped processing exception
     * will be of {@link ResponseProcessingException} type and will contain the {@link Response}
     * instance whose processing has failed.
     * </p>
     *
     * @param <T>          response type
     * @param responseType Java type the response should be converted into.
     * @return future response object of the specified type as a result of the
     *         request invocation.
     */
    public <T> Future<T> submit(Class<T> responseType);

    /**
     * Submit the request for an asynchronous invocation and receive a future
     * response of the specified generic type back.
     * <p>
     * Note that calling the {@link java.util.concurrent.Future#get()} method on the returned
     * {@code Future} instance may throw an {@link java.util.concurrent.ExecutionException}
     * that wraps either a {@link ProcessingException} thrown in case of an invocation processing
     * failure or a {@link WebApplicationException} or one of its subclasses thrown in case the
     * received response status code is not {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     * successful} and the specified response type is not {@link javax.ws.rs.core.Response}.
     * In case a processing of a properly received response fails, the wrapped processing exception
     * will be of {@link ResponseProcessingException} type and will contain the {@link Response}
     * instance whose processing has failed.
     * </p>
     *
     * @param <T>          generic response type
     * @param responseType type literal representing a generic Java type the
     *                     response should be converted into.
     * @return future response object of the specified generic type as a result
     *         of the request invocation.
     */
    public <T> Future<T> submit(GenericType<T> responseType);

    /**
     * Submit the request for an asynchronous invocation and register an
     * {@link InvocationCallback} to process the future result of the invocation.
     * <p>
     * Note that calling the {@link java.util.concurrent.Future#get()} method on the returned
     * {@code Future} instance may throw an {@link java.util.concurrent.ExecutionException}
     * that wraps either a {@link ProcessingException} thrown in case of an invocation processing
     * failure or a {@link WebApplicationException} or one of its subclasses thrown in case the
     * received response status code is not {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     * successful} and the generic type of the supplied response callback is not
     * {@link javax.ws.rs.core.Response}.
     * In case a processing of a properly received response fails, the wrapped processing exception
     * will be of {@link ResponseProcessingException} type and will contain the {@link Response}
     * instance whose processing has failed.
     * </p>
     *
     * @param <T>      response type
     * @param callback invocation callback for asynchronous processing of the
     *                 request invocation result.
     * @return future response object of the specified type as a result of the
     *         request invocation.
     */
    public <T> Future<T> submit(InvocationCallback<T> callback);
}
