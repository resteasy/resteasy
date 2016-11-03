package org.jboss.resteasy.test.providers.custom.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Date;

@Provider
@Produces("*/*")
public class ResponseGetAnnotationsDateContainerReaderWriter implements MessageBodyReader<Date>,
        MessageBodyWriter<Date> {

    private static Logger logger = Logger.getLogger(ResponseGetAnnotationsDateContainerReaderWriter.class);
    public static final String SPLITTER = " ANNOTATION_VALUE ";

    @Override
    public long getSize(Date arg0, Class<?> arg1, Type arg2, Annotation[] arg3,
                        MediaType arg4) {
        Annotation[] annotations = ResponseGetAnnotationsAnnotatedClass.class.getAnnotations();
        int size = String.valueOf(Long.MAX_VALUE).length() + SPLITTER.length()
                + annotations[0].annotationType().getName().length()
                + annotations[1].annotationType().getName().length();
        logger.info("getSize() " + size);
        return size;
    }

    @Override
    public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2,
                               MediaType arg3) {
        return arg0 == Date.class;
    }

    @Override
    public void writeTo(Date date, Class<?> arg1, Type arg2, Annotation[] arg3,
                        MediaType arg4, MultivaluedMap<String, Object> arg5,
                        OutputStream stream) throws IOException, WebApplicationException {
        String annotation = parseAnnotations(arg3);
        byte[] bytes = dateToString(date).getBytes();
        byte[] bytes1 = SPLITTER.getBytes();
        byte[] bytes2 = annotation.getBytes();

        logger.info("*** bytes to write " + (bytes.length + bytes1.length + bytes2.length));
        stream.write(bytes);
        stream.write(bytes1);
        stream.write(bytes2);
    }

    @Override
    public boolean isReadable(Class<?> arg0, Type arg1, Annotation[] arg2,
                              MediaType arg3) {
        return isWriteable(arg0, arg1, arg2, arg3);
    }

    @Override
    public Date readFrom(Class<Date> arg0, Type arg1, Annotation[] arg2,
                         MediaType arg3, MultivaluedMap<String, String> arg4,
                         InputStream arg5) throws IOException, WebApplicationException {
        InputStreamReader reader = new InputStreamReader(arg5);
        BufferedReader br = new BufferedReader(reader);
        long date = Long.parseLong(br.readLine());
        return new Date(date);
    }

    protected String parseAnnotations(Annotation[] annotations) {
        StringBuilder value = new StringBuilder();
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                value.append(annotation.annotationType().getName())
                        .append(", ");
            }
        }
        return value.toString();
    }

    public static final String dateToString(Date date) {
        return String.valueOf(date.getTime());
    }
}
