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
      factory.addMessageBodyReader(new DefaultPlainText());
      factory.addMessageBodyWriter(new DefaultPlainText());


      factory.addMessageBodyReader(new JAXBProvider());
      factory.addMessageBodyWriter(new JAXBProvider());

      factory.addMessageBodyReader(new DefaultFromString());
      factory.addMessageBodyWriter(new DefaultFromString());
   }
}
