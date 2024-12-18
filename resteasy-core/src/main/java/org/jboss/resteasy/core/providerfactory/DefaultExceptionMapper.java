/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.core.providerfactory;

import java.lang.invoke.MethodHandles;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.jboss.logging.Logger;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * The default {@link ExceptionMapper} for RESTEasy.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class DefaultExceptionMapper implements ExceptionMapper<Throwable> {
    private static final LogMessages LOGGER = Logger.getMessageLogger(MethodHandles.lookup(), LogMessages.class,
            DefaultExceptionMapper.class.getName());
    static final DefaultExceptionMapper INSTANCE = new DefaultExceptionMapper();

    @Override
    public Response toResponse(final Throwable exception) {
        // If this is an ApplicationException we want to unwrap it for the real cause.
        if (exception instanceof ApplicationException) {
            final Throwable cause = exception.getCause();
            if (cause != null) {
                return process(cause);
            }
        }
        return process(exception);
    }

    private Response process(final Throwable exception) {
        // Check the level to ignore logging if turned off.
        if (LOGGER.isEnabled(Logger.Level.ERROR)) {
            // Get the current context
            final ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();
            final Request request = providerFactory.getContextData(Request.class);
            final ResourceInfo resourceInfo = providerFactory.getContextData(ResourceInfo.class);
            final UriInfo uriInfo = providerFactory.getContextData(UriInfo.class);

            final StringBuilder info = new StringBuilder();

            // Add the method if available
            if (request != null) {
                info.append(request.getMethod());
                if (uriInfo != null || resourceInfo != null) {
                    info.append(' ');
                }
            }
            // Add the request URI if available
            if (uriInfo != null) {
                info.append(uriInfo.getRequestUri().getPath());
                if (resourceInfo != null) {
                    info.append(" - ");
                }
            }
            // Add the resource class and method
            if (resourceInfo != null) {
                info.append(resourceInfo.getResourceClass().getName())
                        .append('.')
                        .append(resourceInfo.getResourceMethod().getName());
            }
            if (info.length() == 0) {
                LOGGER.defaultExceptionMapper(exception);
            } else {
                LOGGER.defaultExceptionMapper(exception, info);
            }
        }
        // We must return the response from a WebApplicationException.
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }
        return Response.serverError()
                .entity(exception.getLocalizedMessage())
                .build();
    }
}
