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
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

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

   @Rule
   public FakeHttpServer fakeHttpServer = new FakeHttpServer(FakeHttpServer::dummyMethods);

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
      Assert.assertEquals("Wrong RuntimeType in ClientBuilder", RuntimeType.CLIENT, clientBuilder.getConfiguration().getRuntimeType());
      Client client = clientBuilder.build();
      Assert.assertEquals("Wrong RuntimeType in Client", RuntimeType.CLIENT, client.getConfiguration().getRuntimeType());
      WebTarget target = client.target("http://localhost:8081");
      Assert.assertEquals("Wrong RuntimeType in WebTarget", RuntimeType.CLIENT, target.getConfiguration().getRuntimeType());
   }

   @Test
   public void testClientRequestFilterInheritance()
   {
      Client client = ClientBuilder.newClient();
      try
      {
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
   public void testClientResponseFilterInheritance()
   {
      Client client = ClientBuilder.newClient();
      try
      {
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
   public void testReaderInterceptorInheritance()
   {
      Client client = ClientBuilder.newClient();
      try
      {
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
   public void testWriterInterceptorInheritance()
   {
      Client client = ClientBuilder.newClient();
      try
      {
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
   public void testMessageBodyReaderInheritance()
   {
      Client client = ClientBuilder.newClient();
      try
      {
         fakeHttpServer.start();

         WebTarget parentWebTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
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
   public void testMessageBodyWriterInterceptorInheritance()
   {
      Client client = ClientBuilder.newClient();
      try
      {
         fakeHttpServer.start();

         WebTarget parentWebTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
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
   public void testContextResolverInheritance()
   {
      Client client = ClientBuilder.newClient();
      try
      {
         fakeHttpServer.start();

         WebTarget parentWebTarget = client.target("http://" + fakeHttpServer.getHostAndPort());
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
      Assert.assertTrue(ERROR_MSG, classes.contains(ConfigurationInheritanceTestFeature1.class));
      Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritanceTestFeature3.class));
      Assert.assertTrue(ERROR_MSG, classes.contains(ConfigurationInheritanceTestFeature5.class));
      Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritanceTestFilter3.class));
      Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritanceTestFilter4.class));
      Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritanceTestMessageBodyReader3.class));
      Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritanceTestMessageBodyReader4.class));

      Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritanceTestFeature1.class));
      Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritanceTestFeature2.class));
      Assert.assertFalse(ERROR_MSG, config.isEnabled(ConfigurationInheritanceTestFeature3.class));
      Assert.assertFalse(ERROR_MSG, config.isEnabled(ConfigurationInheritanceTestFeature4.class));
      Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritanceTestFeature5.class));
      Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritanceTestFeature6.class));

      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFeature1.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFeature2.class));
      Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFeature3.class));
      Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFeature4.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFeature5.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFeature6.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFilter1.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFilter2.class));
      Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFilter3.class));
      Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFilter4.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFilter5.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFilter6.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestMessageBodyReader1.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestMessageBodyReader2.class));
      Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestMessageBodyReader3.class));
      Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestMessageBodyReader4.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestMessageBodyReader5.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestMessageBodyReader6.class));

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

      Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritanceTestFeature1.class).isEmpty());
      Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritanceTestFeature2.class).isEmpty());
      Assert.assertTrue(ERROR_MSG, config.getContracts(ConfigurationInheritanceTestFeature3.class).isEmpty());
      Assert.assertTrue(ERROR_MSG, config.getContracts(ConfigurationInheritanceTestFeature4.class).isEmpty());
      Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritanceTestFeature5.class).isEmpty());
      Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritanceTestFeature6.class).isEmpty());
   }

   private void checkSecondConfiguration(Configuration config) {
      Set<Class<?>> classes = config.getClasses();
      Assert.assertTrue(ERROR_MSG, classes.contains(ConfigurationInheritanceTestFeature1.class));
      Assert.assertTrue(ERROR_MSG, classes.contains(ConfigurationInheritanceTestFeature3.class));
      Assert.assertFalse(ERROR_MSG, classes.contains(ConfigurationInheritanceTestFeature5.class));

      Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritanceTestFeature1.class));
      Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritanceTestFeature2.class));
      Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritanceTestFeature3.class));
      Assert.assertTrue(ERROR_MSG, config.isEnabled(ConfigurationInheritanceTestFeature4.class));
      Assert.assertFalse(ERROR_MSG, config.isEnabled(ConfigurationInheritanceTestFeature5.class));
      Assert.assertFalse(ERROR_MSG, config.isEnabled(ConfigurationInheritanceTestFeature6.class));

      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFeature1.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFeature2.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFeature3.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFeature4.class));
      Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFeature5.class));
      Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFeature6.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFilter1.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFilter2.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFilter3.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFilter4.class));
      Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFilter5.class));
      Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestFilter6.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestMessageBodyReader1.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestMessageBodyReader2.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestMessageBodyReader3.class));
      Assert.assertTrue(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestMessageBodyReader4.class));
      Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestMessageBodyReader5.class));
      Assert.assertFalse(ERROR_MSG, config.isRegistered(ConfigurationInheritanceTestMessageBodyReader6.class));

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

      Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritanceTestFeature1.class).isEmpty());
      Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritanceTestFeature2.class).isEmpty());
      Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritanceTestFeature3.class).isEmpty());
      Assert.assertFalse(ERROR_MSG, config.getContracts(ConfigurationInheritanceTestFeature4.class).isEmpty());
      Assert.assertTrue(ERROR_MSG, config.getContracts(ConfigurationInheritanceTestFeature5.class).isEmpty());
      Assert.assertTrue(ERROR_MSG, config.getContracts(ConfigurationInheritanceTestFeature6.class).isEmpty());
   }
}
