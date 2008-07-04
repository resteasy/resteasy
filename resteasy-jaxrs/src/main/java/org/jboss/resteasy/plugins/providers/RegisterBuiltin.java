package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.plugins.providers.json.jettison.JettisonProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegisterBuiltin
{
   public static void register(ResteasyProviderFactory factory)
   {
      //factory.addMessageBodyReader(new MultipartEntityProvider());

      IIOImageProvider imageProvider = new IIOImageProvider();
      factory.addMessageBodyReader(imageProvider);
      factory.addMessageBodyWriter(imageProvider);


      MimeMultipartProvider multipartProvider = new MimeMultipartProvider();
      factory.addMessageBodyReader(multipartProvider);
      factory.addMessageBodyWriter(multipartProvider);

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

      JettisonProvider jettison = new JettisonProvider();
      factory.addMessageBodyReader(jettison);
      factory.addMessageBodyWriter(jettison);

   }
}
