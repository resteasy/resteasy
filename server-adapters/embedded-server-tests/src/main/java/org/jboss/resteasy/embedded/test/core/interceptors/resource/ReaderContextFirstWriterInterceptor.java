/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.embedded.test.core.interceptors.resource;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.CompletionStage;

import jakarta.annotation.Priority;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import org.jboss.resteasy.spi.AsyncWriterInterceptor;
import org.jboss.resteasy.spi.AsyncWriterInterceptorContext;

@Provider
@Priority(100)
public class ReaderContextFirstWriterInterceptor implements WriterInterceptor, AsyncWriterInterceptor {

    @Override
    public void aroundWriteTo(WriterInterceptorContext context)
            throws IOException, WebApplicationException {
        MultivaluedMap<String, Object> headers = context.getHeaders();
        String header = (String) headers.getFirst(ReaderContextResource.HEADERNAME);
        if (header != null && header.equals(getClass().getName())) {
            context.setAnnotations(ReaderContextResource.class.getAnnotations());
            context.setEntity(toList(getClass().getName()));
            context.setMediaType(MediaType.TEXT_HTML_TYPE);
            context.setType(LinkedList.class);
        }
        context.proceed();
    }

    private static <T> LinkedList<T> toList(T o) {
        LinkedList<T> list = new LinkedList<T>();
        list.add(o);
        return list;
    }

    @Override
    public CompletionStage<Void> asyncAroundWriteTo(AsyncWriterInterceptorContext context) {
        MultivaluedMap<String, Object> headers = context.getHeaders();
        String header = (String) headers.getFirst(ReaderContextResource.HEADERNAME);
        if (header != null && header.equals(getClass().getName())) {
            context.setAnnotations(ReaderContextResource.class.getAnnotations());
            context.setEntity(toList(getClass().getName()));
            context.setMediaType(MediaType.TEXT_HTML_TYPE);
            context.setType(LinkedList.class);
        }
        return context.asyncProceed();
    }
}
