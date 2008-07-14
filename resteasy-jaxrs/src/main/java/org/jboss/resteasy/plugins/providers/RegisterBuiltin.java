package org.jboss.resteasy.plugins.providers;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegisterBuiltin
{

   private final static Logger logger = LoggerFactory.getLogger(RegisterBuiltin.class);

   public static void register(ResteasyProviderFactory factory)
   {

      // Spec required providers.

      logger.debug("Registering standard providers");

      DataSourceProvider dataSourceProvider = new DataSourceProvider();
      factory.addMessageBodyReader(dataSourceProvider);
      factory.addMessageBodyWriter(dataSourceProvider);

      factory.addMessageBodyReader(new DefaultTextPlain());
      factory.addMessageBodyWriter(new DefaultTextPlain());

      JAXBProvider jaxb = new JAXBProvider();
      factory.addMessageBodyReader(jaxb);
      factory.addMessageBodyWriter(jaxb);

      factory.addMessageBodyReader(new StringTextStar());
      factory.addMessageBodyWriter(new StringTextStar());

      factory.addMessageBodyReader(new InputStreamProvider());
      factory.addMessageBodyWriter(new InputStreamProvider());

      factory.addMessageBodyReader(new ByteArrayProvider());
      factory.addMessageBodyWriter(new ByteArrayProvider());

      factory.addMessageBodyReader(new FormUrlEncodedProvider());
      factory.addMessageBodyWriter(new FormUrlEncodedProvider());

      factory.addMessageBodyWriter(new StreamingOutputProvider());

      // optional providers.

      if (isAvailable("javax.imageio.IIOImage"))
      {
         // javax.imageio is part of standard java, hence we
         // could really just add it. However anyone relying on this
         // provider would become jax-rs implementation dependent.
         logger.info("Adding IIOImageProvider");
         Object provider = instantiate("org.jboss.resteasy.plugins.providers.IIOImageProvider");
         factory.addMessageBodyReader((MessageBodyReader) provider);
         factory.addMessageBodyWriter((MessageBodyWriter) provider);
      }
         
      if (isAvailable("org.codehaus.jettison.json.JSONObject"))
      {
         logger.info("Adding JettisonProvider");
         Object provider = instantiate("org.jboss.resteasy.plugins.providers.json.jettison.JettisonProvider");
         factory.addMessageBodyReader((MessageBodyReader) provider);
         factory.addMessageBodyWriter((MessageBodyWriter) provider);
      }

      if (isAvailable("javax.mail.internet.MimeMultipart"))
      {
         logger.info("Adding MimeMultipartProvider");
         Object provider = instantiate("org.jboss.resteasy.plugins.providers.MimeMultipartProvider");
         factory.addMessageBodyReader((MessageBodyReader) provider);
         factory.addMessageBodyWriter((MessageBodyWriter) provider);
      }

      if (isAvailable("org.ho.yaml.Yaml"))
      {
         logger.info("Adding YamlProvider");
         Object provider = instantiate("org.jboss.resteasy.plugins.providers.YamlProvider");
         factory.addMessageBodyReader((MessageBodyReader) provider);
         factory.addMessageBodyWriter((MessageBodyWriter) provider);
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
