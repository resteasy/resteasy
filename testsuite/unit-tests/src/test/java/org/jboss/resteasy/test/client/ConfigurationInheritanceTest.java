package org.jboss.resteasy.test.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestFeature1;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestFeature2;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestFeature3;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestFeature4;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestFeature5;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestFeature6;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestFilter1;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestFilter2;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestFilter3;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestFilter4;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestFilter5;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestFilter6;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestMessageBodyReader1;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestMessageBodyReader2;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestMessageBodyReader3;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestMessageBodyReader4;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestMessageBodyReader5;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritanceTestMessageBodyReader6;
import org.jboss.resteasy.test.common.FakeHttpServer;
import org.jboss.resteasy.test.common.TestServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpTestCaseDetails Regression test for RESTEASY-1345
 * @tpSince RESTEasy 3.0.17
 */
public class ConfigurationInheritanceTest extends ResteasyProviderFactoryImpl {
    private static ConfigurationInheritanceTestFeature2 testFeature2 = new ConfigurationInheritanceTestFeature2();
    private static ConfigurationInheritanceTestFeature4 testFeature4 = new ConfigurationInheritanceTestFeature4();
    private static ConfigurationInheritanceTestFeature6 testFeature6 = new ConfigurationInheritanceTestFeature6();
    private static ConfigurationInheritanceTestFilter2 testFilter2 = new ConfigurationInheritanceTestFilter2();
    private static ConfigurationInheritanceTestFilter4 testFilter4 = new ConfigurationInheritanceTestFilter4();
    private static ConfigurationInheritanceTestFilter6 testFilter6 = new ConfigurationInheritanceTestFilter6();
    private static ConfigurationInheritanceTestMessageBodyReader2 testMessageBodyReader2 = new ConfigurationInheritanceTestMessageBodyReader2();
    private static ConfigurationInheritanceTestMessageBodyReader4 testMessageBodyReader4 = new ConfigurationInheritanceTestMessageBodyReader4();
    private static ConfigurationInheritanceTestMessageBodyReader6 testMessageBodyReader6 = new ConfigurationInheritanceTestMessageBodyReader6();

    private static final String ERROR_MSG = "Error during client-side registration";

    @TestServer
    public FakeHttpServer fakeHttpServer;

