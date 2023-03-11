package org.jboss.resteasy.test.core.basic.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

@Provider
@Produces("text/plain")
public class ProviderInjectionSimpleMessageBodyWriter implements MessageBodyWriter<String> {

    // just in case there was a pool of instances - we want to test all of them
    private static Set<ProviderInjectionSimpleMessageBodyWriter> instances = new HashSet<ProviderInjectionSimpleMessageBodyWriter>();
    @Context
    private Providers fieldProviders = null;
    private Providers constructorProviders = null;

    public ProviderInjectionSimpleMessageBodyWriter(@Context final Providers providers) {
        constructorProviders = providers;
        instances.add(this);
    }

    public static Set<ProviderInjectionSimpleMessageBodyWriter> getInstances() {
        return instances;
    }

    public long getSize(String t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return 3;
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    public void writeTo(String t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        Writer writer = new OutputStreamWriter(entityStream);
        writer.write("bar");
        writer.flush();
    }

    public Providers getFieldProviders() {
        return fieldProviders;
    }

    public Providers getConstructorProviders() {
        return constructorProviders;
    }
}
