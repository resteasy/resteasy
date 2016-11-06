package org.jboss.resteasy.test.cdi.basic.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.util.ReadFromStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Tests to make sure that a CDI bean was injected and that this provider overrides the default XML provider
 */
@Provider
@Produces("application/xml")
@Consumes("application/xml")
public class BeanReaderWriterXFormatProvider implements MessageBodyReader<BeanReaderWriterXFormat>, MessageBodyWriter<BeanReaderWriterXFormat> {
    private static Logger logger = Logger.getLogger(BeanReaderWriterXFormatProvider.class);
    @Inject
    BeanReaderWriterConfigBean bean;

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return BeanReaderWriterXFormat.class.isAssignableFrom(type);
    }

    @Override
    public BeanReaderWriterXFormat readFrom(Class<BeanReaderWriterXFormat> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        logger.info("********** readFrom ********");
        if (bean != null) {
            logger.info("BeanReaderWriterXFormatProvider: BeanReaderWriterConfigBean version: " + bean.version());
        }
        byte[] bytes = ReadFromStream.readFromStream(1024, entityStream);
        String val = new String(bytes);
        String[] split = val.split(" ");
        return new BeanReaderWriterXFormat(split[0], split[1]);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return BeanReaderWriterXFormat.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(BeanReaderWriterXFormat xFormat, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(BeanReaderWriterXFormat xFormat, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        logger.info("********** writeTo ********");
        String message = "";
        if (bean != null) {
            logger.info("BeanReaderWriterXFormatProvider: BeanReaderWriterConfigBean version: " + bean.version());
            message += xFormat.getId() + " " + bean.version();
        } else {
            message += xFormat.getId() + " 0";
        }
        entityStream.write(message.getBytes());
    }
}
