package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.core.LoggerCategories;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBElementProvider;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlRootElementProvider;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlTypeProvider;
import org.jboss.resteasy.plugins.providers.jaxb.XmlJAXBContextFinder;
import org.jboss.resteasy.plugins.providers.multipart.ListMultipartReader;
import org.jboss.resteasy.plugins.providers.multipart.ListMultipartWriter;
import org.jboss.resteasy.plugins.providers.multipart.MapMultipartFormDataReader;
import org.jboss.resteasy.plugins.providers.multipart.MapMultipartFormDataWriter;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormAnnotationReader;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormAnnotationWriter;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataReader;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataWriter;
import org.jboss.resteasy.plugins.providers.multipart.MultipartReader;
import org.jboss.resteasy.plugins.providers.multipart.MultipartWriter;
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

      JAXBXmlRootElementProvider jaxb = new JAXBXmlRootElementProvider();
      factory.addMessageBodyReader(jaxb);
      factory.addMessageBodyWriter(jaxb);
      logger.info("Added {}", jaxb.getClass().getSimpleName());

      JAXBElementProvider elementProvider = new JAXBElementProvider();
      factory.addMessageBodyReader(elementProvider);
      factory.addMessageBodyWriter(elementProvider);
      logger.info("Added {}", elementProvider.getClass().getSimpleName());

      JAXBXmlTypeProvider xmlType = new JAXBXmlTypeProvider();
      factory.addMessageBodyReader(xmlType);
      factory.addMessageBodyWriter(xmlType);
      factory.addContextResolver(XmlJAXBContextFinder.class);
      logger.info("Added {}", xmlType.getClass().getSimpleName());

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

      factory.addMessageBodyReader(MultipartReader.class);
      factory.addMessageBodyReader(ListMultipartReader.class);
      factory.addMessageBodyReader(MultipartFormDataReader.class);
      factory.addMessageBodyReader(MapMultipartFormDataReader.class);
      factory.addMessageBodyWriter(MultipartWriter.class);
      factory.addMessageBodyWriter(MultipartFormDataWriter.class);
      factory.addMessageBodyWriter(ListMultipartWriter.class);
      factory.addMessageBodyWriter(MapMultipartFormDataWriter.class);
      factory.addMessageBodyReader(MultipartFormAnnotationReader.class);
      factory.addMessageBodyWriter(MultipartFormAnnotationWriter.class);

      // optional providers.
      optionalProvider("org.jboss.resteasy.plugins.providers.atom.AtomFeedProvider", "org.jboss.resteasy.plugins.providers.atom.AtomFeedProvider", factory);
      optionalProvider("org.jboss.resteasy.plugins.providers.atom.AtomEntryProvider", "org.jboss.resteasy.plugins.providers.atom.AtomEntryProvider", factory);
      optionalProvider("javax.imageio.IIOImage", "org.jboss.resteasy.plugins.providers.IIOImageProvider", factory);
      optionalContextResolver("org.codehaus.jettison.json.JSONObject", "org.jboss.resteasy.plugins.providers.jaxb.json.JsonJAXBContextFinder", factory);
      optionalContextResolver("com.sun.xml.fastinfoset.stax.StAXDocumentSerializer", "org.jboss.resteasy.plugins.providers.jaxb.fastinfoset.FastinfoSetJAXBContextFinder", factory);
      optionalProvider("javax.mail.internet.MimeMultipart", "org.jboss.resteasy.plugins.providers.MimeMultipartProvider", factory);
      optionalProvider("org.ho.yaml.Yaml", "org.jboss.resteasy.plugins.providers.YamlProvider", factory);
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
