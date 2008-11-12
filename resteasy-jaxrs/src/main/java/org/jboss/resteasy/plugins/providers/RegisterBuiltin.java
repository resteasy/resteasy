package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.core.LoggerCategories;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegisterBuiltin
{

   private final static Logger logger = LoggerCategories.getProviderLogger();

   public static void register(ResteasyProviderFactory factory)
   {

      // Spec required providers.

      logger.debug("Registering standard providers");

      DataSourceProvider dataSourceProvider = new DataSourceProvider();
      factory.addMessageBodyReader(dataSourceProvider);
      factory.addMessageBodyWriter(dataSourceProvider);
      logger.info("Added {}", dataSourceProvider.getClass().getSimpleName());

      DefaultTextPlain plainText = new DefaultTextPlain();
      factory.addMessageBodyReader(plainText);
      factory.addMessageBodyWriter(plainText);
      logger.info("Added {}", plainText.getClass().getSimpleName());

      StringTextStar stringTextStar = new StringTextStar();
      factory.addMessageBodyReader(stringTextStar);
      factory.addMessageBodyWriter(stringTextStar);


      InputStreamProvider inputStreamProvider = new InputStreamProvider();
      factory.addMessageBodyReader(inputStreamProvider);
      factory.addMessageBodyWriter(inputStreamProvider);

      ByteArrayProvider byteArrayProvider = new ByteArrayProvider();
      factory.addMessageBodyReader(byteArrayProvider);
      factory.addMessageBodyWriter(byteArrayProvider);

      FormUrlEncodedProvider formProvider = new FormUrlEncodedProvider();
      factory.addMessageBodyReader(formProvider);
      factory.addMessageBodyWriter(formProvider);

      factory.addMessageBodyWriter(new StreamingOutputProvider());

      optionalProvider("org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlRootElementProvider", "org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlRootElementProvider", factory);
      optionalProvider("org.jboss.resteasy.plugins.providers.jaxb.JAXBElementProvider", "org.jboss.resteasy.plugins.providers.jaxb.JAXBElementProvider", factory);
      optionalProvider("org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlTypeProvider", "org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlTypeProvider", factory);
      optionalContextResolver("org.jboss.resteasy.plugins.providers.jaxb.XmlJAXBContextFinder", "org.jboss.resteasy.plugins.providers.jaxb.XmlJAXBContextFinder", factory);

      optionalReader("org.jboss.resteasy.plugins.providers.multipart.MultipartReader", "org.jboss.resteasy.plugins.providers.multipart.MultipartReader", factory);
      optionalReader("org.jboss.resteasy.plugins.providers.multipart.ListMultipartReader", "org.jboss.resteasy.plugins.providers.multipart.ListMultipartReader", factory);
      optionalReader("org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataReader", "org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataReader", factory);
      optionalReader("org.jboss.resteasy.plugins.providers.multipart.MapMultipartFormDataReader", "org.jboss.resteasy.plugins.providers.multipart.MapMultipartFormDataReader", factory);
      optionalWriter("org.jboss.resteasy.plugins.providers.multipart.MultipartWriter", "org.jboss.resteasy.plugins.providers.multipart.MultipartWriter", factory);
      optionalWriter("org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataWriter", "org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataWriter", factory);
      optionalWriter("org.jboss.resteasy.plugins.providers.multipart.ListMultipartWriter", "org.jboss.resteasy.plugins.providers.multipart.ListMultipartWriter", factory);
      optionalWriter("org.jboss.resteasy.plugins.providers.multipart.MapMultipartFormDataWriter", "org.jboss.resteasy.plugins.providers.multipart.MapMultipartFormDataWriter", factory);
      optionalReader("org.jboss.resteasy.plugins.providers.multipart.MultipartFormAnnotationReader", "org.jboss.resteasy.plugins.providers.multipart.MultipartFormAnnotationReader", factory);
      optionalWriter("org.jboss.resteasy.plugins.providers.multipart.MultipartFormAnnotationWriter", "org.jboss.resteasy.plugins.providers.multipart.MultipartFormAnnotationWriter", factory);

      // optional providers.
      optionalProvider("org.jboss.resteasy.plugins.providers.atom.AtomFeedProvider", "org.jboss.resteasy.plugins.providers.atom.AtomFeedProvider", factory);
      optionalProvider("org.jboss.resteasy.plugins.providers.atom.AtomEntryProvider", "org.jboss.resteasy.plugins.providers.atom.AtomEntryProvider", factory);

      optionalProvider("org.jboss.resteasy.plugins.providers.IIOImageProvider", "org.jboss.resteasy.plugins.providers.IIOImageProvider", factory);
      optionalContextResolver("org.jboss.resteasy.plugins.providers.jaxb.json.JsonJAXBContextFinder", "org.jboss.resteasy.plugins.providers.jaxb.json.JsonJAXBContextFinder", factory);
      optionalContextResolver("org.jboss.resteasy.plugins.providers.jaxb.fastinfoset.FastinfoSetJAXBContextFinder", "org.jboss.resteasy.plugins.providers.jaxb.fastinfoset.FastinfoSetJAXBContextFinder", factory);
      optionalProvider("org.jboss.resteasy.plugins.providers.multipart.MimeMultipartProvider", "org.jboss.resteasy.plugins.providers.multipart.MimeMultipartProvider", factory);
      optionalProvider("org.jboss.resteasy.plugins.providers.YamlProvider", "org.jboss.resteasy.plugins.providers.YamlProvider", factory);
   }

   private static void optionalProvider(String dependency, String providerClass, ResteasyProviderFactory factory)
   {
      if (isAvailable(dependency))
      {
         logger.info("Adding " + providerClass);
         Object provider = instantiate(providerClass);
         factory.addMessageBodyReader((MessageBodyReader<?>) provider);
         factory.addMessageBodyWriter((MessageBodyWriter<?>) provider);
      }

   }

   private static void optionalReader(String dependency, String providerClass, ResteasyProviderFactory factory)
   {
      if (isAvailable(dependency))
      {
         logger.info("Adding " + providerClass);
         Object provider = instantiate(providerClass);
         factory.addMessageBodyReader((MessageBodyReader<?>) provider);
      }

   }

   private static void optionalWriter(String dependency, String providerClass, ResteasyProviderFactory factory)
   {
      if (isAvailable(dependency))
      {
         logger.info("Adding " + providerClass);
         Object provider = instantiate(providerClass);
         factory.addMessageBodyWriter((MessageBodyWriter<?>) provider);
      }

   }

   private static void optionalContextResolver(String dependency, String providerClass, ResteasyProviderFactory factory)
   {
      if (isAvailable(dependency))
      {
         logger.info("Adding " + providerClass);
         factory.registerProviderInstance(instantiate(providerClass));
      }

   }

   private static boolean isAvailable(String className)
   {

      try
      {
         Thread.currentThread().getContextClassLoader().loadClass(className);
         return true;
      }
      catch (ClassNotFoundException cnfe)
      {
         return false;
      }

   }

   private static Object instantiate(String className)
   {
      try
      {
         Class<?> cl = Thread.currentThread().getContextClassLoader().loadClass(className);
         return cl.newInstance();
      }
      catch (Exception e)
      {
         logger.error("Failed to load: " + className, e);
         return null;
      }
   }

}
