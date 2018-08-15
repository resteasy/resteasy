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

import javax.ws.rs.RuntimeType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFeature1;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFeature2;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFeature3;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFeature4;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFeature5;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFeature6;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFilter1;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFilter2;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFilter3;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFilter4;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFilter5;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestFilter6;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestMessageBodyReader1;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestMessageBodyReader2;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestMessageBodyReader3;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestMessageBodyReader4;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestMessageBodyReader5;
import org.jboss.resteasy.test.client.resource.ConfigurationInheritenceTestMessageBodyReader6;
import org.junit.Assert;
import org.junit.Test;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpTestCaseDetails Regression test for RESTEASY-1345
 * @tpSince RESTEasy 3.0.17
 */
public class ConfigurationInheritenceTest extends ResteasyProviderFactory {
    private static ConfigurationInheritenceTestFeature2 testFeature2 = new ConfigurationInheritenceTestFeature2();
    private static ConfigurationInheritenceTestFeature4 testFeature4 = new ConfigurationInheritenceTestFeature4();
    private static ConfigurationInheritenceTestFeature6 testFeature6 = new ConfigurationInheritenceTestFeature6();
    private static ConfigurationInheritenceTestFilter2 testFilter2 = new ConfigurationInheritenceTestFilter2();
    private static ConfigurationInheritenceTestFilter4 testFilter4 = new ConfigurationInheritenceTestFilter4();
    private static ConfigurationInheritenceTestFilter6 testFilter6 = new ConfigurationInheritenceTestFilter6();
    private static ConfigurationInheritenceTestMessageBodyReader2 testMessageBodyReader2 = new ConfigurationInheritenceTestMessageBodyReader2();
    private static ConfigurationInheritenceTestMessageBodyReader4 testMessageBodyReader4 = new ConfigurationInheritenceTestMessageBodyReader4();
    private static ConfigurationInheritenceTestMessageBodyReader6 testMessageBodyReader6 = new ConfigurationInheritenceTestMessageBodyReader6();

    private static final String ERROR_MSG = "Error during client-side registration";

