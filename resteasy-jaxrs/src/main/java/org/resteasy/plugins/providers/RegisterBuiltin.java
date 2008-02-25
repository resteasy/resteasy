package org.resteasy.plugins.providers;

import org.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegisterBuiltin
{
   public static void register(ResteasyProviderFactory factory)
   {
      factory.addMessageBodyReader(new DefaultTextPlain());
      factory.addMessageBodyWriter(new DefaultTextPlain());


      factory.addMessageBodyReader(new JAXBProvider());
      factory.addMessageBodyWriter(new JAXBProvider());

      factory.addMessageBodyReader(new StringTextStar());
      factory.addMessageBodyWriter(new StringTextStar());

      factory.addMessageBodyReader(new InputStreamProvider());
      factory.addMessageBodyWriter(new InputStreamProvider());

      factory.addMessageBodyReader(new ByteArrayProvider());
      factory.addMessageBodyWriter(new ByteArrayProvider());
   }
}
