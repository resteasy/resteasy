package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextFinder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JaxbCacheTest extends BaseResourceTest
{
   @Test
   public void testCache() throws Exception
   {
      ResteasyProviderFactory.pushContext(Providers.class, getProviderFactory());
      {
         ContextResolver<JAXBContextFinder> resolver = getProviderFactory().getContextResolver(JAXBContextFinder.class, MediaType.APPLICATION_XML_TYPE);
         JAXBContextFinder finder = resolver.getContext(Child.class);
         JAXBContext ctx = finder.findCachedContext(Child.class, MediaType.APPLICATION_XML_TYPE, null);


         JAXBContext ctx2 = finder.findCachedContext(Child.class, MediaType.APPLICATION_XML_TYPE, null);

         Assert.assertTrue(ctx == ctx2);
      }

      {
         ContextResolver<JAXBContextFinder> resolver = getProviderFactory().getContextResolver(JAXBContextFinder.class, MediaType.APPLICATION_JSON_TYPE);
         JAXBContextFinder finder = resolver.getContext(Child.class);
         JAXBContext ctx = finder.findCachedContext(Child.class, MediaType.APPLICATION_JSON_TYPE, null);


         JAXBContext ctx2 = finder.findCachedContext(Child.class, MediaType.APPLICATION_JSON_TYPE, null);

         Assert.assertTrue(ctx == ctx2);
      }
      {
         MediaType mediaType = new MediaType("application", "fastinfoset");
         ContextResolver<JAXBContextFinder> resolver = getProviderFactory().getContextResolver(JAXBContextFinder.class, mediaType);
         JAXBContextFinder finder = resolver.getContext(Child.class);
         JAXBContext ctx = finder.findCachedContext(Child.class, mediaType, null);


         JAXBContext ctx2 = finder.findCachedContext(Child.class, mediaType, null);

         Assert.assertTrue(ctx == ctx2);
      }
   }

   @Test
   public void testCache2() throws Exception
   {
      ResteasyProviderFactory.pushContext(Providers.class, getProviderFactory());
      {
         ContextResolver<JAXBContextFinder> resolver = getProviderFactory().getContextResolver(JAXBContextFinder.class, MediaType.APPLICATION_XML_TYPE);
         JAXBContextFinder finder = resolver.getContext(Child.class);
         JAXBContext ctx = finder.findCacheContext(MediaType.APPLICATION_XML_TYPE, null, Child.class, Parent.class);


         JAXBContext ctx2 = finder.findCacheContext(MediaType.APPLICATION_XML_TYPE, null, Child.class, Parent.class);

         Assert.assertTrue(ctx == ctx2);
      }

      {
         ContextResolver<JAXBContextFinder> resolver = getProviderFactory().getContextResolver(JAXBContextFinder.class, MediaType.APPLICATION_JSON_TYPE);
         JAXBContextFinder finder = resolver.getContext(Child.class);
         JAXBContext ctx = finder.findCacheContext(MediaType.APPLICATION_JSON_TYPE, null, Child.class, Parent.class);


         JAXBContext ctx2 = finder.findCacheContext(MediaType.APPLICATION_JSON_TYPE, null, Child.class, Parent.class);

         Assert.assertTrue(ctx == ctx2);
      }
      {
         MediaType mediaType = new MediaType("application", "fastinfoset");
         ContextResolver<JAXBContextFinder> resolver = getProviderFactory().getContextResolver(JAXBContextFinder.class, mediaType);
         JAXBContextFinder finder = resolver.getContext(Child.class);
         JAXBContext ctx = finder.findCacheContext(mediaType, null, Child.class, Parent.class);


         JAXBContext ctx2 = finder.findCacheContext(mediaType, null, Child.class, Parent.class);

         Assert.assertTrue(ctx == ctx2);
      }
   }
}
