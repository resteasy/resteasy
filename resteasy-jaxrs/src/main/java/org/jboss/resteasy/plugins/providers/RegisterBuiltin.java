package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.core.LoggerCategories;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBElementProvider;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlRootElementProvider;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlTypeProvider;
import org.jboss.resteasy.plugins.providers.jaxb.XmlRootElementFastinfoSetProvider;
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
      logger.info("Added {}", xmlType.getClass().getSimpleName());

      XmlRootElementFastinfoSetProvider fast = new XmlRootElementFastinfoSetProvider();
      factory.addMessageBodyReader((MessageBodyReader<?>) fast);
      factory.addMessageBodyWriter((MessageBodyWriter<?>) fast);
      logger.info("Added {}", fast.getClass().getSimpleName());
      //      if (isAvailable("com.sun.xml.fastinfoset.stax.StAXDocumentSerializer"))
      //      {
      //         Object provider = 
      //            instantiate("org.jboss.resteasy.plugins.providers.jaxb.XmlRootElementFastinfoSetProvider");
      //         
      //         factory.addMessageBodyReader((MessageBodyReader<?>) provider);
      //         factory.addMessageBodyWriter((MessageBodyWriter<?>) provider);
      //      }

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

      // optional providers.

      if (isAvailable("javax.imageio.IIOImage"))
      {
         // javax.imageio is part of standard java, hence we
         // could really just add it. However anyone relying on this
         // provider would become jax-rs implementation dependent.
         logger.info("Adding IIOImageProvider");
         Object provider = instantiate("org.jboss.resteasy.plugins.providers.IIOImageProvider");
         factory.addMessageBodyReader((MessageBodyReader<?>) provider);
         factory.addMessageBodyWriter((MessageBodyWriter<?>) provider);
      }

      if (isAvailable("org.codehaus.jettison.json.JSONObject"))
      {
         logger.info("Adding JettisonProvider");
         Object provider = instantiate("org.jboss.resteasy.plugins.providers.json.jettison.JettisonProvider");
         factory.addMessageBodyReader((MessageBodyReader<?>) provider);
         factory.addMessageBodyWriter((MessageBodyWriter<?>) provider);
      }

      if (isAvailable("javax.mail.internet.MimeMultipart"))
      {
         logger.info("Adding MimeMultipartProvider");
         Object provider = instantiate("org.jboss.resteasy.plugins.providers.MimeMultipartProvider");
         factory.addMessageBodyReader((MessageBodyReader<?>) provider);
         factory.addMessageBodyWriter((MessageBodyWriter<?>) provider);
      }

      if (isAvailable("org.ho.yaml.Yaml"))
      {
         logger.info("Adding YamlProvider");
         Object provider = instantiate("org.jboss.resteasy.plugins.providers.YamlProvider");
         factory.addMessageBodyReader((MessageBodyReader<?>) provider);
         factory.addMessageBodyWriter((MessageBodyWriter<?>) provider);
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
