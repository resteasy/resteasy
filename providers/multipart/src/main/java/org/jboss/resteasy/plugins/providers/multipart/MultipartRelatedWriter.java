package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;

/**
 * The {@link MessageBodyWriter} implementation to serialize
 * {@link MultipartRelatedOutput} objects.
 *
 * @author Attila Kiraly
 * @version $Revision: 1 $
 */
@Provider
@Produces("multipart/related")
public class MultipartRelatedWriter extends AbstractMultipartRelatedWriter
        implements AsyncMessageBodyWriter<MultipartRelatedOutput> {

    public boolean isWriteable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return MultipartRelatedOutput.class.isAssignableFrom(type);
    }

    public long getSize(MultipartRelatedOutput multipartRelatedOutput,
            Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType) {
        return -1;
    }

    public void writeTo(MultipartRelatedOutput multipartRelatedOutput,
            Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {
        writeRelated(multipartRelatedOutput, mediaType, httpHeaders,
                entityStream, annotations);
    }

    @Override
    public CompletionStage<Void> asyncWriteTo(MultipartRelatedOutput multipartRelatedOutput, Class<?> type, Type genericType,
            Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            AsyncOutputStream entityStream) {
        return asyncWriteRelated(multipartRelatedOutput, mediaType, httpHeaders,
                entityStream, annotations);
    }
}
