package org.jboss.resteasy.plugins.providers.jsonb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import jakarta.annotation.Priority;
import jakarta.json.bind.Jsonb;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.providers.jsonb.i18n.LogMessages;
import org.jboss.resteasy.plugins.providers.jsonb.i18n.Messages;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.util.DelegatingOutputStream;

/**
 * Created by rsearls on 6/26/17.
 */
@Provider
@Produces({ "application/json", "application/*+json", "text/json" })
@Consumes({ "application/json", "application/*+json", "text/json" })
@Priority(Priorities.USER - 100)
public class JsonBindingProvider extends AbstractJsonBindingProvider
        implements MessageBodyReader<Object>, AsyncMessageBodyWriter<Object> {

    private final boolean disabled;

    public JsonBindingProvider() {
        super();
        ResteasyConfiguration context = ResteasyContext.getContextData(ResteasyConfiguration.class);
        if (context == null) {
            disabled = Boolean.getBoolean(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB);
        } else {
            disabled = (Boolean.parseBoolean(context.getParameter(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB))
                    || Boolean.parseBoolean(context.getParameter("resteasy.jsonp.enable")));
        }
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        if (disabled) {
            return false;
        }
        return isSupportedMediaType(mediaType);
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws java.io.IOException, jakarta.ws.rs.WebApplicationException {
        final EmptyCheckInputStream is = new EmptyCheckInputStream(entityStream);

        try (Jsonb jsonb = getJsonb(type)) {
            return jsonb.fromJson(is, genericType);
            // If null is returned, considered to be empty stream
        } catch (Throwable e) {
            if (is.isEmpty()) {
                return null;
            }
            // detail text provided in logger message
            throw new ProcessingException(Messages.MESSAGES.jsonBDeserializationError(e.toString()), e);
        }
    }

    private static class EmptyCheckInputStream extends InputStream {
        private final InputStream delegate;
        boolean read = false;
        boolean empty = false;

        EmptyCheckInputStream(final InputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public int read() throws IOException {
            final int i = delegate.read();
            afterRead(i);
            return i;
        }

        @Override
        public int read(final byte[] b) throws IOException {
            final int i = delegate.read();
            afterRead(i);
            return i;
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            final int i = delegate.read(b, off, len);
            afterRead(i);
            return i;
        }

        @Override
        public byte[] readAllBytes() throws IOException {
            return delegate.readAllBytes();
        }

        @Override
        public byte[] readNBytes(final int len) throws IOException {
            return delegate.readNBytes(len);
        }

        @Override
        public int readNBytes(final byte[] b, final int off, final int len) throws IOException {
            return delegate.readNBytes(b, off, len);
        }

        @Override
        public long skip(final long n) throws IOException {
            return delegate.skip(n);
        }

        @Override
        public int available() throws IOException {
            return delegate.available();
        }

        @Override
        public void close() {
            // Do not close the stream as per the Jakarta REST specification, currently 3.1, the stream being used
            // must not be closed in a MessageBodyReader.
        }

        @Override
        public void mark(final int readlimit) {
            delegate.mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            delegate.reset();
        }

        @Override
        public boolean markSupported() {
            return delegate.markSupported();
        }

        @Override
        public long transferTo(final OutputStream out) throws IOException {
            return delegate.transferTo(out);
        }

        private synchronized void afterRead(final int n) {
            if (!read && n <= 0) {
                empty = true;
            }
            read = true;
        }

        public boolean isEmpty() {
            return empty;
        }
    };

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        if (disabled) {
            return false;
        }
        return isSupportedMediaType(mediaType);
    }

    @Override
    public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType) {
        return -1L;
    }

    @Override
    public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream)
            throws java.io.IOException, jakarta.ws.rs.WebApplicationException {
        try (Jsonb jsonb = getJsonb(type)) {
            entityStream = new DelegatingOutputStream(entityStream) {
                @Override
                public void flush() throws IOException {
                    // don't flush as this is a performance hit on Undertow.
                    // and causes chunked encoding to happen.
                }
            };
            entityStream.write(jsonb.toJson(t).getBytes(getCharset(mediaType)));
            entityStream.flush();
        } catch (Throwable e) {
            throw new ProcessingException(Messages.MESSAGES.jsonBSerializationError(e.toString()), e);
        }
    }

    @Override
    public CompletionStage<Void> asyncWriteTo(Object t, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, AsyncOutputStream entityStream) {
        Jsonb jsonb = getJsonb(type);
        try {
            return entityStream.asyncWrite(jsonb.toJson(t).getBytes(getCharset(mediaType)))
                    .whenComplete((unused, throwable) -> {
                        try {
                            jsonb.close();
                        } catch (Exception e) {
                            LogMessages.LOGGER.debug("Failed to close the JSONB context.", e);
                        }
                    });
        } catch (Throwable e) {
            CompletableFuture<Void> ret = new CompletableFuture<>();
            ret.completeExceptionally(new ProcessingException(Messages.MESSAGES.jsonBSerializationError(e.toString()), e));
            try {
                jsonb.close();
            } catch (Exception ex) {
                LogMessages.LOGGER.debug("Failed to close the JSONB context.", ex);
            }
            return ret;
        }
    }
}
