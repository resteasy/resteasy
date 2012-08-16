package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.NotImplementedYetException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Configuration;
import javax.ws.rs.core.Configurable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyClientFactory extends ClientFactory
{
   @Override
   protected Client getClient()
   {
      return new ResteasyClient();
   }

   @Override
   protected Client getClient(Configurable configuration)
   {
      throw new NotImplementedYetException();
   }
}
