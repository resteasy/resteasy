/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.plugins.providers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.util.MediaTypeHelper;

/**
 * @author <a href="mailto:mlittle@redhat.com">Mark Little</a>
 * @version $Revision: 1 $
 */

@Provider
@Produces("*/*")
@Consumes("*/*")
public class FileProvider extends AbstractDiskIoProvider<File> {
    public boolean isReadable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return File.class == type;
    }

    public File readFrom(Class<File> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());

        return readFromStream(httpHeaders, entityStream).toFile();
    }

    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return File.class.isAssignableFrom(type) && !MediaTypeHelper.isBlacklisted(mediaType); // catch subtypes
    }

    public long getSize(File o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return o.length();
    }

    public void writeTo(File uploadFile, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
        writeToStream(uploadFile.toPath(), mediaType, httpHeaders, entityStream);
    }

    public CompletionStage<Void> asyncWriteTo(File uploadFile, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            AsyncOutputStream entityStream) {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
        return asyncWriteToStream(uploadFile.toPath(), mediaType, httpHeaders, entityStream);
    }
}
