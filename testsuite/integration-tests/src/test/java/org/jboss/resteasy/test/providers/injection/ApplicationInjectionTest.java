package org.jboss.resteasy.test.providers.injection;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionApplication1;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionApplication2;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionApplicationParent;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionAsyncResponseProvider;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionBodyReader;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionBodyWriter;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionContainerRequestFilter;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionContainerResponseFilter;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionContextResolver;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionDynamicFeature;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionDynamicFeatureFilter;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionException;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionExceptionMapper;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionFeature;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionFeatureFilter;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionParamConverterProvider;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionReaderInterceptor;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionResource;
import org.jboss.resteasy.test.providers.injection.resource.ApplicationInjectionWriterInterceptor;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Providers
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test injection of Application proxies
 * @tpSince RESTEasy 4.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ApplicationInjectionTest {

   static ResteasyClient client;

   @Deployment(name="CDI_ON")
   public static Archive<?> createTestArchiveCdiOn() {
      WebArchive war = ShrinkWrap.create(WebArchive.class, ApplicationInjectionTest.class.getSimpleName() + "_cdi_on.war");
      war.addClass(ApplicationInjectionResource.class);
      war.addClass(ApplicationInjectionApplicationParent.class);
      war.addClass(ApplicationInjectionApplication1.class);
      war.addClass(ApplicationInjectionApplication2.class);
      war.addClass(ApplicationInjectionContainerRequestFilter.class);
      war.addClass(ApplicationInjectionContainerResponseFilter.class);
      war.addClass(ApplicationInjectionReaderInterceptor.class);
      war.addClass(ApplicationInjectionWriterInterceptor.class);
      war.addClass(ApplicationInjectionBodyReader.class);
      war.addClass(ApplicationInjectionBodyWriter.class);
      war.addClass(ApplicationInjectionParamConverterProvider.class);
      war.addClass(ApplicationInjectionException.class);
      war.addClass(ApplicationInjectionExceptionMapper.class);
      war.addClass(ApplicationInjectionContextResolver.class);
      war.addClass(ApplicationInjectionDynamicFeature.class);
      war.addClass(ApplicationInjectionDynamicFeatureFilter.class);
      war.addClass(ApplicationInjectionFeature.class);
      war.addClass(ApplicationInjectionFeatureFilter.class);
      war.addClass(ApplicationInjectionAsyncResponseProvider.class);
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      return war;
   }

   @Deployment(name="CDI_OFF")
   public static Archive<?> createTestArchiveCdiOff() {
      WebArchive war = ShrinkWrap.create(WebArchive.class, ApplicationInjectionTest.class.getSimpleName() + "_cdi_off.war");
      war.addClass(ApplicationInjectionResource.class);
      war.addClass(ApplicationInjectionApplicationParent.class);
      war.addClass(ApplicationInjectionApplication1.class);
      war.addClass(ApplicationInjectionApplication2.class);
      war.addClass(ApplicationInjectionContainerRequestFilter.class);
      war.addClass(ApplicationInjectionContainerResponseFilter.class);
      war.addClass(ApplicationInjectionReaderInterceptor.class);
      war.addClass(ApplicationInjectionWriterInterceptor.class);
      war.addClass(ApplicationInjectionBodyReader.class);
      war.addClass(ApplicationInjectionBodyWriter.class);
      war.addClass(ApplicationInjectionParamConverterProvider.class);
      war.addClass(ApplicationInjectionException.class);
      war.addClass(ApplicationInjectionExceptionMapper.class);
      war.addClass(ApplicationInjectionContextResolver.class);
      war.addClass(ApplicationInjectionDynamicFeature.class);
      war.addClass(ApplicationInjectionDynamicFeatureFilter.class);
      war.addClass(ApplicationInjectionFeature.class);
      war.addClass(ApplicationInjectionFeatureFilter.class);
      war.addClass(ApplicationInjectionAsyncResponseProvider.class);
      war.addAsWebInfResource(ApplicationInjectionTest.class.getPackage(), "beans.xml", "beans.xml");
      return war;
   }

   @Before
   public void init() {
      client = new ResteasyClientBuilder().build();
   }

   @After
   public void after() throws Exception {
      client.close();
   }

   private String generateURL(String path, String extension) {
      return PortProviderUtil.generateURL(path, ApplicationInjectionTest.class.getSimpleName() + "_cdi_" + extension);
   }

   /**
    * @tpTestDetails Test with CDI enabled.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testCdiOn() throws Exception {
      doTestResourceMethodSucceeds("on");
      doTestExceptionMapper("on");
      doTestAsync("on");
   }
   
   /**
    * @tpTestDetails Test with CDI disabled.
    * @tpSince RESTEasy 4.0.0
    */
   @Test
   public void testCdiOff() throws Exception {
      doTestResourceMethodSucceeds("off");
      doTestExceptionMapper("off");
      doTestAsync("off");
   }

   void doTestResourceMethodSucceeds(String cdi) throws Exception {
      Class<?>[] classes = new Class<?>[] {
         ApplicationInjectionBodyReader.class,
         ApplicationInjectionBodyWriter.class,
         ApplicationInjectionContainerRequestFilter.class,
         ApplicationInjectionContainerResponseFilter.class,
         ApplicationInjectionParamConverterProvider.ApplicationInjectionParamConverter.class,
         ApplicationInjectionReaderInterceptor.class,
         ApplicationInjectionWriterInterceptor.class,
         ApplicationInjectionContextResolver.class,
         ApplicationInjectionResource.class,
         ApplicationInjectionDynamicFeatureFilter.class,
         ApplicationInjectionFeatureFilter.class
      };
      {
         Response response = client.target(generateURL("/app1/test/param", cdi)).request().post(Entity.entity("app1", MediaType.TEXT_PLAIN));
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         String s = response.readEntity(String.class);
         Assert.assertEquals(classes.length, count("ApplicationInjectionApplication1", s));
         verifyClasses(classes, s);
      }
      {
         Response response = client.target(generateURL("/app2/test/param", cdi)).request().post(Entity.entity("app2", MediaType.TEXT_PLAIN));
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         String s = response.readEntity(String.class);
         Assert.assertEquals(classes.length, count("ApplicationInjectionApplication2", s));
         verifyClasses(classes, s);
      }
   }

   void doTestExceptionMapper(String cdi) {
      Class<?>[] classes = new Class<?>[] {
         ApplicationInjectionBodyReader.class,
         ApplicationInjectionBodyWriter.class,
         ApplicationInjectionContainerRequestFilter.class,
         ApplicationInjectionContainerResponseFilter.class,
         ApplicationInjectionExceptionMapper.class,
         ApplicationInjectionParamConverterProvider.ApplicationInjectionParamConverter.class,
         ApplicationInjectionReaderInterceptor.class,
         ApplicationInjectionWriterInterceptor.class,
         ApplicationInjectionContextResolver.class,
         ApplicationInjectionResource.class,
         ApplicationInjectionDynamicFeatureFilter.class,
         ApplicationInjectionFeatureFilter.class
      };
      {
         Response response = client.target(generateURL("/app1/exception/param", cdi)).request().post(Entity.entity("app1", MediaType.TEXT_PLAIN));
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         String s = response.readEntity(String.class);
         Assert.assertEquals(classes.length, count("ApplicationInjectionApplication1", s));
         verifyClasses(classes, s);
      }
      {
         Response response = client.target(generateURL("/app2/exception/param", cdi)).request().post(Entity.entity("app2", MediaType.TEXT_PLAIN));
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         String s = response.readEntity(String.class);
         Assert.assertEquals(classes.length, count("ApplicationInjectionApplication2", s));
         verifyClasses(classes, s);
      }
   }
   
   void doTestAsync(String cdi) {
      Class<?>[] classes = new Class<?>[] {
         ApplicationInjectionBodyReader.class,
         ApplicationInjectionBodyWriter.class,
         ApplicationInjectionContainerRequestFilter.class,
         ApplicationInjectionContainerResponseFilter.class,
         ApplicationInjectionParamConverterProvider.ApplicationInjectionParamConverter.class,
         ApplicationInjectionReaderInterceptor.class,
         ApplicationInjectionWriterInterceptor.class,
         ApplicationInjectionContextResolver.class,
         ApplicationInjectionResource.class,
         ApplicationInjectionDynamicFeatureFilter.class,
         ApplicationInjectionFeatureFilter.class,
         ApplicationInjectionAsyncResponseProvider.class
      };
      {
         Response response = client.target(generateURL("/app1/async/param", cdi)).request().post(Entity.entity("app1", MediaType.TEXT_PLAIN));
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         String s = response.readEntity(String.class);
         Assert.assertEquals(classes.length, count("ApplicationInjectionApplication1", s));
         verifyClasses(classes, s);
      }
      {
         Response response = client.target(generateURL("/app2/async/param", cdi)).request().post(Entity.entity("app2", MediaType.TEXT_PLAIN));
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         String s = response.readEntity(String.class);
         Assert.assertEquals(classes.length, count("ApplicationInjectionApplication2", s));
         verifyClasses(classes, s);
      }
   }

   private static int count(String s, String t) {
      int n = 0;
      while (t.contains(s)) {
         n++;
         t = t.substring(t.indexOf(s) + s.length());
      }
      return n;
   }

   private static boolean verifyClasses(Class<?>[] classes, String s) {
      for (Class<?> clazz : classes) {
         if (!s.contains(clazz.getName())) {
            return false;
         }
      }
      return true;
   }
}
