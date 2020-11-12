/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2020 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.client.exception;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * An interface which allows a {@link WebApplicationException} to be unwrapped.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public interface WebApplicationExceptionWrapper<T extends WebApplicationException> {

    /**
     * If the {@code resteasy.original.webapplicationexception.behavior} is set to {@code true} or the request is
     * determined to not be a server side request, then the {@link WebApplicationException} passed in will be returned.
     * If the property is not set to {@code true} and this is a server side request then the exception is wrapped and
     * the response is {@linkplain #sanitize(Response) sanitized}.
     *
     * @param e the exception to possibly wrapped
     *
     * @return the wrapped exception or the original exception if the exception has already been wrapped the the
     * wrapping feature is turned off
     */
    static WebApplicationException wrap(final WebApplicationException e) {
        ResteasyConfiguration config = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
        boolean originalBehavior = false;
        try {
            final String s = config.getParameter(ResteasyContextParameters.RESTEASY_ORIGINAL_WEBAPPLICATIONEXCEPTION_BEHAVIOR);
            originalBehavior = (s != null && (s.isEmpty() || Boolean.parseBoolean(s)));
        } catch (Exception ignore) {
            // nothing to do, assume false
        }
        final boolean serverSide = ResteasyProviderFactory.searchContextData(Dispatcher.class) != null;
        if (originalBehavior || !serverSide) {
            return e;
        }
        if (e instanceof WebApplicationExceptionWrapper) {
            return e;
        }
        if (e instanceof BadRequestException) {
            return new ResteasyBadRequestException((BadRequestException) e);
        }
        if (e instanceof NotAuthorizedException) {
            return new ResteasyNotAuthorizedException((NotAuthorizedException) e);
        }
        if (e instanceof ForbiddenException) {
            return new ResteasyForbiddenException((ForbiddenException) e);
        }
        if (e instanceof NotFoundException) {
            return new ResteasyNotFoundException((NotFoundException) e);
        }
        if (e instanceof NotAllowedException) {
            return new ResteasyNotAllowedException((NotAllowedException) e);
        }
        if (e instanceof NotAcceptableException) {
            return new ResteasyNotAcceptableException((NotAcceptableException) e);
        }
        if (e instanceof NotSupportedException) {
            return new ResteasyNotSupportedException((NotSupportedException) e);
        }
        if (e instanceof InternalServerErrorException) {
            return new ResteasyInternalServerErrorException((InternalServerErrorException) e);
        }
        if (e instanceof ServiceUnavailableException) {
            return new ResteasyServiceUnavailableException((ServiceUnavailableException) e);
        }
        if (e instanceof ClientErrorException) {
            return new ResteasyClientErrorException((ClientErrorException) e);
        }
        if (e instanceof ServerErrorException) {
            return new ResteasyServerErrorException((ServerErrorException) e);
        }
        if (e instanceof RedirectionException) {
            return new ResteasyRedirectionException((RedirectionException) e);
        }

        return new ResteasyWebApplicationException(e);
    }

    /**
     * Unwraps the exception if the passed in expression is a {@link WebApplicationExceptionWrapper}. Otherwise
     * the exception passed in is returned.
     *
     * @param e the exception to unwrap
     *
     * @return the unwrapped exception or the exception parameter itself if it was not a {@link WebApplicationExceptionWrapper}
     */
    static WebApplicationException unwrap(final WebApplicationException e) {
        if (e instanceof WebApplicationExceptionWrapper) {
            return ((WebApplicationExceptionWrapper<?>) e).unwrap();
        }
        return e;
    }

    /**
     * Sanitizes the response by creating a new response with only the status code, allowed methods, entity and the
     * media type. All other information is removed.
     *
     * @param response the response to sanitize.
     *
     * @return the new response
     */
    static Response sanitize(final Response response) {
        MediaType mediaType = response.getMediaType();
        if (mediaType == null) {
            // Use a default media type if not set
            mediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
        return Response.status(response.getStatus())
                .allow(response.getAllowedMethods())
                .type(mediaType)
                .build();
    }

    /**
     * Returns the original, unwrapped, exception.
     *
     * @return the original exception
     */
    T unwrap();
}
