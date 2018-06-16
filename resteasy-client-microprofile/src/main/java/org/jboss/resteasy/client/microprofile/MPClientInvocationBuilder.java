package org.jboss.resteasy.client.microprofile;

import java.net.URI;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientRequestHeaders;

/**
 * An extension of ClientInvocationBuilder for implementing MP REST Client
 * 
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 *
 */
public class MPClientInvocationBuilder extends ClientInvocationBuilder
{

   public MPClientInvocationBuilder(ResteasyClient client, URI uri, ClientConfiguration configuration)
   {
      super(client, uri, configuration);
   }
   
   @Override
   protected ClientInvocation createClientInvocation(ResteasyClient client, URI uri, ClientRequestHeaders headers, ClientConfiguration parent)
   {
      return new MPClientInvocation(client, uri, headers, parent);
   }

   @Override
   protected ClientInvocation createClientInvocation(ClientInvocation invocation)
   {
      return new MPClientInvocation(invocation);
   }
}
