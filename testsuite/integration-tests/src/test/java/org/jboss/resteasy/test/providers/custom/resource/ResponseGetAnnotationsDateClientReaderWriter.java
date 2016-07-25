package org.jboss.resteasy.test.providers.custom.resource;

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
public class ResponseGetAnnotationsDateClientReaderWriter implements MessageBodyReader<Date>,
        MessageBodyWriter<Date> {
    private StringBuilder atom;

    public ResponseGetAnnotationsDateClientReaderWriter(final StringBuilder atom) {
        super();
        this.atom = atom;
    }

    @Override
    public long getSize(Date arg0, Class<?> arg1, Type arg2, Annotation[] arg3,
                        MediaType arg4) {
        return String.valueOf(Long.MAX_VALUE).length()
                + ResponseGetAnnotationsDateContainerReaderWriter.SPLITTER.length();
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
        byte[] bytes = dateToString(date).getBytes();
        stream.write(bytes);
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
        String data = br.readLine();
        String[] split = data == null ? new String[]{"0"} : data
                .split(ResponseGetAnnotationsDateContainerReaderWriter.SPLITTER);
        long date = Long.parseLong(split[0]);
        atom.append(split[1]);
        return new Date(date);
    }

    public static final String dateToString(Date date) {
        return String.valueOf(date.getTime());
    }
}
