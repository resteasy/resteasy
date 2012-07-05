/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2012 Oracle and/or its affiliates. All rights reserved.
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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 * Uniform interface for synchronous invocation of HTTP methods.
 *
 * @author Marek Potociar
 * @since 2.0
 */
public interface SyncInvoker {

    // GET

    /**
     * Invoke HTTP GET method for the current request synchronously.
     *
     * @return invocation response.
     * @throws ClientException in case the invocation processing has failed.
     */
    Response get() throws ClientException;

    /**
     * Invoke HTTP GET method for the current request synchronously.
     *
     * @param <T>          response entity type.
     * @param responseType Java type the response entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T get(Class<T> responseType) throws ClientException, WebApplicationException;

    /**
     * Invoke HTTP GET method for the current request synchronously.
     *
     * @param <T>          generic response entity type.
     * @param responseType representation of a generic Java type the response
     *                     entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T get(GenericType<T> responseType) throws ClientException, WebApplicationException;

    // PUT

    /**
     * Invoke HTTP PUT method for the current request synchronously.
     *
     * @param entity request entity.
     * @return invocation response.
     * @throws ClientException in case the invocation processing has failed.
     */
    Response put(Entity<?> entity) throws ClientException;

    /**
     * Invoke HTTP PUT method for the current request synchronously.
     *
     * @param <T>          response entity type.
     * @param entity       request entity.
     * @param responseType Java type the response entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T put(Entity<?> entity, Class<T> responseType) throws ClientException, WebApplicationException;

    /**
     * Invoke HTTP PUT method for the current request synchronously.
     *
     * @param <T>          generic response entity type.
     * @param entity       request entity.
     * @param responseType representation of a generic Java type the response
     *                     entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T put(Entity<?> entity, GenericType<T> responseType) throws ClientException, WebApplicationException;

    // POST

    /**
     * Invoke HTTP POST method for the current request synchronously.
     *
     * @param entity request entity.
     * @return invocation response.
     * @throws ClientException in case the invocation processing has failed.
     */
    Response post(Entity<?> entity) throws ClientException;

    /**
     * Invoke HTTP POST method for the current request synchronously.
     *
     * @param <T>          response entity type.
     * @param entity       request entity.
     * @param responseType Java type the response entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T post(Entity<?> entity, Class<T> responseType) throws ClientException, WebApplicationException;

    /**
     * Invoke HTTP POST method for the current request synchronously.
     *
     * @param <T>          generic response entity type.
     * @param entity       request entity.
     * @param responseType representation of a generic Java type the response
     *                     entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T post(Entity<?> entity, GenericType<T> responseType) throws ClientException, WebApplicationException;

    // DELETE

    /**
     * Invoke HTTP DELETE method for the current request synchronously.
     *
     * @return invocation response.
     * @throws ClientException in case the invocation processing has failed.
     */
    Response delete() throws ClientException;

    /**
     * Invoke HTTP DELETE method for the current request synchronously.
     *
     * @param <T>          response entity type.
     * @param responseType Java type the response entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T delete(Class<T> responseType) throws ClientException, WebApplicationException;

    /**
     * Invoke HTTP DELETE method for the current request synchronously.
     *
     * @param <T>          generic response entity type.
     * @param responseType representation of a generic Java type the response
     *                     entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T delete(GenericType<T> responseType) throws ClientException, WebApplicationException;

    // HEAD

    /**
     * Invoke HTTP HEAD method for the current request synchronously.
     *
     * @return invocation response.
     * @throws ClientException in case the invocation processing has failed.
     */
    Response head() throws ClientException;

    // OPTIONS

    /**
     * Invoke HTTP OPTIONS method for the current request synchronously.
     *
     * @return invocation response.
     * @throws ClientException in case the invocation processing has failed.
     */
    Response options() throws ClientException;

    /**
     * Invoke HTTP OPTIONS method for the current request synchronously.
     *
     * @param <T>          response entity type.
     * @param responseType Java type the response entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T options(Class<T> responseType) throws ClientException, WebApplicationException;

    /**
     * Invoke HTTP OPTIONS method for the current request synchronously.
     *
     * @param <T>          generic response entity type.
     * @param responseType representation of a generic Java type the response
     *                     entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T options(GenericType<T> responseType) throws ClientException, WebApplicationException;

    // TRACE

    /**
     * Invoke HTTP TRACE method for the current request synchronously.
     *
     * @return invocation response.
     * @throws ClientException in case the invocation processing has failed.
     */
    Response trace() throws ClientException;

    /**
     * Invoke HTTP TRACE method for the current request synchronously.
     *
     * @param <T>          response entity type.
     * @param responseType Java type the response entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T trace(Class<T> responseType) throws ClientException, WebApplicationException;

    /**
     * Invoke HTTP TRACE method for the current request synchronously.
     *
     * @param <T>          generic response entity type.
     * @param responseType representation of a generic Java type the response
     *                     entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T trace(GenericType<T> responseType) throws ClientException, WebApplicationException;

    // ARBITRARY METHOD

    /**
     * Invoke an arbitrary method for the current request synchronously.
     *
     * @param name method name.
     * @return invocation response.
     * @throws ClientException in case the invocation processing has failed.
     */
    Response method(String name) throws ClientException;

    /**
     * Invoke an arbitrary method for the current request synchronously.
     *
     * @param <T>          response entity type.
     * @param name         method name.
     * @param responseType Java type the response entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T method(String name, Class<T> responseType) throws ClientException, WebApplicationException;

    /**
     * Invoke an arbitrary method for the current request synchronously.
     *
     * @param <T>          generic response entity type.
     * @param name         method name.
     * @param responseType representation of a generic Java type the response
     *                     entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T method(String name, GenericType<T> responseType) throws ClientException, WebApplicationException;

    /**
     * Invoke an arbitrary method for the current request synchronously.
     *
     * @param name   method name.
     * @param entity request entity.
     * @return invocation response.
     * @throws ClientException in case the invocation processing has failed.
     */
    Response method(String name, Entity<?> entity) throws ClientException;

    /**
     * Invoke an arbitrary method for the current request synchronously.
     *
     * @param <T>          response entity type.
     * @param name         method name.
     * @param entity       request entity.
     * @param responseType Java type the response entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T method(String name, Entity<?> entity, Class<T> responseType) throws ClientException, WebApplicationException;

    /**
     * Invoke an arbitrary method for the current request synchronously.
     *
     * @param <T>          generic response entity type.
     * @param name         method name.
     * @param entity       request entity.
     * @param responseType representation of a generic Java type the response
     *                     entity will be converted to.
     * @return invocation response.
     * @throws ClientException         in case the invocation processing has failed.
     * @throws WebApplicationException in case the response status code of the response
     *                                 returned by the server is not
     *                                 {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL
     *                                 successful}.
     */
    <T> T method(String name, Entity<?> entity, GenericType<T> responseType) throws ClientException, WebApplicationException;
}
