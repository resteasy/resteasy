package org.jboss.resteasy.test.providers;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.internal.LocalResteasyProviderFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.providers.resource.ProviderFactoryPrecedenceIntegerPlainTextWriter;
import org.jboss.resteasy.test.providers.resource.ProviderFactoryPrecendencePlainTextWriter;
import org.jboss.resteasy.util.DelegatingOutputStream;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @tpSubChapter Providers
 * @tpChapter Unit tests
 * @tpTestCaseDetails Multipart Contains JSON part should not trigger Stream Closed
 * @tpSince RESTEasy 3.0.14
 */
public class MultipurtContainsJsonTest {
    private static final LogMessages logger = Logger.getMessageLogger(LogMessages.class, MultipurtContainsJsonTest.class.getName());
    private static final Annotation[] EMPTY_ANNOTATION = new Annotation[0];

    public static MultipartFormDataOutput getMultipartWithoutJSON() {
        MultipartFormDataOutput dataOutput = new MultipartFormDataOutput();

        dataOutput.addFormData("str-field", "Hello World", MediaType.TEXT_PLAIN_TYPE);
        dataOutput.addFormData("bytes-field", "text file".getBytes(StandardCharsets.UTF_8), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        return dataOutput;
    }

    public static MessageBodyWriter<MultipartFormDataOutput> getWriter() {
        ResteasyProviderFactory factory = new LocalResteasyProviderFactory(new ResteasyProviderFactory());
        RegisterBuiltin.register(factory);
        factory.registerProviderInstance(new ProviderFactoryPrecendencePlainTextWriter());
        factory.registerProviderInstance(new ProviderFactoryPrecedenceIntegerPlainTextWriter());
        ResteasyProviderFactory.pushContext(Providers.class, factory);


        MultipartFormDataOutput data = getMultipartWithoutJSON();
        return factory.getMessageBodyWriter(MultipartFormDataOutput.class, data.getClass(), EMPTY_ANNOTATION, MediaType.MULTIPART_FORM_DATA_TYPE);
    }

    @Test
    public void testMultipartWithoutJSON() throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MultipartFormDataOutput data = getMultipartWithoutJSON();
        MessageBodyWriter<MultipartFormDataOutput> writer = getWriter();

        DelegatingOutputStream delegatingOutputStream = new DelegatingOutputStream(outputStream) {
            @Override
            public void close() throws IOException {
                throw new IOException("stream closed");
                //super.close();
            }
        };
        MultivaluedMapImpl<String, Object> headers = new MultivaluedMapImpl<String, Object>();
        writer.writeTo(data, data.getClass(), data.getClass(), EMPTY_ANNOTATION, MediaType.MULTIPART_FORM_DATA_TYPE, headers, delegatingOutputStream);
        byte[] buf = outputStream.toByteArray();
        logger.info(new String(buf, StandardCharsets.UTF_8));
    }

    @Test
    public void testMultipartContainsJSON() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MultipartFormDataOutput data = getMultipartWithoutJSON();
        MessageBodyWriter<MultipartFormDataOutput> writer = getWriter();

        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("key1", "value1");
        jsonMap.put("key2", "value2");
        jsonMap.put("key3", 3);
        data.addFormData("json-field", jsonMap, MediaType.APPLICATION_JSON_TYPE);
        data.addFormData("str-field-1", "str-field-1", MediaType.TEXT_PLAIN_TYPE);

        DelegatingOutputStream delegatingOutputStream = new DelegatingOutputStream(outputStream) {
            @Override
            public void close() throws IOException {
                throw new IOException("Stream closed wrongly!!!");
            }
        };
        MultivaluedMapImpl<String, Object> headers = new MultivaluedMapImpl<String, Object>();
        writer.writeTo(data, data.getClass(), data.getClass(), new Annotation[0], MediaType.MULTIPART_FORM_DATA_TYPE, headers, delegatingOutputStream);
        byte[] buf = outputStream.toByteArray();
        logger.info(new String(buf, StandardCharsets.UTF_8));
    }
}
