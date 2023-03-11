package org.jboss.resteasy.client.jaxrs.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.MessageBodyWriter;

import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AbortedResponse extends FinalizedClientResponse {
    @SuppressWarnings("unchecked")
    public AbortedResponse(final ClientConfiguration configuration, final Response response) {
        super(configuration, RESTEasyTracingLogger.empty());

        for (Map.Entry<String, List<Object>> entry : response.getMetadata().entrySet()) {
            for (Object obj : entry.getValue()) {
                getMetadata().add(entry.getKey(), configuration.toHeaderString(obj));
            }
        }
        setStatus(response.getStatus());
        setEntity(response.getEntity());
        if (response instanceof BuiltResponse) {
            BuiltResponse built = (BuiltResponse) response;
            setEntityClass(built.getEntityClass());
            setGenericType(built.getGenericType());
            setAnnotations(built.getAnnotations());
        }

        // spec requires that aborting a request still acts like it went over the wire
        // so we must marshall the entity and buffer it.
        if (response.getEntity() != null) {
            MediaType mediaType = getMediaType();
            if (mediaType == null) {
                mediaType = MediaType.WILDCARD_TYPE;
                getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, MediaType.WILDCARD);
            }

            if (!(response.getEntity() instanceof InputStream)) {

                @SuppressWarnings("rawtypes")
                MessageBodyWriter writer = configuration
                        .getMessageBodyWriter(getEntityClass(), getGenericType(),
                                null, mediaType);
                if (writer == null) {
                    throw new ProcessingException(
                            Messages.MESSAGES.failedToBufferAbortedResponseNoWriter(mediaType, entityClass.getName()));
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    writer.writeTo(getEntity(), getEntityClass(), getGenericType(), getAnnotations(), mediaType, getHeaders(),
                            baos);
                } catch (IOException e) {
                    throw new ProcessingException(Messages.MESSAGES.failedToBufferAbortedResponse(), e);
                }
                bufferedEntity = baos.toByteArray();
                setInputStream(new ByteArrayInputStream(bufferedEntity));
            } else {
                InputStream is = (InputStream) response.getEntity();
                setInputStream(is);
            }
            //  The following lines commented for RESTEASY-1540
            //         setEntity(null); // clear all entity information
            //         setAnnotations(null);
        }

    }

    @Override
    protected void setInputStream(InputStream is) {
        this.is = is;
    }

    /**
     * Added for RESTEASY-1540.
     */
    @Override
    public synchronized <T> T readEntity(Class<T> type, Type genericType, Annotation[] anns) {
        setEntity(null); // clear all entity information
        setAnnotations(null);
        return super.readEntity(type, genericType, anns);
    }
}