    /**
     * @tpTestDetails Register items to clientBuilder.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testClientBuilderToClient() {
        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
        clientBuilder.register(ConfigurationInheritenceTestFeature1.class);
        clientBuilder.register(testFeature2);
        clientBuilder.register(new ConfigurationInheritenceTestFilter1());
        clientBuilder.register(testFilter2);
        clientBuilder.register(new ConfigurationInheritenceTestMessageBodyReader1());
        clientBuilder.register(testMessageBodyReader2);
        clientBuilder.property("property1", "value1");

        Client client = clientBuilder.build();
        client.register(ConfigurationInheritenceTestFeature3.class);
        client.register(testFeature4);
        client.register(new ConfigurationInheritenceTestFilter3());
        client.register(testFilter4);
        client.register(new ConfigurationInheritenceTestMessageBodyReader3());
        client.register(testMessageBodyReader4);
        client.property("property2", "value2");

        clientBuilder.register(ConfigurationInheritenceTestFeature5.class);
        clientBuilder.register(testFeature6);
        clientBuilder.register(new ConfigurationInheritenceTestFilter5());
        clientBuilder.register(testFilter6);
        clientBuilder.register(new ConfigurationInheritenceTestMessageBodyReader5());
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
        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
        Client client = clientBuilder.build();
        client.register(ConfigurationInheritenceTestFeature1.class);
        client.register(testFeature2);
        client.register(new ConfigurationInheritenceTestFilter1());
        client.register(testFilter2);
        client.register(new ConfigurationInheritenceTestMessageBodyReader1());
        client.register(testMessageBodyReader2);
        client.property("property1", "value1");

        WebTarget target = client.target("http://localhost:8081");
        target.register(ConfigurationInheritenceTestFeature3.class);
        target.register(testFeature4);
        target.register(new ConfigurationInheritenceTestFilter3());
        target.register(testFilter4);
        target.register(new ConfigurationInheritenceTestMessageBodyReader3());
        target.register(testMessageBodyReader4);
        target.property("property2", "value2");

        client.register(ConfigurationInheritenceTestFeature5.class);
        client.register(testFeature6);
        client.register(new ConfigurationInheritenceTestFilter5());
        client.register(testFilter6);
        client.register(new ConfigurationInheritenceTestMessageBodyReader5());
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
        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
        Assert.assertEquals("Wrong RuntimeType in ClientBuilder", RuntimeType.CLIENT, clientBuilder.getConfiguration().getRuntimeType());
        Client client = clientBuilder.build();
        Assert.assertEquals("Wrong RuntimeType in Client", RuntimeType.CLIENT, client.getConfiguration().getRuntimeType());
        WebTarget target = client.target("http://localhost:8081");
        Assert.assertEquals("Wrong RuntimeType in WebTarget", RuntimeType.CLIENT, target.getConfiguration().getRuntimeType());
    }

   @Test
   public void testClientRequestFilterInheritence()
   {
      Client client = ClientBuilder.newClient();
      try
      {
         WebTarget parentWebTarget = client.target("http://www.test.com");
         WebTarget childWebTarget = parentWebTarget.path("path");

         // Registration on parent MUST not affect the child
         AtomicInteger parentRequestFilterCounter = new AtomicInteger(0);
         ClientRequestFilter parentClientRequestFilter = (containerRequestContext) -> {
            parentRequestFilterCounter.incrementAndGet();
         };
         parentWebTarget.register(parentClientRequestFilter);
         childWebTarget.request().get().close();
         Assert.assertEquals(0, parentRequestFilterCounter.get());

         // Child MUST only use the snapshot configuration of the parent
         // taken at child creation time.
         AtomicInteger childRequestFilterCounter = new AtomicInteger(0);
         ClientRequestFilter childClientRequestFilter = (containerRequestContext) -> {
            childRequestFilterCounter.incrementAndGet();
         };
         childWebTarget.register(childClientRequestFilter);
         childWebTarget.request().get().close();
         Assert.assertEquals(1, childRequestFilterCounter.get());
         Assert.assertEquals(0, parentRequestFilterCounter.get());
      }
      finally
      {
         client.close();
      }
   }

   @Test
   public void testClientResponseFilterInheritence()
   {
      Client client = ClientBuilder.newClient();
      try
      {
         WebTarget parentWebTarget = client.target("http://www.test.com");
         WebTarget childWebTarget = parentWebTarget.path("path");

         // Registration on parent MUST not affect the child
         AtomicInteger parentResponseFilterCounter = new AtomicInteger(0);
         ClientResponseFilter parentClientResponseFilter = (containerRequestContext, containerResponseContext) -> {
            parentResponseFilterCounter.incrementAndGet();
         };
         parentWebTarget.register(parentClientResponseFilter);
         childWebTarget.request().get().close();
         Assert.assertEquals(0, parentResponseFilterCounter.get());

         // Child MUST only use the snapshot configuration of the parent
         // taken at child creation time.
         AtomicInteger childResponseFilterCounter = new AtomicInteger(0);
         ClientResponseFilter childClientResponseFilter = (containerRequestContext, containerResponseContext) -> {
            childResponseFilterCounter.incrementAndGet();
         };
         childWebTarget.register(childClientResponseFilter);
         childWebTarget.request().get().close();
         Assert.assertEquals(1, childResponseFilterCounter.get());
         Assert.assertEquals(0, parentResponseFilterCounter.get());
      }
      finally
      {
         client.close();
      }
   }

   @Test
   public void testReaderInterceptorInheritence()
   {
      Client client = ClientBuilder.newClient();
      try
      {
         WebTarget parentWebTarget = client.target("http://www.test.com");
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
         Assert.assertEquals(0, parentReaderInterceptorCounter.get());

         // Child MUST only use the snapshot configuration of the parent
         // taken at child creation time.
         AtomicInteger childReaderInterceptorCounter = new AtomicInteger(0);
         ReaderInterceptor childReaderInterceptor = (readerInterceptorContext) -> {
            childReaderInterceptorCounter.incrementAndGet();
            return readerInterceptorContext.proceed();
         };
         childWebTarget.register(childReaderInterceptor);
         childWebTarget.request().get().readEntity(String.class);
         Assert.assertEquals(1, childReaderInterceptorCounter.get());
         Assert.assertEquals(0, parentReaderInterceptorCounter.get());
      }
      finally
      {
         client.close();
      }
   }

   @Test
   public void testWriterInterceptorInheritence()
   {
      Client client = ClientBuilder.newClient();
      try
      {
         WebTarget parentWebTarget = client.target("http://www.test.com");
         WebTarget childWebTarget = parentWebTarget.path("path");

         // Registration on parent MUST not affect the child
         AtomicInteger parentWriterInterceptorCounter = new AtomicInteger(0);
         WriterInterceptor parentWriterInterceptor = (writerInterceptorContext) -> {
            parentWriterInterceptorCounter.incrementAndGet();
            writerInterceptorContext.proceed();
         };
         parentWebTarget.register(parentWriterInterceptor);
         childWebTarget.request().post(Entity.text("Hello")).close();
         Assert.assertEquals(0, parentWriterInterceptorCounter.get());

         // Child MUST only use the snapshot configuration of the parent
         // taken at child creation time.
         AtomicInteger childWriterInterceptorCounter = new AtomicInteger(0);
         WriterInterceptor childWriterInterceptor = (writerInterceptorContext) -> {
            childWriterInterceptorCounter.incrementAndGet();
            writerInterceptorContext.proceed();
         };
         childWebTarget.register(childWriterInterceptor);
         childWebTarget.request().post(Entity.text("Hello")).close();
         Assert.assertEquals(1, childWriterInterceptorCounter.get());
         Assert.assertEquals(0, parentWriterInterceptorCounter.get());
      }
      finally
      {
         client.close();
      }
   }

   @Test
   public void testMessageBodyReaderInheritence()
   {
      Client client = ClientBuilder.newClient();
      try
      {
         WebTarget parentWebTarget = client.target("http://www.test.com");
         WebTarget childWebTarget = parentWebTarget.path("path");
         childWebTarget.register((ClientResponseFilter) (containerRequestContext, containerResponseContext) -> {
            containerResponseContext.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
            containerResponseContext.setEntityStream(new ByteArrayInputStream("hello".getBytes()));
         });

         // Registration on parent MUST not affect the child
         AtomicInteger parentMessageBodyReaderCounter = new AtomicInteger(0);
         MessageBodyReader<String> parentMessageBodyReader = new MessageBodyReader<String>()
         {
            @Override
            public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                  MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                  throws IOException, WebApplicationException
            {
               return null;
            }

            @Override
            public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
            {
               parentMessageBodyReaderCounter.incrementAndGet();
               return false;
            }
         };
         parentWebTarget.register(parentMessageBodyReader);
         childWebTarget.request().get().readEntity(String.class);
         Assert.assertEquals(0, parentMessageBodyReaderCounter.get());

         // Child MUST only use the snapshot configuration of the parent
         // taken at child creation time.
         AtomicInteger childMessageBodyReaderCounter = new AtomicInteger(0);
         MessageBodyReader<String> childMessageBodyReader = new MessageBodyReader<String>()
         {
            @Override
            public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                  MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                  throws IOException, WebApplicationException
            {
               return null;
            }

            @Override
            public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
            {
               childMessageBodyReaderCounter.incrementAndGet();
               return false;
            }
         };
         childWebTarget.register(childMessageBodyReader);
         childWebTarget.request().get().readEntity(String.class);
         Assert.assertEquals(1, childMessageBodyReaderCounter.get());
         Assert.assertEquals(0, parentMessageBodyReaderCounter.get());
      }
      finally
      {
         client.close();
      }
   }

   @Test
   public void testMessageBodyWriterInterceptorInheritence()
   {
      Client client = ClientBuilder.newClient();
      try
      {
         WebTarget parentWebTarget = client.target("http://www.test.com");
         WebTarget childWebTarget = parentWebTarget.path("path");

         // Registration on parent MUST not affect the child
         AtomicInteger parentMessageBodyWriterCounter = new AtomicInteger(0);
         MessageBodyWriter<String> parentMessageBodyWriter = new MessageBodyWriter<String>()
         {

            @Override
            public void writeTo(String t, Class<?> type, Type genericType, Annotation[] annotations,
                  MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                  throws IOException, WebApplicationException
            {

            }

            @Override
            public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
            {
               parentMessageBodyWriterCounter.incrementAndGet();
               return false;
            }
         };
         parentWebTarget.register(parentMessageBodyWriter);
         childWebTarget.request().post(Entity.text("Hello")).close();
         Assert.assertEquals(0, parentMessageBodyWriterCounter.get());

         // Child MUST only use the snapshot configuration of the parent
         // taken at child creation time.
         AtomicInteger childMessageBodyWriterCounter = new AtomicInteger(0);
         MessageBodyWriter<String> childMessageBodyWriter = new MessageBodyWriter<String>()
         {

            @Override
            public void writeTo(String t, Class<?> type, Type genericType, Annotation[] annotations,
                  MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                  throws IOException, WebApplicationException
            {

            }

            @Override
            public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
            {
               childMessageBodyWriterCounter.incrementAndGet();
               return false;
            }
         };
         childWebTarget.register(childMessageBodyWriter);
         childWebTarget.request().post(Entity.text("Hello")).close();
         Assert.assertEquals(1, childMessageBodyWriterCounter.get());
         Assert.assertEquals(0, parentMessageBodyWriterCounter.get());
      }
      finally
      {
         client.close();
      }
   }

   @Test
   public void testContextResolverInheritence()
   {
      Client client = ClientBuilder.newClient();
      try
      {
         WebTarget parentWebTarget = client.target("http://www.test.com");
         WebTarget childWebTarget = parentWebTarget.path("path");
         List<String> result = new ArrayList<>();
         childWebTarget.register(new ClientRequestFilter()
         {

            @Context
            Providers providers;

            @Override
            public void filter(ClientRequestContext requestContext) throws IOException
            {
               ContextResolver<String> contextResolver = providers.getContextResolver(String.class,
                     MediaType.WILDCARD_TYPE);
               if (contextResolver != null)
               {
                  String context = contextResolver.getContext(getClass());
                  result.add(context);
               }
            }
         });

         // Registration on parent MUST not affect the child
         ContextResolver<String> parentContextResolver = new ContextResolver<String>()
         {

            @Override
            public String getContext(Class<?> type)
            {
               return "ParentContext";
            }
         };
         parentWebTarget.register(parentContextResolver);
         childWebTarget.request().get().close();
         Assert.assertTrue(result.isEmpty());

         // Child MUST only use the snapshot configuration of the parent
         // taken at child creation time.
         ContextResolver<String> childContextResolver = new ContextResolver<String>()
         {

            @Override
            public String getContext(Class<?> type)
            {
               return null;
            }
         };
         childWebTarget.register(childContextResolver);
         childWebTarget.request().get().close();
         Assert.assertEquals(1, result.size());
         Assert.assertEquals(null, result.get(0));
      }
      finally
      {
         client.close();
      }
   }
   
    private void checkFirstConfiguration(Configuration config) {
        Set<Class<?>> classes = config.getClasses();
        Assert.assertTrue(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFeature1.class));
        Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFeature3.class));
        Assert.assertTrue(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFeature5.class));
        Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFilter3.class));
        Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFilter4.class));
        Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritenceTestMessageBodyReader3.class));
        Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritenceTestMessageBodyReader4.class));

        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature1.class));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature2.class));
        Assert.assertFalse(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature3.class));
        Assert.assertFalse(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature4.class));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature5.class));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature6.class));

        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature1.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature2.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature3.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature4.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature5.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature6.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter1.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter2.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter3.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter4.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter5.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter6.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader1.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader2.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader3.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader4.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader5.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader6.class));

        Set<Object> instances = config.getInstances();
        Assert.assertTrue(ERROR_MSG, instances.contains(testFeature2));
        Assert.assertFalse(ERROR_MSG, instances.contains(testFeature4));
        Assert.assertTrue(ERROR_MSG, instances.contains(testFeature6));
        Assert.assertTrue(ERROR_MSG, instances.contains(testFilter2));
        Assert.assertFalse(ERROR_MSG, instances.contains(testFilter4));
        Assert.assertTrue(ERROR_MSG, instances.contains(testFilter6));
        Assert.assertTrue(ERROR_MSG, instances.contains(testMessageBodyReader2));
        Assert.assertFalse(ERROR_MSG, instances.contains(testMessageBodyReader4));
        Assert.assertTrue(ERROR_MSG, instances.contains(testMessageBodyReader6));

        Assert.assertTrue(ERROR_MSG, config.isEnabled(testFeature2));
        Assert.assertFalse(ERROR_MSG, config.isEnabled(testFeature4));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(testFeature6));

        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFeature2));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(testFeature4));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFeature6));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFilter2));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(testFilter4));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFilter6));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testMessageBodyReader2));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(testMessageBodyReader4));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testMessageBodyReader6));

        Assert.assertEquals(ERROR_MSG, 2, config.getProperties().size());
        Assert.assertEquals(ERROR_MSG, "value1", config.getProperty("property1"));
        Assert.assertEquals(ERROR_MSG, "value3", config.getProperty("property3"));

        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature1.class).isEmpty());
        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature2.class).isEmpty());
        Assert.assertTrue(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature3.class).isEmpty());
        Assert.assertTrue(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature4.class).isEmpty());
        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature5.class).isEmpty());
        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature6.class).isEmpty());
    }

    private void checkSecondConfiguration(Configuration config) {
        Set<Class<?>> classes = config.getClasses();
        Assert.assertTrue(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFeature1.class));
        Assert.assertTrue(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFeature3.class));
        Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritenceTestFeature5.class));

        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature1.class));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature2.class));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature3.class));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature4.class));
        Assert.assertFalse(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature5.class));
        Assert.assertFalse(ERROR_MSG, config.isEnabled(ConfigurationInheritenceTestFeature6.class));

        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature1.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature2.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature3.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature4.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature5.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFeature6.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter1.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter2.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter3.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter4.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter5.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestFilter6.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader1.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader2.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader3.class));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader4.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader5.class));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritenceTestMessageBodyReader6.class));

        Set<Object> instances = config.getInstances();
        Assert.assertTrue(ERROR_MSG, instances.contains(testFeature2));
        Assert.assertTrue(ERROR_MSG, instances.contains(testFeature4));
        Assert.assertFalse(ERROR_MSG, instances.contains(testFeature6));
        Assert.assertTrue(ERROR_MSG, instances.contains(testFilter2));
        Assert.assertTrue(ERROR_MSG, instances.contains(testFilter4));
        Assert.assertFalse(ERROR_MSG, instances.contains(testFilter6));
        Assert.assertTrue(ERROR_MSG, instances.contains(testMessageBodyReader2));
        Assert.assertTrue(ERROR_MSG, instances.contains(testMessageBodyReader4));
        Assert.assertFalse(ERROR_MSG, instances.contains(testMessageBodyReader6));

        Assert.assertTrue(ERROR_MSG, config.isEnabled(testFeature2));
        Assert.assertTrue(ERROR_MSG, config.isEnabled(testFeature4));
        Assert.assertFalse(ERROR_MSG, config.isEnabled(testFeature6));

        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFeature2));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFeature4));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(testFeature6));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFilter2));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testFilter4));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(testFilter6));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testMessageBodyReader2));
        Assert.assertTrue(ERROR_MSG, config.isRegistered(testMessageBodyReader4));
        Assert.assertFalse(ERROR_MSG, config.isRegistered(testMessageBodyReader6));

        Assert.assertEquals(ERROR_MSG, 2, config.getProperties().size());
        Assert.assertEquals(ERROR_MSG, "value1", config.getProperty("property1"));
        Assert.assertEquals(ERROR_MSG, "value2", config.getProperty("property2"));

        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature1.class).isEmpty());
        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature2.class).isEmpty());
        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature3.class).isEmpty());
        Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature4.class).isEmpty());
        Assert.assertTrue(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature5.class).isEmpty());
        Assert.assertTrue(ERROR_MSG, config.getContracts(ConfigurationInheritenceTestFeature6.class).isEmpty());
    }
}