package org.jboss.resteasy.client.jaxrs.internal;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.invoke.ConstantBootstraps;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.ext.ReaderInterceptor;

import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ProvidersContextRetainer;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.interception.jaxrs.AbstractReaderInterceptorContext;
import org.jboss.resteasy.core.interception.jaxrs.ClientReaderInterceptorContext;
import org.jboss.resteasy.plugins.providers.sse.EventInput;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.specimpl.AbstractBuiltResponse;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HeaderValueProcessor;
import org.jboss.resteasy.spi.MarshalledEntity;
import org.jboss.resteasy.spi.util.Types;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.InputStreamToByteArray;
import org.jboss.resteasy.util.ReadFromStream;
import org.reactivestreams.Publisher;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class ClientResponse extends BuiltResponse {
    private static final VarHandle INPUT_STREAM_HANDLER = ConstantBootstraps.fieldVarHandle(MethodHandles.lookup(), "is",
            VarHandle.class, AbstractBuiltResponse.class, InputStream.class);
    // One thing to note, I don't cache header objects because I was too lazy to proxy the headers multivalued map
    protected Map<String, Object> properties;
    protected ClientConfiguration configuration;
    protected RESTEasyTracingLogger tracingLogger;

    @Deprecated
    protected ClientResponse(final ClientConfiguration configuration) {
        setClientConfiguration(configuration);
        tracingLogger = RESTEasyTracingLogger.empty();
    }

    protected ClientResponse(final ClientConfiguration configuration, final RESTEasyTracingLogger tracingLogger) {
        setClientConfiguration(configuration);
        this.tracingLogger = tracingLogger;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setHeaders(MultivaluedMap<String, String> headers) {
        this.metadata = new Headers<Object>();
        this.metadata.putAll((Map) headers);
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setClientConfiguration(ClientConfiguration configuration) {
        this.configuration = configuration;
        this.processor = configuration;
    }

    @Override
    public synchronized Object getEntity() {
        abortIfClosed();
        Object entity = super.getEntity();
        if (entity != null) {
            return checkEntityReadAsInputStreamFullyConsumed(entity);
        }
        return checkEntityReadAsInputStreamFullyConsumed(getEntityStream());
    }

    //Check if the entity was previously fully consumed
    private <T> T checkEntityReadAsInputStreamFullyConsumed(T entity) {
        if (bufferedEntity == null && entity instanceof InputStream && streamFullyRead) {
            throw new IllegalStateException();
        }
        return entity;
    }

    @Override
    public Class<?> getEntityClass() {
        Class<?> classs = super.getEntityClass();
        if (classs != null) {
            return classs;
        }
        Object entity = null;
        try {
            entity = getEntity();
        } catch (Exception e) {
        }
        return entity != null ? entity.getClass() : null;
    }

    @Override
    public boolean hasEntity() {
        abortIfClosed();
        return getInputStream() != null && (entity != null || getMediaType() != null);
    }

    /**
     * In case of an InputStream or Reader and a invocation that returns no Response object, we need to make
     * sure the GC does not close the returned InputStream or Reader
     */
    public void noReleaseConnection() {

        isClosed = true;
    }

    @Override
    public void close() {
        if (isClosed())
            return;
        try {
            isClosed = true;
            releaseConnection();
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }

    @Override
    protected HeaderValueProcessor getHeaderValueProcessor() {
        return configuration;
    }

    protected InputStream getEntityStream() {
        if (bufferedEntity != null)
            return new ByteArrayInputStream(bufferedEntity);

        if (isClosed())
            throw new ProcessingException(Messages.MESSAGES.streamIsClosed());
        InputStream is = getInputStream();
        return is != null ? new AbstractBuiltResponse.InputStreamWrapper<ClientResponse>(is, this) : null;
    }

    @Override
    protected void setInputStream(final InputStream is) {
        InputStream old = this.is;
        safeClose(old);
        while (!INPUT_STREAM_HANDLER.compareAndSet(this, old, is)) {
            old = this.is;
            safeClose(old);
        }
    }

    // this is synchronized in conjunction with finalize to protect against premature finalize called by the GC
    @Override
    protected synchronized <T> Object readFrom(Class<T> type, Type genericType,
            MediaType media, Annotation[] annotations) {
        Type useGeneric = genericType == null ? type : genericType;
        Class<?> useType = type;
        media = media == null ? MediaType.WILDCARD_TYPE : media;
        annotations = annotations == null ? this.annotations : annotations;
        boolean isMarshalledEntity = false;
        if (type.equals(MarshalledEntity.class)) {
            isMarshalledEntity = true;
            ParameterizedType param = (ParameterizedType) useGeneric;
            useGeneric = param.getActualTypeArguments()[0];
            useType = Types.getRawType(useGeneric);
        }

        Providers current = ResteasyContext.getContextData(Providers.class);
        ResteasyContext.pushContext(Providers.class, configuration);
        Object obj = null;
        try {
            InputStream is = getEntityStream();
            if (is == null) {
                throw new IllegalStateException(Messages.MESSAGES.inputStreamWasEmpty());
            }
            if (isMarshalledEntity) {
                is = new InputStreamToByteArray(is);

            }

            ReaderInterceptor[] readerInterceptors = configuration.getReaderInterceptors(null, null);

            final Object finalObj;

            final long timestamp = tracingLogger.timestamp("RI_SUMMARY");
            AbstractReaderInterceptorContext context = new ClientReaderInterceptorContext(readerInterceptors,
                    configuration.getProviderFactory(), useType,
                    useGeneric, annotations, media, getStringHeaders(), is, properties, tracingLogger);
            try {
                finalObj = context.proceed();
                obj = finalObj;
            } finally {
                tracingLogger.logDuration("RI_SUMMARY", timestamp, context.getProcessedInterceptorCount());
            }

            if (isMarshalledEntity) {
                InputStreamToByteArray isba = (InputStreamToByteArray) is;
                final byte[] bytes = isba.toByteArray();
                return new MarshalledEntity<Object>() {
                    @Override
                    public byte[] getMarshalledBytes() {
                        return bytes;
                    }

                    @Override
                    public Object getEntity() {
                        return finalObj;
                    }
                };
            } else {
                return finalObj;
            }

        } catch (ProcessingException pe) {
            throw pe;
        } catch (Exception ex) {
            throw new ProcessingException(ex);
        } finally {
            if (!Publisher.class.isAssignableFrom(type) && !EventInput.class.isAssignableFrom(type)) {
                ResteasyContext.popContextData(Providers.class);
                if (current != null)
                    ResteasyContext.pushContext(Providers.class, current);
                if (obj instanceof ProvidersContextRetainer)
                    ((ProvidersContextRetainer) obj).setProviders(configuration);
            }
        }
    }

    @Override
    public boolean bufferEntity() {
        abortIfClosed();
        if (bufferedEntity != null)
            return true;
        if (streamRead)
            return false;
        if (metadata.getFirst(HttpHeaderNames.CONTENT_TYPE) == null)
            return false;
        InputStream is = getInputStream();
        if (is == null)
            return false;
        try {
            bufferedEntity = ReadFromStream.readFromStream(1024, is);
        } catch (IOException e) {
            throw new ProcessingException(e);
        } finally {
            try {
                releaseConnection();
            } catch (IOException e) {
                throw new ProcessingException(e);
            }
        }
        return true;
    }

    @Override
    public void abortIfClosed() {
        if (bufferedEntity == null)
            super.abortIfClosed();
    }

    protected static void safeClose(final Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (IOException e) {
            LogMessages.LOGGER.debugf(e, "Failed to close %s", closeable);
        }
    }

}
