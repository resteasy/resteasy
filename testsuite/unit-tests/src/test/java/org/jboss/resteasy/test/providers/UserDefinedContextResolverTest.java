package org.jboss.resteasy.test.providers;

import java.lang.annotation.Annotation;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextFinder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * @tpSubChapter Providers
 * @tpChapter Unit tests
 * @tpTestCaseDetails Regression test for RESTEASY-1471
 * @tpSince RESTEasy 3.1.0.Final
 */
public class UserDefinedContextResolverTest {

   public static class TestException extends RuntimeException {
      private static final long serialVersionUID = 1L;
   }
   
   @Produces({"text/xml", "text/*+xml", "application/xml", "application/*+xml"})
   @SuppressWarnings("rawtypes")
   public static class TestContextFinder implements JAXBContextFinder {

      @Override
      public JAXBContext findCachedContext(Class type, MediaType mediaType, Annotation[] parameterAnnotations) throws JAXBException {
         return null;
      }
      @Override
      public JAXBContext findCacheContext(MediaType mediaType, Annotation[] paraAnnotations, Class... classes) throws JAXBException {
         return null;
      }
      @Override
      public JAXBContext findCacheXmlTypeContext(MediaType mediaType, Annotation[] paraAnnotations, Class... classes) throws JAXBException {
         return null;
      }
      @Override
      public JAXBContext createContext(Annotation[] parameterAnnotations, Class... classes) throws JAXBException {
         return null;
      }
   }
   
   @Produces({"text/xml", "text/*+xml", "application/xml", "application/*+xml"})
   public static class TestContextResolver implements ContextResolver<JAXBContextFinder> {

      @Override
      public JAXBContextFinder getContext(Class<?> type) {
         return new TestContextFinder();
      }
   }
   
   @Test
   public void testUserDefinedContextResolver() {

      ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();
      ContextResolver<JAXBContextFinder> finder1 = providerFactory.getContextResolver(JAXBContextFinder.class, MediaType.TEXT_XML_TYPE);
      Assert.assertNotNull(finder1);
      providerFactory.register(TestContextResolver.class);
      ContextResolver<JAXBContextFinder> finder2 = providerFactory.getContextResolver(JAXBContextFinder.class, MediaType.TEXT_XML_TYPE);
      JAXBContextFinder finder = finder2.getContext(JAXBContextFinder.class);
      Assert.assertTrue(finder instanceof TestContextFinder);
   }
}
