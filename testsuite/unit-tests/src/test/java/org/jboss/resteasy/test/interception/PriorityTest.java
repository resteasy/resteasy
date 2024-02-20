package org.jboss.resteasy.test.interception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import org.jboss.resteasy.core.interception.jaxrs.ClientResponseFilterRegistryImpl;
import org.jboss.resteasy.core.interception.jaxrs.ContainerResponseFilterRegistryImpl;
import org.jboss.resteasy.core.interception.jaxrs.JaxrsInterceptorRegistryImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.test.common.FakeHttpServer;
import org.jboss.resteasy.test.common.TestServer;
import org.jboss.resteasy.test.interception.resource.PriorityClientRequestFilter1;
import org.jboss.resteasy.test.interception.resource.PriorityClientRequestFilter2;
import org.jboss.resteasy.test.interception.resource.PriorityClientRequestFilter3;
import org.jboss.resteasy.test.interception.resource.PriorityClientResponseFilter1;
import org.jboss.resteasy.test.interception.resource.PriorityClientResponseFilter2;
import org.jboss.resteasy.test.interception.resource.PriorityClientResponseFilter3;
import org.jboss.resteasy.test.interception.resource.PriorityContainerResponseFilter1;
import org.jboss.resteasy.test.interception.resource.PriorityContainerResponseFilter2;
import org.jboss.resteasy.test.interception.resource.PriorityContainerResponseFilter3;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Interception tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Check functionality of Priority annotation on filter classes. Use more classes with different value in
 *                    Priority annotation.
 * @tpSince RESTEasy 3.0.16
 */
public class PriorityTest {

    private static final String ERROR_MESSAGE = "RESTEasy uses filter in wrong older";

    @TestServer
    public FakeHttpServer fakeHttpServer;

