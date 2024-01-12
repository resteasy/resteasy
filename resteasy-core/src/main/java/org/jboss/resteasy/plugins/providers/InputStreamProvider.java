package org.jboss.resteasy.plugins.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.MediaTypeHelper;

/**
 * @author <a href="mailto:bill@burkecentral.com">BillBurke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("*/*")
@Consumes("*/*")
public class InputStreamProvider implements MessageBodyReader<InputStream>, AsyncMessageBodyWriter<InputStream> {
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(InputStream.class);
    }

    public InputStream readFrom(Class<InputStream> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
        return entityStream;
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return InputStream.class.isAssignableFrom(type) && !MediaTypeHelper.isBlacklisted(mediaType);
    }

    public long getSize(InputStream inputStream, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType) {
        return -1;
    }

    public void writeTo(InputStream inputStream, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
        try {
            ProviderHelper.writeTo(inputStream, entityStream);
        } finally {
            inputStream.close();
        }
    }

    @Override
    public CompletionStage<Void> asyncWriteTo(InputStream inputStream, Class<?> type, Type genericType,
            Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            AsyncOutputStream entityStream) {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
        try {
            int c = inputStream.read();
            if (c == -1) {
                httpHeaders.putSingle(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(0));
                return entityStream.asyncWrite(new byte[0]); // fix RESTEASY-204
            }
            return entityStream.asyncWrite(new byte[] { (byte) c })
                    .thenCompose(v -> ProviderHelper.writeToAndCloseInput(inputStream, entityStream));
        } catch (IOException e) {
            return ProviderHelper.completedException(e);
        }
    }
}
