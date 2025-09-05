/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.embedded.test.providers.custom.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.util.TypeConverter;

@Provider
@Produces("*/*")
@Consumes("*/*")
public class WriterNotBuiltinTestWriter implements MessageBodyWriter, MessageBodyReader {
    private static Logger logger = Logger.getLogger(WriterNotBuiltinTestWriter.class);

    public static volatile boolean used;

    public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    public long getSize(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return o.toString().getBytes().length;
    }

    public void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        entityStream.write(o.toString().getBytes());
        logger.info("my writeTo");
        used = true;
    }

    public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @SuppressWarnings(value = "unchecked")
    public Object readFrom(Class type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        String value = ProviderHelper.readString(entityStream, mediaType);
        return TypeConverter.getType(type, value);
    }
}