    /**
     * @tpTestDetails Test for classes implements ContainerResponseFilter.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPriority() throws Exception {
        JaxrsInterceptorRegistry<ContainerResponseFilter> containerResponseFilterRegistry = new ContainerResponseFilterRegistryImpl(
                ResteasyProviderFactory.newInstance());
        JaxrsInterceptorRegistry<ClientResponseFilter> clientResponseFilterRegistry = new ClientResponseFilterRegistryImpl(
                ResteasyProviderFactory.newInstance());
        JaxrsInterceptorRegistry<ClientRequestFilter> clientRequestFilterRegistry = new JaxrsInterceptorRegistryImpl<ClientRequestFilter>(
                ResteasyProviderFactory.newInstance(), ClientRequestFilter.class);

        containerResponseFilterRegistry.registerClass(PriorityContainerResponseFilter2.class);
        containerResponseFilterRegistry.registerClass(PriorityContainerResponseFilter1.class);
        containerResponseFilterRegistry.registerClass(PriorityContainerResponseFilter3.class);

        ContainerResponseFilter[] containerResponseFilters = containerResponseFilterRegistry.postMatch(null, null);
        assertTrue(containerResponseFilters[0] instanceof PriorityContainerResponseFilter3, ERROR_MESSAGE);
        assertTrue(containerResponseFilters[1] instanceof PriorityContainerResponseFilter2, ERROR_MESSAGE);
        assertTrue(containerResponseFilters[2] instanceof PriorityContainerResponseFilter1, ERROR_MESSAGE);

        clientResponseFilterRegistry.registerClass(PriorityClientResponseFilter3.class);
        clientResponseFilterRegistry.registerClass(PriorityClientResponseFilter1.class);
        clientResponseFilterRegistry.registerClass(PriorityClientResponseFilter2.class);

        ClientResponseFilter[] clientResponseFilters = clientResponseFilterRegistry.postMatch(null, null);
        assertTrue(clientResponseFilters[0] instanceof PriorityClientResponseFilter3, ERROR_MESSAGE);
        assertTrue(clientResponseFilters[1] instanceof PriorityClientResponseFilter2, ERROR_MESSAGE);
        assertTrue(clientResponseFilters[2] instanceof PriorityClientResponseFilter1, ERROR_MESSAGE);

        clientRequestFilterRegistry.registerClass(PriorityClientRequestFilter3.class);
        clientRequestFilterRegistry.registerClass(PriorityClientRequestFilter1.class);
        clientRequestFilterRegistry.registerClass(PriorityClientRequestFilter2.class);

        ClientRequestFilter[] clientRequestFilters = clientRequestFilterRegistry.postMatch(null, null);
        assertTrue(clientRequestFilters[0] instanceof PriorityClientRequestFilter1, ERROR_MESSAGE);
        assertTrue(clientRequestFilters[1] instanceof PriorityClientRequestFilter2, ERROR_MESSAGE);
        assertTrue(clientRequestFilters[2] instanceof PriorityClientRequestFilter3, ERROR_MESSAGE);

    }

    /**
     * @tpTestDetails Test for classes implements ClientRequestFilter.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPriorityOverride() {
        JaxrsInterceptorRegistry<ClientRequestFilter> clientRequestFilterRegistry = new JaxrsInterceptorRegistryImpl<ClientRequestFilter>(
                ResteasyProviderFactory.newInstance(), ClientRequestFilter.class);

        clientRequestFilterRegistry.registerClass(PriorityClientRequestFilter3.class, 100);
        clientRequestFilterRegistry.registerClass(PriorityClientRequestFilter1.class, 200);
        clientRequestFilterRegistry.registerClass(PriorityClientRequestFilter2.class, 300);

        ClientRequestFilter[] clientRequestFilters = clientRequestFilterRegistry.postMatch(null, null);
        assertTrue(clientRequestFilters[0] instanceof PriorityClientRequestFilter3, ERROR_MESSAGE);
        assertTrue(clientRequestFilters[1] instanceof PriorityClientRequestFilter1, ERROR_MESSAGE);
        assertTrue(clientRequestFilters[2] instanceof PriorityClientRequestFilter2, ERROR_MESSAGE);
    }

    @Test
    public void testClientRequestFilterPriorityOverride() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget webTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            StringBuilder result = new StringBuilder();
            webTarget.register(new ClientRequestFilter() {
                @Override
                public void filter(ClientRequestContext requestContext) throws IOException {
                    result.append("K");
                }
            }, 1);
            webTarget.register(new ClientRequestFilter() {
                @Override
                public void filter(ClientRequestContext requestContext) throws IOException {
                    result.append("O");
                }
            }, 0);
            webTarget.request().get().close();
            Assertions.assertEquals("OK", result.toString());
        } finally {
            client.close();
        }
    }

    @Test
    public void testClientResponseFilterPriorityOverride() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget webTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            StringBuilder result = new StringBuilder();
            webTarget.register(new ClientResponseFilter() {
                @Override
                public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext)
                        throws IOException {
                    result.append("O");
                }
            }, 1);
            webTarget.register(new ClientResponseFilter() {
                @Override
                public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext)
                        throws IOException {
                    result.append("K");
                }
            }, 0);
            webTarget.request().get().close();
            Assertions.assertEquals("OK", result.toString());
        } finally {
            client.close();
        }
    }

    @Test
    public void testReaderInterceptorPriorityOverride() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget webTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            webTarget.register((ClientResponseFilter) (containerRequestContext, containerResponseContext) -> {
                containerResponseContext.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
                containerResponseContext.setEntityStream(new ByteArrayInputStream("hello".getBytes()));
            });
            StringBuilder result = new StringBuilder();
            webTarget.register(new ReaderInterceptor() {
                @Override
                public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
                    result.append("K");
                    return context.proceed();
                }
            }, 1);
            webTarget.register(new ReaderInterceptor() {

                @Override
                public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
                    result.append("O");
                    return context.proceed();
                }
            }, 0);
            webTarget.request().get().readEntity(String.class);
            Assertions.assertEquals("OK", result.toString());
        } finally {
            client.close();
        }
    }

    @Test
    public void testWriterPriorityOverride() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget webTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            StringBuilder result = new StringBuilder();
            webTarget.register(new WriterInterceptor() {
                @Override
                public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
                    result.append("K");
                    context.proceed();
                }
            }, 1);
            webTarget.register(new WriterInterceptor() {
                @Override
                public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
                    result.append("O");
                    context.proceed();
                }
            }, 0);
            webTarget.request().post(Entity.text("Hello")).close();
            Assertions.assertEquals("OK", result.toString());
        } finally {
            client.close();
        }
    }

    @Test
    public void testMessageBodyReaderPriorityOverride() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget webTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            webTarget.register((ClientResponseFilter) (containerRequestContext, containerResponseContext) -> {
                containerResponseContext.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
                containerResponseContext.setEntityStream(new ByteArrayInputStream("hello".getBytes()));
            });
            StringBuilder result = new StringBuilder();
            webTarget.register(new MessageBodyReader<String>() {
                @Override
                public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
                    result.append("K");
                    return false;
                }

                @Override
                public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                        throws IOException, WebApplicationException {
                    return null;
                }
            }, 1);
            webTarget.register(new MessageBodyReader<String>() {
                @Override
                public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
                    result.append("O");
                    return false;
                }

                @Override
                public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                        throws IOException, WebApplicationException {
                    return null;
                }
            }, 0);
            webTarget.request().get().readEntity(String.class);
            Assertions.assertEquals("OK", result.toString());
        } finally {
            client.close();
        }
    }

    @Test
    public void testMessageBodyWriterPriorityOverride() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget webTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            StringBuilder result = new StringBuilder();
            webTarget.register(new MessageBodyWriter<String>() {
                @Override
                public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
                    result.append("K");
                    return false;
                }

                @Override
                public void writeTo(String t, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                        throws IOException, WebApplicationException {
                }
            }, 1);
            webTarget.register(new MessageBodyWriter<String>() {
                @Override
                public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
                    result.append("O");
                    return false;
                }

                @Override
                public void writeTo(String t, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                        throws IOException, WebApplicationException {
                }
            }, 0);
            webTarget.request().post(Entity.text("Hello")).close();
            Assertions.assertEquals("OK", result.toString());
        } finally {
            client.close();
        }
    }

    @Test
    public void testContextResolverPriorityOverride() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget webTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            webTarget.register(new ClientRequestFilter() {
                @Context
                Providers providers;

                @Override
                public void filter(ClientRequestContext requestContext) throws IOException {
                    providers.getContextResolver(String.class, MediaType.WILDCARD_TYPE).getContext(getClass());
                }
            });
            StringBuilder result = new StringBuilder();
            webTarget.register(new ContextResolver<String>() {
                @Override
                public String getContext(Class<?> type) {
                    result.append("O");
                    return null;
                }
            }, 0);
            webTarget.register(new ContextResolver<String>() {
                @Override
                public String getContext(Class<?> type) {
                    result.append("K");
                    return null;
                }
            }, 1);
            webTarget.request().get().close();
            Assertions.assertEquals("OK", result.toString());
        } finally {
            client.close();
        }
    }

    @Test
    public void testContextResolverPriorityOverride_2() {
        Client client = ClientBuilder.newClient();
        try {
            fakeHttpServer.start();

            WebTarget webTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
            webTarget.register(new ClientRequestFilter() {
                @Context
                Providers providers;

                @Override
                public void filter(ClientRequestContext requestContext) throws IOException {
                    providers.getContextResolver(String.class, MediaType.WILDCARD_TYPE).getContext(getClass());
                }
            });
            StringBuilder result = new StringBuilder();
            webTarget.register(new ContextResolver<String>() {
                @Override
                public String getContext(Class<?> type) {
                    result.append("K");
                    return null;
                }
            }, 1);
            webTarget.register(new ContextResolver<String>() {
                @Override
                public String getContext(Class<?> type) {
                    result.append("O");
                    return null;
                }
            }, 0);
            webTarget.request().get().close();
            Assertions.assertEquals("OK", result.toString());
        } finally {
            client.close();
        }
    }
}
