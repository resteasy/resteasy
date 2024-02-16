package org.jboss.resteasy.test.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.DynamicFeatureContextDelegate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/***
 *
 * @author Nicolas NESMON
 *
 */
public class DynamicFeatureContextDelegateTest {

    public static class CustomObject {

    }

    public static class CustomException extends Exception {

    }

    @Provider
    @Produces(MediaType.WILDCARD)
    public static class CustomObjectContextResolver implements ContextResolver<CustomObject> {

        @Override
        public CustomObject getContext(Class<?> type) {
            return new CustomObject();
        }

    }

    @Provider
    @Produces(MediaType.APPLICATION_JSON)
    public static class CustomObjectMessageBodyWriter implements MessageBodyWriter<CustomObject> {

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return true;
        }

        @Override
        public void writeTo(CustomObject t, Class<?> type, Type genericType, Annotation[] annotations,
                MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                throws IOException, WebApplicationException {
        }

        @Override
        public long getSize(CustomObject t, Class<?> type, Type genericType, Annotation[] annotations,
                MediaType mediaType) {
            return 0;
        }

    }

    @Provider
    @Consumes(MediaType.APPLICATION_JSON)
    public static class CustomObjectMessageBodyReader implements MessageBodyReader<CustomObject> {

        @Override
        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return true;
        }

        @Override
        public CustomObject readFrom(Class<CustomObject> type, Type genericType, Annotation[] annotations,
                MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                throws IOException, WebApplicationException {
            return null;
        }

    }

    @Provider
    public static class CustomExceptionMapper implements ExceptionMapper<CustomException> {

        @Override
        public Response toResponse(CustomException exception) {
            return null;
        }

    }

    @Provider
    public static class CustomDynamicFeature implements DynamicFeature {

        @Override
        public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        }

    }

    @Test
    public void testContextResolverRegistration() throws Exception {
        ResteasyProviderFactory resteasyProviderFactory = ResteasyProviderFactory.newInstance();
        DynamicFeatureContextDelegate featureContext = new DynamicFeatureContextDelegate(resteasyProviderFactory);
        featureContext.register(new CustomObjectContextResolver());
        Assertions.assertNull(resteasyProviderFactory.getContextResolver(CustomObject.class, MediaType.WILDCARD_TYPE));
        featureContext.register(CustomObjectContextResolver.class);
        Assertions.assertNull(resteasyProviderFactory.getContextResolver(CustomObject.class, MediaType.WILDCARD_TYPE));
    }

    @Test
    public void testExceptionMapperRegistration() throws Exception {
        ResteasyProviderFactory resteasyProviderFactory = ResteasyProviderFactory.newInstance();
        DynamicFeatureContextDelegate featureContext = new DynamicFeatureContextDelegate(resteasyProviderFactory);
        featureContext.register(new CustomExceptionMapper());
        Assertions.assertNotEquals(resteasyProviderFactory.getExceptionMapper(CustomException.class).getClass(),
                CustomExceptionMapper.class);
        featureContext.register(CustomExceptionMapper.class);
        Assertions.assertNotEquals(resteasyProviderFactory.getExceptionMapper(CustomException.class).getClass(),
                CustomExceptionMapper.class);
    }

    @Test
    public void testMessageBodyWriterRegistration() throws Exception {
        ResteasyProviderFactory resteasyProviderFactory = ResteasyProviderFactory.newInstance();
        DynamicFeatureContextDelegate featureContext = new DynamicFeatureContextDelegate(resteasyProviderFactory);
        featureContext.register(new CustomObjectMessageBodyWriter());
        Assertions.assertNull(resteasyProviderFactory.getMessageBodyWriter(CustomObject.class, CustomObject.class, null,
                MediaType.APPLICATION_JSON_TYPE));
        featureContext.register(CustomObjectMessageBodyWriter.class);
        Assertions.assertNull(resteasyProviderFactory.getMessageBodyWriter(CustomObject.class, CustomObject.class, null,
                MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void testMessageBodyReaderRegistration() throws Exception {
        ResteasyProviderFactory resteasyProviderFactory = ResteasyProviderFactory.newInstance();
        DynamicFeatureContextDelegate featureContext = new DynamicFeatureContextDelegate(resteasyProviderFactory);
        featureContext.register(new CustomObjectMessageBodyReader());
        Assertions.assertNull(resteasyProviderFactory.getMessageBodyReader(CustomObject.class, CustomObject.class, null,
                MediaType.APPLICATION_JSON_TYPE));
        featureContext.register(CustomObjectMessageBodyReader.class);
        Assertions.assertNull(resteasyProviderFactory.getMessageBodyReader(CustomObject.class, CustomObject.class, null,
                MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void testDynamicFeatureRegistration() throws Exception {
        ResteasyProviderFactory resteasyProviderFactory = ResteasyProviderFactory.newInstance();
        DynamicFeatureContextDelegate featureContext = new DynamicFeatureContextDelegate(resteasyProviderFactory);
        featureContext.register(new CustomDynamicFeature());
        Assertions.assertNull(resteasyProviderFactory.getServerDynamicFeatures());
        featureContext.register(CustomDynamicFeature.class);
        Assertions.assertNull(resteasyProviderFactory.getServerDynamicFeatures());
    }

}
