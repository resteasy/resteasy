package org.jboss.resteasy.test.injection.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Consumes("not/real")
public class ProviderInjectionProviderReader implements MessageBodyReader {
    @Context
    public HttpHeaders headers;

    @Context
    public Providers workers;

    public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return false;
    }

    public Object readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        return null;
    }
}
