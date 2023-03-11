package org.jboss.resteasy.test.providers.jsonb.basic.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.util.NoContent;
import org.jboss.resteasy.util.TypeConverter;

/**
 * This test provider is based on DefaultTextPlain provider
 */
@SuppressWarnings("unchecked")
@Provider
@Produces({ "application/json", "application/*+json", "text/json", "*/*" })
@Consumes({ "application/json", "application/*+json", "text/json", "*/*" })
@Priority(Priorities.USER)
public class JsonBindingCustomRepeaterProvider implements MessageBodyReader, MessageBodyWriter {
    public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // StringTextStar should pick up strings
        return !String.class.equals(type) && TypeConverter.isConvertable(type);
    }

    public Object readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        InputStream delegate = NoContent.noContentCheck(httpHeaders, entityStream);
        String value = ProviderHelper.readString(delegate, mediaType);
        return TypeConverter.getType(type, value);
    }

    public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // StringTextStar should pick up strings
        return !String.class.equals(type) && !type.isArray();
    }

    public long getSize(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        String charset = mediaType.getParameters().get("charset");
        if (charset != null)
            try {
                return o.toString().getBytes(charset).length;
            } catch (UnsupportedEncodingException e) {
                // Use default encoding.
            }
        return o.toString().getBytes(StandardCharsets.UTF_8).length;
    }

    public void writeTo(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        String charset = mediaType.getParameters().get("charset");
        if (charset == null)
            entityStream.write(o.toString().getBytes(StandardCharsets.UTF_8));
        else
            entityStream.write(o.toString().getBytes(charset));
    }
}
