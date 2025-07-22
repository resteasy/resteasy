/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.plugins.providers;

import static org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages.LOGGER;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.util.MediaTypeHelper;

@Provider
@Produces("*/*")
@Consumes("*/*")
public class PathProvider extends AbstractDiskIoProvider<Path> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Path.class.isAssignableFrom(type);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Path.class.isAssignableFrom(type) && !MediaTypeHelper.isBlacklisted(mediaType); // catch subtypes
    }

    @Override
    public long getSize(Path path, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return getPathSize(path);
    }

    @Override
    public Path readFrom(Class<Path> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
        return readFromStream(httpHeaders, entityStream);
    }

    @Override
    public CompletionStage<Void> asyncWriteTo(Path path, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            AsyncOutputStream entityStream) {
        return asyncWriteToStream(path, mediaType, httpHeaders, entityStream);
    }

    @Override
    public void writeTo(Path path, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        writeToStream(path, mediaType, httpHeaders, entityStream);
    }
}
