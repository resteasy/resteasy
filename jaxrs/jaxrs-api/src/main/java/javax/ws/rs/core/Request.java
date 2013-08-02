/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2013 Oracle and/or its affiliates. All rights reserved.
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
package javax.ws.rs.core;

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * An injectable helper for request processing, all methods throw an
 * {@link IllegalStateException} if called outside the scope of a request
 * (e.g. from a provider constructor).
 *
 * Precondition processing (see the {@code evaluatePreconditions} methods)
 * can result in either a {@code null} return value to indicate that
 * preconditions have been met and that the request should continue, or
 * a non-{@code null} return value to indicate that preconditions were not met. In the
 * event that preconditions were not met, the returned {@code ResponseBuilder}
 * instance will have an appropriate status and will also include a {@code Vary}
 * header if the {@link #selectVariant(List)} method was called prior to to calling
 * {@code evaluatePreconditions}. It is the responsibility of the caller
 * to check the status and add additional metadata if required. E.g., see
 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5">HTTP/1.1, section 10.3.5</a>
 * for details of the headers that are expected to accompany a {@code 304 Not Modified}
 * response.
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @author Marek Potociar
 * @since 1.0
 */
public interface Request {

    /**
     * Get the request method, e.g. GET, POST, etc.
     *
     * @return the request method.
     * @see javax.ws.rs.HttpMethod
     */
    public String getMethod();

    /**
     * Select the representation variant that best matches the request. Returns
     * {@code null} in case there is no matching variant in the list.
     * <p>
     * More explicit variants are chosen ahead of less explicit ones. A vary header
     * is computed from the supplied list and automatically added to the  response.
     * </p>
     *
     * @param variants a list of Variant that describe all of the available representation
     *                 variants.
     * @return the variant that best matches the request or {@code null} if there's no match.
     * @throws java.lang.IllegalArgumentException
     *          if variants is empty or {@code null}.
     * @throws java.lang.IllegalStateException
     *          if called outside the scope of a request.
     * @see Variant.VariantListBuilder
     */
    public Variant selectVariant(List<Variant> variants);

    /**
     * Evaluate request preconditions based on the passed in value.
     *
     * @param eTag an ETag for the current state of the resource
     * @return {@code null} if the preconditions are met or a {@code ResponseBuilder} set with
     *         the appropriate status if the preconditions are not met. A returned
     *         {@code ResponseBuilder} will include an ETag header set with the value of eTag,
     *         provided none of the precondition evaluation has failed, in which case
     *         the ETag header would not be included and the status code of the returned
     *         {@code ResponseBuilder} would be set to {@link Response.Status#PRECONDITION_FAILED}.
     * @throws java.lang.IllegalArgumentException
     *          if eTag is {@code null}.
     * @throws java.lang.IllegalStateException
     *          if called outside the scope of a request.
     */
    public ResponseBuilder evaluatePreconditions(EntityTag eTag);

    /**
     * Evaluate request preconditions based on the passed in value.
     *
     * @param lastModified a date that specifies the modification date of the resource
     * @return {@code null} if the preconditions are met or a {@code ResponseBuilder} set with
     *         the appropriate status if the preconditions are not met.
     * @throws java.lang.IllegalArgumentException
     *          if lastModified is {@code null}.
     * @throws java.lang.IllegalStateException
     *          if called outside the scope of a request.
     */
    public ResponseBuilder evaluatePreconditions(Date lastModified);

    /**
     * Evaluate request preconditions based on the passed in value.
     *
     * @param lastModified a date that specifies the modification date of the resource
     * @param eTag         an ETag for the current state of the resource
     * @return {@code null} if the preconditions are met or a {@code ResponseBuilder} set with
     *         the appropriate status if the preconditions are not met. A returned
     *         {@code ResponseBuilder} will include an ETag header set with the value of eTag,
     *         provided none of the precondition evaluation has failed, in which case
     *         the ETag header would not be included and the status code of the returned
     *         {@code ResponseBuilder} would be set to {@link Response.Status#PRECONDITION_FAILED}.
     * @throws java.lang.IllegalArgumentException
     *          if lastModified or eTag is {@code null}.
     * @throws java.lang.IllegalStateException
     *          if called outside the scope of a request.
     */
    public ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag);

    /**
     * Evaluate request preconditions for a resource that does not currently
     * exist. The primary use of this method is to support the {@link <a
     * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.24">
     * If-Match: *</a>} and {@link <a
     * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.26">
     * If-None-Match: *</a>} preconditions.
     *
     * <p>Note that both preconditions {@code If-None-Match: *} and
     * <code>If-None-Match: <i>something</i></code> will always be considered to
     * have been met and it is the applications responsibility
     * to enforce any additional method-specific semantics. E.g. a
     * {@code PUT} on a resource that does not exist might succeed whereas
     * a {@code GET} on a resource that does not exist would likely result
     * in a 404 response. It would be the responsibility of the application to
     * generate the 404 response.</p>
     *
     * @return {@code null} if the preconditions are met or a {@code ResponseBuilder} set with
     *         the appropriate status if the preconditions are not met.
     * @throws IllegalStateException if called outside the scope of a request.
     * @since 1.1
     */
    public ResponseBuilder evaluatePreconditions();
}
