package org.jboss.resteasy.plugins.providers;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.util.FindAnnotation;
import org.jboss.resteasy.util.Encode;
import org.jboss.resteasy.util.NoContent;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("rawtypes")
@Provider
@Produces("application/x-www-form-urlencoded")
@Consumes("application/x-www-form-urlencoded")
@ConstrainedTo(RuntimeType.CLIENT)
public class FormUrlEncodedProvider implements MessageBodyReader<MultivaluedMap>, AsyncMessageBodyWriter<MultivaluedMap> {
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return MultivaluedMap.class.equals(type);
        /*
         * if (!MultivaluedMap.class.isAssignableFrom(type)) return false;
         * if (genericType == null) return true;
         *
         * if (!(genericType instanceof ParameterizedType)) return false;
         * ParameterizedType params = (ParameterizedType) genericType;
         * if (params.getActualTypeArguments().length != 2) return false;
         * return params.getActualTypeArguments()[0].equals(String.class) &&
         * params.getActualTypeArguments()[1].equals(String.class);
         */
    }

    public MultivaluedMap readFrom(Class<MultivaluedMap> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
        if (NoContent.isContentLengthZero(httpHeaders))
            return new MultivaluedMapImpl<String, String>();
        boolean encoded = FindAnnotation.findAnnotation(annotations, Encoded.class) != null;
        String charset = mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);
        if (charset == null) {
            charset = StandardCharsets.UTF_8.name();
        }
        if (encoded)
            return parseForm(entityStream, charset);
        else
            return Encode.decode(parseForm(entityStream, charset), charset);
    }

    public static MultivaluedMap<String, String> parseForm(InputStream entityStream, String charset)
            throws IOException {
        char[] buffer = new char[100];
        StringBuffer buf = new StringBuffer();
        if (charset == null) {
            charset = StandardCharsets.UTF_8.name();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(entityStream, charset));

        int wasRead = 0;
        do {
            wasRead = reader.read(buffer, 0, 100);
            if (wasRead > 0)
                buf.append(buffer, 0, wasRead);
        } while (wasRead > -1);

        String form = buf.toString();

        MultivaluedMap<String, String> formData = new MultivaluedMapImpl<String, String>();
        if ("".equals(form))
            return formData;

        String[] params = form.split("&");

        for (String param : params) {
            if (param.indexOf('=') >= 0) {
                String[] nv = param.split("=", 2);
                String val = nv.length > 1 ? nv[1] : "";
                formData.add(nv[0], val);
            } else {
                formData.add(param, "");
            }
        }
        return formData;
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return MultivaluedMap.class.isAssignableFrom(type);
        /*
         * if (!MultivaluedMap.class.isAssignableFrom(type)) return false;
         * if (genericType == null) return true;
         *
         * if (!(genericType instanceof ParameterizedType)) return false;
         * ParameterizedType params = (ParameterizedType) genericType;
         * if (params.getActualTypeArguments().length != 2) return false;
         * return params.getActualTypeArguments()[0].equals(String.class) &&
         * params.getActualTypeArguments()[1].equals(String.class);
         */
    }

    public long getSize(MultivaluedMap stringStringMultivaluedMap, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType) {
        return -1;
    }

    public void writeTo(MultivaluedMap data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        byte[] bytes = getBytes(data, mediaType, annotations);
        //logger.info("*** FORM PROVIDER WRITING: " + new String(bytes));

        //      httpHeaders.putSingle(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(bytes.length));
        entityStream.write(bytes);

    }

    private byte[] getBytes(MultivaluedMap data, MediaType mediaType, Annotation[] annotations) throws IOException {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
        @SuppressWarnings(value = "unchecked")
        MultivaluedMap<String, String> formData = (MultivaluedMap<String, String>) data;
        String charset = mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);
        if (charset == null) {
            charset = StandardCharsets.UTF_8.name();
        }
        boolean encoded = FindAnnotation.findAnnotation(annotations, Encoded.class) != null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos, charset);

        boolean first = true;
        for (Map.Entry<String, List<String>> entry : formData.entrySet()) {
            String encodedName = entry.getKey();
            if (!encoded)
                encodedName = URLEncoder.encode(entry.getKey(), charset);

            for (String value : entry.getValue()) {
                if (first)
                    first = false;
                else
                    writer.write("&");
                if (!encoded) {
                    value = URLEncoder.encode(value, charset);
                }
                writer.write(encodedName);
                writer.write("=");
                writer.write(value);
            }
            writer.flush();
        }

        return baos.toByteArray();
    }

    @Override
    public CompletionStage<Void> asyncWriteTo(MultivaluedMap data, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, AsyncOutputStream entityStream) {
        try {
            byte[] bytes = getBytes(data, mediaType, annotations);
            //logger.info("*** FORM PROVIDER WRITING: " + new String(bytes));

            //      httpHeaders.putSingle(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(bytes.length));
            return entityStream.asyncWrite(bytes);
        } catch (IOException e) {
            return ProviderHelper.completedException(e);
        }
    }
}
