/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 Oracle and/or its affiliates. All rights reserved.
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
package javax.ws.rs;

import java.util.Date;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;
import static javax.ws.rs.core.HttpHeaders.RETRY_AFTER;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

/**
 * A runtime exception indicating that the requested resource
 * {@link javax.ws.rs.core.Response.Status#SERVICE_UNAVAILABLE cannot be served}.
 *
 * @author Marek Potociar
 * @since 2.0
 */
public class ServiceUnavailableException extends ServerErrorException {

    private static final long serialVersionUID = 3821068205617492633L;

    /**
     * Construct a new "service unavailable" exception without any "Retry-After" information
     * specified for the failed request.
     */
    public ServiceUnavailableException() {
        super(Response.status(SERVICE_UNAVAILABLE).build());
    }

    /**
     * Construct a new "service unavailable" exception without any "Retry-After" information
     * specified for the failed request.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     */
    public ServiceUnavailableException(String message) {
        super(message, Response.status(SERVICE_UNAVAILABLE).build());
    }

    /**
     * Construct a new "service unavailable" exception with an interval specifying
     * the "Retry-After" information for the failed request.
     *
     * @param retryAfter decimal interval in seconds after which the failed request may be retried.
     */
    public ServiceUnavailableException(Long retryAfter) {
        super(Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build());
    }

    /**
     * Construct a new "service unavailable" exception with an interval specifying
     * the "Retry-After" information for the failed request.
     *
     * @param message    the detail message (which is saved for later retrieval
     *                   by the {@link #getMessage()} method).
     * @param retryAfter decimal interval in seconds after which the failed request may be retried.
     */
    public ServiceUnavailableException(String message, Long retryAfter) {
        super(message, Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build());
    }

    /**
     * Construct a new "service unavailable" exception with an interval specifying
     * the "Retry-After" information for the failed request.
     *
     * @param retryAfter a date/time after which the failed request may be retried.
     */
    public ServiceUnavailableException(Date retryAfter) {
        super(Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build());
    }

    /**
     * Construct a new "service unavailable" exception with an interval specifying
     * the "Retry-After" information for the failed request.
     *
     * @param message    the detail message (which is saved for later retrieval
     *                   by the {@link #getMessage()} method).
     * @param retryAfter a date/time after which the failed request may be retried.
     */
    public ServiceUnavailableException(String message, Date retryAfter) {
        super(message, Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build());
    }

    /**
     * Construct a new "service unavailable" exception.
     *
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 503}.
     */
    public ServiceUnavailableException(Response response) {
        super(validate(response, SERVICE_UNAVAILABLE));
    }

    /**
     * Construct a new "service unavailable" exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 503}.
     */
    public ServiceUnavailableException(String message, Response response) {
        super(message, validate(response, SERVICE_UNAVAILABLE));
    }

    /**
     * Construct a new "service unavailable" exception with a date specifying
     * the "Retry-After" information for the failed request and an underlying
     * request failure cause.
     *
     * @param retryAfter a date/time after which the failed request may be retried.
     * @param cause      the underlying cause of the exception.
     */
    public ServiceUnavailableException(Date retryAfter, Throwable cause) {
        super(Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build(), cause);
    }

    /**
     * Construct a new "service unavailable" exception with a date specifying
     * the "Retry-After" information for the failed request and an underlying
     * request failure cause.
     *
     * @param message    the detail message (which is saved for later retrieval
     *                   by the {@link #getMessage()} method).
     * @param retryAfter a date/time after which the failed request may be retried.
     * @param cause      the underlying cause of the exception.
     */
    public ServiceUnavailableException(String message, Date retryAfter, Throwable cause) {
        super(message, Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build(), cause);
    }

    /**
     * Construct a new "service unavailable" exception with an interval specifying
     * the "Retry-After" information for the failed request and an underlying
     * request failure cause.
     *
     * @param retryAfter decimal interval in seconds after which the failed request may be retried.
     * @param cause      the underlying cause of the exception.
     */
    public ServiceUnavailableException(Long retryAfter, Throwable cause) {
        super(Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build(), cause);
    }

    /**
     * Construct a new "service unavailable" exception with an interval specifying
     * the "Retry-After" information for the failed request and an underlying
     * request failure cause.
     *
     * @param message    the detail message (which is saved for later retrieval
     *                   by the {@link #getMessage()} method).
     * @param retryAfter decimal interval in seconds after which the failed request may be retried.
     * @param cause      the underlying cause of the exception.
     */
    public ServiceUnavailableException(String message, Long retryAfter, Throwable cause) {
        super(message, Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build(), cause);
    }

    /**
     * Construct a new "service unavailable" exception.
     *
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 503}.
     */
    public ServiceUnavailableException(Response response, Throwable cause) {
        super(validate(response, SERVICE_UNAVAILABLE), cause);
    }

    /**
     * Construct a new "service unavailable" exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 503}.
     */
    public ServiceUnavailableException(String message, Response response, Throwable cause) {
        super(message, validate(response, SERVICE_UNAVAILABLE), cause);
    }

    /**
     * Check if the underlying response contains the information on when is it
     * possible to {@link HttpHeaders#RETRY_AFTER retry the request}.
     *
     * @return {@code true} in case the retry time is specified in the underlying
     *         response, {@code false} otherwise.
     */
    public boolean hasRetryAfter() {
        return getResponse().getHeaders().containsKey(RETRY_AFTER);
    }

    /**
     * Get the retry time for the failed request.
     *
     * @param requestTime time of sending the original request that may be used to compute
     *                    the retry time (in case the retry time information specified as
     *                    a decimal interval in seconds).
     * @return time when the request may be retried or {@code null} if there is no retry
     *         information available.
     * @throws NullPointerException in case the {@code requestTime} parameter is {@code null}.
     */
    public Date getRetryTime(final Date requestTime) {
        final String value = getResponse().getHeaderString(RETRY_AFTER);
        if (value == null) {
            return null;
        }

        try {
            Long interval = Long.parseLong(value);
            return new Date(requestTime.getTime() + interval * 1000);
        } catch (NumberFormatException ex) {
            // not an decimal value; ignoring exception and parsing as date
        }

        final RuntimeDelegate.HeaderDelegate<Date> dateDelegate =
                RuntimeDelegate.getInstance().createHeaderDelegate(Date.class);
        return dateDelegate.fromString(value);
    }
}
