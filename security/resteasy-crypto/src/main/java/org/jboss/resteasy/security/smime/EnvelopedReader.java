package org.jboss.resteasy.security.smime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.security.BouncyIntegration;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.util.Types;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Consumes("*/*")
public class EnvelopedReader implements MessageBodyReader<EnvelopedInput> {
    static {
        BouncyIntegration.init();
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return EnvelopedInput.class.isAssignableFrom(type);
    }

    public EnvelopedInput readFrom(Class<EnvelopedInput> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> headers, InputStream entityStream) throws IOException, WebApplicationException {
        Class<?> baseType = null;
        Type baseGenericType = null;

        if (genericType != null && genericType instanceof ParameterizedType) {
            ParameterizedType param = (ParameterizedType) genericType;
            baseGenericType = param.getActualTypeArguments()[0];
            baseType = Types.getRawType(baseGenericType);
        }
        EnvelopedInputImpl input = new EnvelopedInputImpl();
        input.setType(baseType);
        input.setGenericType(baseGenericType);

        StringBuilder headerString = new StringBuilder();
        if (headers.containsKey("Content-Disposition")) {
            headerString.append("Content-Disposition: ").append(headers.getFirst("Content-Disposition")).append("\r\n");
        }
        if (headers.containsKey("Content-Type")) {
            headerString.append("Content-Type: ").append(headers.getFirst("Content-Type")).append("\r\n");
        }
        if (headers.containsKey("Content-Transfer-Encoding")) {
            headerString.append("Content-Transfer-Encoding: ").append(headers.getFirst("Content-Transfer-Encoding"))
                    .append("\r\n");
        }
        headerString.append("\r\n");
        ByteArrayInputStream is = new ByteArrayInputStream(headerString.toString().getBytes(StandardCharsets.UTF_8));
        MimeBodyPart body = null;
        try {
            body = new MimeBodyPart(new SequenceInputStream(is, entityStream));
        } catch (MessagingException e) {
            throw new ReaderException(e);
        }
        Providers providers = ResteasyContext.getContextData(Providers.class);
        input.setProviders(providers);
        input.setAnnotations(annotations);
        input.setBody(body);
        return input;
    }
}