    /**
     * @tpTestDetails Register items to clientBuilder.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testClientBuilderToClient() {
        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilderImpl();
        clientBuilder.register(ConfigurationInheritanceTestFeature1.class);
        clientBuilder.register(testFeature2);
        clientBuilder.register(new ConfigurationInheritanceTestFilter1());
        clientBuilder.register(testFilter2);
        clientBuilder.register(new ConfigurationInheritanceTestMessageBodyReader1());
        clientBuilder.register(testMessageBodyReader2);
        clientBuilder.property("property1", "value1");

        Client client = clientBuilder.build();
        client.register(ConfigurationInheritanceTestFeature3.class);
        client.register(testFeature4);
        client.register(new ConfigurationInheritanceTestFilter3());
        client.register(testFilter4);
        client.register(new ConfigurationInheritanceTestMessageBodyReader3());
        client.register(testMessageBodyReader4);
        client.property("property2", "value2");

        clientBuilder.register(ConfigurationInheritanceTestFeature5.class);
        clientBuilder.register(testFeature6);
        clientBuilder.register(new ConfigurationInheritanceTestFilter5());
        clientBuilder.register(testFilter6);
        clientBuilder.register(new ConfigurationInheritanceTestMessageBodyReader5());
        clientBuilder.register(testMessageBodyReader6);
        clientBuilder.property("property3", "value3");

        checkFirstConfiguration(clientBuilder.getConfiguration());
        checkSecondConfiguration(client.getConfiguration());
    }

    /**
     * @tpTestDetails Register items to client.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testClientToWebTarget() {
        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilderImpl();
        Client client = clientBuilder.build();
        client.register(ConfigurationInheritanceTestFeature1.class);
        client.register(testFeature2);
        client.register(new ConfigurationInheritanceTestFilter1());
        client.register(testFilter2);
        client.register(new ConfigurationInheritanceTestMessageBodyReader1());
        client.register(testMessageBodyReader2);
        client.property("property1", "value1");

        WebTarget target = client.target("http://localhost:8081");
        target.register(ConfigurationInheritanceTestFeature3.class);
        target.register(testFeature4);
        target.register(new ConfigurationInheritanceTestFilter3());
        target.register(testFilter4);
        target.register(new ConfigurationInheritanceTestMessageBodyReader3());
        target.register(testMessageBodyReader4);
        target.property("property2", "value2");

        client.register(ConfigurationInheritanceTestFeature5.class);
        client.register(testFeature6);
        client.register(new ConfigurationInheritanceTestFilter5());
        client.register(testFilter6);
        client.register(new ConfigurationInheritanceTestMessageBodyReader5());
        client.register(testMessageBodyReader6);
        client.property("property3", "value3");

        checkFirstConfiguration(client.getConfiguration());
        checkSecondConfiguration(target.getConfiguration());
    }

    /**
     * @tpTestDetails Check default RuntimeType oc clientBuilder, client end webTarget.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testRuntimeType() {
        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilderImpl();
        Assertions.assertEquals(RuntimeType.CLIENT,
                clientBuilder.getConfiguration().getRuntimeType(), "Wrong RuntimeType in ClientBuilder");
        Client client = clientBuilder.build();
        Assertions.assertEquals(RuntimeType.CLIENT, client.getConfiguration().getRuntimeType(),
                "Wrong RuntimeType in Client");
        WebTarget target = client.target("http://localhost:8081");
        Assertions.assertEquals(RuntimeType.CLIENT,
                target.getConfiguration().getRuntimeType(), "Wrong RuntimeType in WebTarget");
    }

    @Test
    public void testClientRequestFilterInheritance() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget parentWebTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            WebTarget childWebTarget = parentWebTarget.path("path");

            // Registration on parent MUST not affect the child
            AtomicInteger parentRequestFilterCounter = new AtomicInteger(0);
            ClientRequestFilter parentClientRequestFilter = (containerRequestContext) -> {
                parentRequestFilterCounter.incrementAndGet();
            };
            parentWebTarget.register(parentClientRequestFilter);
            childWebTarget.request().get().close();
            Assertions.assertEquals(0, parentRequestFilterCounter.get());

            // Child MUST only use the snapshot configuration of the parent
            // taken at child creation time.
            AtomicInteger childRequestFilterCounter = new AtomicInteger(0);
            ClientRequestFilter childClientRequestFilter = (containerRequestContext) -> {
                childRequestFilterCounter.incrementAndGet();
            };
            childWebTarget.register(childClientRequestFilter);
            childWebTarget.request().get().close();
            Assertions.assertEquals(1, childRequestFilterCounter.get());
            Assertions.assertEquals(0, parentRequestFilterCounter.get());
        } finally {
            client.close();
        }
    }

    @Test
    public void testClientResponseFilterInheritance() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget parentWebTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            WebTarget childWebTarget = parentWebTarget.path("path");

            // Registration on parent MUST not affect the child
            AtomicInteger parentResponseFilterCounter = new AtomicInteger(0);
            ClientResponseFilter parentClientResponseFilter = (containerRequestContext, containerResponseContext) -> {
                parentResponseFilterCounter.incrementAndGet();
            };
            parentWebTarget.register(parentClientResponseFilter);
            childWebTarget.request().get().close();
            Assertions.assertEquals(0, parentResponseFilterCounter.get());

            // Child MUST only use the snapshot configuration of the parent
            // taken at child creation time.
            AtomicInteger childResponseFilterCounter = new AtomicInteger(0);
            ClientResponseFilter childClientResponseFilter = (containerRequestContext, containerResponseContext) -> {
                childResponseFilterCounter.incrementAndGet();
            };
            childWebTarget.register(childClientResponseFilter);
            childWebTarget.request().get().close();
            Assertions.assertEquals(1, childResponseFilterCounter.get());
            Assertions.assertEquals(0, parentResponseFilterCounter.get());
        } finally {
            client.close();
        }
    }

    @Test
    public void testReaderInterceptorInheritance() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget parentWebTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            WebTarget childWebTarget = parentWebTarget.path("path");
            childWebTarget.register((ClientResponseFilter) (containerRequestContext, containerResponseContext) -> {
                containerResponseContext.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
                containerResponseContext.setEntityStream(new ByteArrayInputStream("hello".getBytes()));
            });

            // Registration on parent MUST not affect the child
            AtomicInteger parentReaderInterceptorCounter = new AtomicInteger(0);
            ReaderInterceptor parentReaderInterceptor = (readerInterceptorContext) -> {
                parentReaderInterceptorCounter.incrementAndGet();
                return readerInterceptorContext.proceed();
            };
            parentWebTarget.register(parentReaderInterceptor);
            childWebTarget.request().get().readEntity(String.class);
            Assertions.assertEquals(0, parentReaderInterceptorCounter.get());

            // Child MUST only use the snapshot configuration of the parent
            // taken at child creation time.
            AtomicInteger childReaderInterceptorCounter = new AtomicInteger(0);
            ReaderInterceptor childReaderInterceptor = (readerInterceptorContext) -> {
                childReaderInterceptorCounter.incrementAndGet();
                return readerInterceptorContext.proceed();
            };
            childWebTarget.register(childReaderInterceptor);
            childWebTarget.request().get().readEntity(String.class);
            Assertions.assertEquals(1, childReaderInterceptorCounter.get());
            Assertions.assertEquals(0, parentReaderInterceptorCounter.get());
        } finally {
            client.close();
        }
    }

    @Test
    public void testWriterInterceptorInheritance() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget parentWebTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            WebTarget childWebTarget = parentWebTarget.path("path");

            // Registration on parent MUST not affect the child
            AtomicInteger parentWriterInterceptorCounter = new AtomicInteger(0);
            WriterInterceptor parentWriterInterceptor = (writerInterceptorContext) -> {
                parentWriterInterceptorCounter.incrementAndGet();
                writerInterceptorContext.proceed();
            };
            parentWebTarget.register(parentWriterInterceptor);
            childWebTarget.request().post(Entity.text("Hello")).close();
            Assertions.assertEquals(0, parentWriterInterceptorCounter.get());

            // Child MUST only use the snapshot configuration of the parent
            // taken at child creation time.
            AtomicInteger childWriterInterceptorCounter = new AtomicInteger(0);
            WriterInterceptor childWriterInterceptor = (writerInterceptorContext) -> {
                childWriterInterceptorCounter.incrementAndGet();
                writerInterceptorContext.proceed();
            };
            childWebTarget.register(childWriterInterceptor);
            childWebTarget.request().post(Entity.text("Hello")).close();
            Assertions.assertEquals(1, childWriterInterceptorCounter.get());
            Assertions.assertEquals(0, parentWriterInterceptorCounter.get());
        } finally {
            client.close();
        }
    }

    @Test
    public void testMessageBodyReaderInheritance() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget parentWebTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            WebTarget childWebTarget = parentWebTarget.path("path");
            childWebTarget.register((ClientResponseFilter) (containerRequestContext, containerResponseContext) -> {
                containerResponseContext.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
                containerResponseContext.setEntityStream(new ByteArrayInputStream("hello".getBytes()));
            });

            // Registration on parent MUST not affect the child
            AtomicInteger parentMessageBodyReaderCounter = new AtomicInteger(0);
            MessageBodyReader<String> parentMessageBodyReader = new MessageBodyReader<String>() {
                @Override
                public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                        throws IOException, WebApplicationException {
                    return null;
                }

                @Override
                public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
                    parentMessageBodyReaderCounter.incrementAndGet();
                    return false;
                }
            };
            parentWebTarget.register(parentMessageBodyReader);
            childWebTarget.request().get().readEntity(String.class);
            Assertions.assertEquals(0, parentMessageBodyReaderCounter.get());

            // Child MUST only use the snapshot configuration of the parent
            // taken at child creation time.
            AtomicInteger childMessageBodyReaderCounter = new AtomicInteger(0);
            MessageBodyReader<String> childMessageBodyReader = new MessageBodyReader<String>() {
                @Override
                public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                        throws IOException, WebApplicationException {
                    return null;
                }

                @Override
                public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
                    childMessageBodyReaderCounter.incrementAndGet();
                    return false;
                }
            };
            childWebTarget.register(childMessageBodyReader);
            childWebTarget.request().get().readEntity(String.class);
            Assertions.assertEquals(1, childMessageBodyReaderCounter.get());
            Assertions.assertEquals(0, parentMessageBodyReaderCounter.get());
        } finally {
            client.close();
        }
    }

    @Test
    public void testMessageBodyWriterInterceptorInheritance() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget parentWebTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            WebTarget childWebTarget = parentWebTarget.path("path");

            // Registration on parent MUST not affect the child
            AtomicInteger parentMessageBodyWriterCounter = new AtomicInteger(0);
            MessageBodyWriter<String> parentMessageBodyWriter = new MessageBodyWriter<String>() {

                @Override
                public void writeTo(String t, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                        throws IOException, WebApplicationException {

                }

                @Override
                public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
                    parentMessageBodyWriterCounter.incrementAndGet();
                    return false;
                }
            };
            parentWebTarget.register(parentMessageBodyWriter);
            childWebTarget.request().post(Entity.text("Hello")).close();
            Assertions.assertEquals(0, parentMessageBodyWriterCounter.get());

            // Child MUST only use the snapshot configuration of the parent
            // taken at child creation time.
            AtomicInteger childMessageBodyWriterCounter = new AtomicInteger(0);
            MessageBodyWriter<String> childMessageBodyWriter = new MessageBodyWriter<String>() {

                @Override
                public void writeTo(String t, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                        throws IOException, WebApplicationException {

                }

                @Override
                public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
                    childMessageBodyWriterCounter.incrementAndGet();
                    return false;
                }
            };
            childWebTarget.register(childMessageBodyWriter);
            childWebTarget.request().post(Entity.text("Hello")).close();
            Assertions.assertEquals(1, childMessageBodyWriterCounter.get());
            Assertions.assertEquals(0, parentMessageBodyWriterCounter.get());
        } finally {
            client.close();
        }
    }

    @Test
    public void testContextResolverInheritance() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget parentWebTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            WebTarget childWebTarget = parentWebTarget.path("path");
            List<String> result = new ArrayList<>();
            childWebTarget.register(new ClientRequestFilter() {

                @Context
                Providers providers;

                @Override
                public void filter(ClientRequestContext requestContext) throws IOException {
                    ContextResolver<String> contextResolver = providers.getContextResolver(String.class,
                            MediaType.WILDCARD_TYPE);
                    if (contextResolver != null) {
                        String context = contextResolver.getContext(getClass());
                        result.add(context);
                    }
                }
            });

            // Registration on parent MUST not affect the child
            ContextResolver<String> parentContextResolver = new ContextResolver<String>() {

                @Override
                public String getContext(Class<?> type) {
                    return "ParentContext";
                }
            };
            parentWebTarget.register(parentContextResolver);
            childWebTarget.request().get().close();
            Assertions.assertTrue(result.isEmpty());

            // Child MUST only use the snapshot configuration of the parent
            // taken at child creation time.
            ContextResolver<String> childContextResolver = new ContextResolver<String>() {

                @Override
                public String getContext(Class<?> type) {
                    return null;
                }
            };
            childWebTarget.register(childContextResolver);
            childWebTarget.request().get().close();
            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(null, result.get(0));
        } finally {
            client.close();
        }
    }

    private void checkFirstConfiguration(Configuration config) {
        Set<Class<?>> classes = config.getClasses();
        Assertions.assertTrue(classes.contains(ConfigurationInheritanceTestFeature1.class), ERROR_MSG);
        Assertions.assertFalse(classes.contains(ConfigurationInheritanceTestFeature3.class), ERROR_MSG);
        Assertions.assertTrue(classes.contains(ConfigurationInheritanceTestFeature5.class), ERROR_MSG);
        Assertions.assertFalse(classes.contains(ConfigurationInheritanceTestFilter3.class), ERROR_MSG);
        Assertions.assertFalse(classes.contains(ConfigurationInheritanceTestFilter4.class), ERROR_MSG);
        Assertions.assertFalse(classes.contains(ConfigurationInheritanceTestMessageBodyReader3.class), ERROR_MSG);
        Assertions.assertFalse(classes.contains(ConfigurationInheritanceTestMessageBodyReader4.class), ERROR_MSG);

        Assertions.assertTrue(config.isEnabled(ConfigurationInheritanceTestFeature1.class), ERROR_MSG);
        Assertions.assertTrue(config.isEnabled(ConfigurationInheritanceTestFeature2.class), ERROR_MSG);
        Assertions.assertFalse(config.isEnabled(ConfigurationInheritanceTestFeature3.class), ERROR_MSG);
        Assertions.assertFalse(config.isEnabled(ConfigurationInheritanceTestFeature4.class), ERROR_MSG);
        Assertions.assertTrue(config.isEnabled(ConfigurationInheritanceTestFeature5.class), ERROR_MSG);
        Assertions.assertTrue(config.isEnabled(ConfigurationInheritanceTestFeature6.class), ERROR_MSG);

        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFeature1.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFeature2.class), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(ConfigurationInheritanceTestFeature3.class), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(ConfigurationInheritanceTestFeature4.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFeature5.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFeature6.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFilter1.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFilter2.class), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(ConfigurationInheritanceTestFilter3.class), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(ConfigurationInheritanceTestFilter4.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFilter5.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFilter6.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestMessageBodyReader1.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestMessageBodyReader2.class), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(ConfigurationInheritanceTestMessageBodyReader3.class), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(ConfigurationInheritanceTestMessageBodyReader4.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestMessageBodyReader5.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestMessageBodyReader6.class), ERROR_MSG);

        Set<Object> instances = config.getInstances();
        Assertions.assertTrue(instances.contains(testFeature2), ERROR_MSG);
        Assertions.assertFalse(instances.contains(testFeature4), ERROR_MSG);
        Assertions.assertTrue(instances.contains(testFeature6), ERROR_MSG);
        Assertions.assertTrue(instances.contains(testFilter2), ERROR_MSG);
        Assertions.assertFalse(instances.contains(testFilter4), ERROR_MSG);
        Assertions.assertTrue(instances.contains(testFilter6), ERROR_MSG);
        Assertions.assertTrue(instances.contains(testMessageBodyReader2), ERROR_MSG);
        Assertions.assertFalse(instances.contains(testMessageBodyReader4), ERROR_MSG);
        Assertions.assertTrue(instances.contains(testMessageBodyReader6), ERROR_MSG);

        Assertions.assertTrue(config.isEnabled(testFeature2), ERROR_MSG);
        Assertions.assertFalse(config.isEnabled(testFeature4), ERROR_MSG);
        Assertions.assertTrue(config.isEnabled(testFeature6), ERROR_MSG);

        Assertions.assertTrue(config.isRegistered(testFeature2), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(testFeature4), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(testFeature6), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(testFilter2), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(testFilter4), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(testFilter6), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(testMessageBodyReader2), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(testMessageBodyReader4), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(testMessageBodyReader6), ERROR_MSG);

        Assertions.assertEquals(2, config.getProperties().size(), ERROR_MSG);
        Assertions.assertEquals("value1", config.getProperty("property1"), ERROR_MSG);
        Assertions.assertEquals("value3", config.getProperty("property3"), ERROR_MSG);

        Assertions.assertFalse(config.getContracts(ConfigurationInheritanceTestFeature1.class).isEmpty(), ERROR_MSG);
        Assertions.assertFalse(config.getContracts(ConfigurationInheritanceTestFeature2.class).isEmpty(), ERROR_MSG);
        Assertions.assertTrue(config.getContracts(ConfigurationInheritanceTestFeature3.class).isEmpty(), ERROR_MSG);
        Assertions.assertTrue(config.getContracts(ConfigurationInheritanceTestFeature4.class).isEmpty(), ERROR_MSG);
        Assertions.assertFalse(config.getContracts(ConfigurationInheritanceTestFeature5.class).isEmpty(), ERROR_MSG);
        Assertions.assertFalse(config.getContracts(ConfigurationInheritanceTestFeature6.class).isEmpty(), ERROR_MSG);
    }

    private void checkSecondConfiguration(Configuration config) {
        Set<Class<?>> classes = config.getClasses();
        Assertions.assertTrue(classes.contains(ConfigurationInheritanceTestFeature1.class), ERROR_MSG);
        Assertions.assertTrue(classes.contains(ConfigurationInheritanceTestFeature3.class), ERROR_MSG);
        Assertions.assertFalse(classes.contains(ConfigurationInheritanceTestFeature5.class), ERROR_MSG);

        Assertions.assertTrue(config.isEnabled(ConfigurationInheritanceTestFeature1.class), ERROR_MSG);
        Assertions.assertTrue(config.isEnabled(ConfigurationInheritanceTestFeature2.class), ERROR_MSG);
        Assertions.assertTrue(config.isEnabled(ConfigurationInheritanceTestFeature3.class), ERROR_MSG);
        Assertions.assertTrue(config.isEnabled(ConfigurationInheritanceTestFeature4.class), ERROR_MSG);
        Assertions.assertFalse(config.isEnabled(ConfigurationInheritanceTestFeature5.class), ERROR_MSG);
        Assertions.assertFalse(config.isEnabled(ConfigurationInheritanceTestFeature6.class), ERROR_MSG);

        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFeature1.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFeature2.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFeature3.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFeature4.class), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(ConfigurationInheritanceTestFeature5.class), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(ConfigurationInheritanceTestFeature6.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFilter1.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFilter2.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFilter3.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestFilter4.class), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(ConfigurationInheritanceTestFilter5.class), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(ConfigurationInheritanceTestFilter6.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestMessageBodyReader1.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestMessageBodyReader2.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestMessageBodyReader3.class), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(ConfigurationInheritanceTestMessageBodyReader4.class), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(ConfigurationInheritanceTestMessageBodyReader5.class), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(ConfigurationInheritanceTestMessageBodyReader6.class), ERROR_MSG);

        Set<Object> instances = config.getInstances();
        Assertions.assertTrue(instances.contains(testFeature2), ERROR_MSG);
        Assertions.assertTrue(instances.contains(testFeature4), ERROR_MSG);
        Assertions.assertFalse(instances.contains(testFeature6), ERROR_MSG);
        Assertions.assertTrue(instances.contains(testFilter2), ERROR_MSG);
        Assertions.assertTrue(instances.contains(testFilter4), ERROR_MSG);
        Assertions.assertFalse(instances.contains(testFilter6), ERROR_MSG);
        Assertions.assertTrue(instances.contains(testMessageBodyReader2), ERROR_MSG);
        Assertions.assertTrue(instances.contains(testMessageBodyReader4), ERROR_MSG);
        Assertions.assertFalse(instances.contains(testMessageBodyReader6), ERROR_MSG);

        Assertions.assertTrue(config.isEnabled(testFeature2), ERROR_MSG);
        Assertions.assertTrue(config.isEnabled(testFeature4), ERROR_MSG);
        Assertions.assertFalse(config.isEnabled(testFeature6), ERROR_MSG);

        Assertions.assertTrue(config.isRegistered(testFeature2), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(testFeature4), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(testFeature6), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(testFilter2), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(testFilter4), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(testFilter6), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(testMessageBodyReader2), ERROR_MSG);
        Assertions.assertTrue(config.isRegistered(testMessageBodyReader4), ERROR_MSG);
        Assertions.assertFalse(config.isRegistered(testMessageBodyReader6), ERROR_MSG);

        Assertions.assertEquals(2, config.getProperties().size(), ERROR_MSG);
        Assertions.assertEquals("value1", config.getProperty("property1"), ERROR_MSG);
        Assertions.assertEquals("value2", config.getProperty("property2"), ERROR_MSG);

        Assertions.assertFalse(config.getContracts(ConfigurationInheritanceTestFeature1.class).isEmpty(), ERROR_MSG);
        Assertions.assertFalse(config.getContracts(ConfigurationInheritanceTestFeature2.class).isEmpty(), ERROR_MSG);
        Assertions.assertFalse(config.getContracts(ConfigurationInheritanceTestFeature3.class).isEmpty(), ERROR_MSG);
        Assertions.assertFalse(config.getContracts(ConfigurationInheritanceTestFeature4.class).isEmpty(), ERROR_MSG);
        Assertions.assertTrue(config.getContracts(ConfigurationInheritanceTestFeature5.class).isEmpty(), ERROR_MSG);
        Assertions.assertTrue(config.getContracts(ConfigurationInheritanceTestFeature6.class).isEmpty(), ERROR_MSG);
    }
}
