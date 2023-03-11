package org.jboss.resteasy.plugins.providers.jsonp;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AbstractJsonpProvider {
    @Context
    jakarta.ws.rs.ext.Providers providers;

    private static final JsonReaderFactory readerFactory = Json.createReaderFactory(null);
    private static final JsonWriterFactory writerFactory = Json.createWriterFactory(null);

    public static Charset getCharset(final MediaType mediaType) {
        if (mediaType != null) {
            String charset = mediaType.getParameters().get("charset");
            if (charset != null)
                return Charset.forName(charset);
        }
        return null;
    }

    protected JsonReader findReader(MediaType mediaType, InputStream is) {
        ContextResolver<JsonReaderFactory> resolver = providers.getContextResolver(JsonReaderFactory.class, mediaType);
        JsonReaderFactory factory = null;
        if (resolver != null) {
            factory = resolver.getContext(JsonReaderFactory.class);
        }
        if (factory == null) {
            factory = readerFactory;
        }
        Charset charset = getCharset(mediaType);
        return charset == null ? factory.createReader(is) : factory.createReader(is, charset);
    }

    protected JsonWriter findWriter(MediaType mediaType, OutputStream os) {
        ContextResolver<JsonWriterFactory> resolver = providers.getContextResolver(JsonWriterFactory.class, mediaType);
        JsonWriterFactory factory = null;
        if (resolver != null) {
            factory = resolver.getContext(JsonWriterFactory.class);
        }
        if (factory == null) {
            factory = writerFactory;
        }
        Charset charset = getCharset(mediaType);
        return charset == null ? factory.createWriter(os) : factory.createWriter(os, charset);
    }
}
