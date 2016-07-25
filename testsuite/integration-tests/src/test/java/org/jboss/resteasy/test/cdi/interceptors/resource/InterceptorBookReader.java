package org.jboss.resteasy.test.cdi.interceptors.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.cdi.util.Constants;

import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Consumes(Constants.MEDIA_TYPE_TEST_XML)
@Interceptors({InterceptorOne.class})
@InterceptorClassBinding
public class InterceptorBookReader implements MessageBodyReader<InterceptorBook> {

    private static Logger logger = Logger.getLogger(InterceptorBookReader.class);

    private static MessageBodyReader<InterceptorBook> delegate;


    static {
        logger.info("In InterceptorBookReader static {}");
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        delegate = factory.getMessageBodyReader(InterceptorBook.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE);
        logger.info("In InterceptorBookReader static {}");
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        logger.info("entering InterceptorBookReader.isReadable()");
        boolean b = InterceptorBook.class.equals(type);
        logger.info("leaving InterceptorBookReader.isReadable()");
        return b;
    }

    @Interceptors({InterceptorTwo.class})
    @InterceptorMethodBinding
    public InterceptorBook readFrom(Class<InterceptorBook> type, Type genericType,
                                    Annotation[] annotations, MediaType mediaType,
                                    MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        logger.info("entering InterceptorBookReader.readFrom()");
        InterceptorBook book = InterceptorBook.class.cast(delegate.readFrom(InterceptorBook.class, genericType, annotations, mediaType, httpHeaders, entityStream));
        logger.info("InterceptorBookReader.readFrom() read " + book);
        logger.info("leaving InterceptorBookReader.readFrom()");
        return book;
    }
}

