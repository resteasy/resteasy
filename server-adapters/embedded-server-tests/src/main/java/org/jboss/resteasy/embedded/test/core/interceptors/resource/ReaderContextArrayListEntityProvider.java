/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.embedded.test.core.interceptors.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ReaderContextArrayListEntityProvider implements
        MessageBodyReader<ArrayList<String>>,
        MessageBodyWriter<ArrayList<String>> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return type == ArrayList.class;
    }

    @Override
    public long getSize(ArrayList<String> t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        int annlen = annotations.length > 0 ? annotations[0].annotationType()
                .getName().length() : 0;
        return t.iterator().next().length() + annlen
                + mediaType.toString().length();
    }

    @Override
    public void writeTo(ArrayList<String> t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {
        String ann = "";
        if (annotations.length > 0) {
            ann = annotations[0].annotationType().getName();
        }
        entityStream.write((t.iterator().next() + ann + mediaType.toString())
                .getBytes());
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return type == ArrayList.class;
    }

    @Override
    public ArrayList<String> readFrom(Class<ArrayList<String>> type,
            Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        String text = readString(entityStream);
        entityStream.close();
        String ann = "";
        if (annotations.length > 0) {
            ann = annotations[0].annotationType().getName();
        }
        ArrayList<String> list = new ArrayList<String>();
        list.add(text + ann + mediaType.toString());
        return list;
    }

    private static String readString(final InputStream in) throws IOException {
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            final StringBuilder sb = new StringBuilder();
            final char[] buffer = new char[256];
            int len;
            while ((len = reader.read(buffer)) > 0) {
                sb.append(buffer, 0, len);
            }
            return sb.toString();
        }
    }
}
