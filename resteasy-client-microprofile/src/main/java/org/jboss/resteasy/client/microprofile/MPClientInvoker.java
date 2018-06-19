package org.jboss.resteasy.client.microprofile;

import java.lang.reflect.Method;

import javax.ws.rs.client.WebTarget;

import org.jboss.resteasy.client.jaxrs.ProxyConfig;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientRequestHeaders;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ClientInvoker;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.InvocationProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.WebTargetProcessor;

public class MPClientInvoker extends ClientInvoker
{

   public MPClientInvoker(ResteasyWebTarget parent, Class<?> declaring, Method method, ProxyConfig config)
   {
      super(parent, declaring, method, config);
   }

   protected ClientInvocation createRequest(Object[] args)
   {
      WebTarget target = this.webTarget;
      for (int i = 0; i < processors.length; i++)
      {
         if (processors != null && processors[i] instanceof WebTargetProcessor)
         {
            WebTargetProcessor processor = (WebTargetProcessor)processors[i];
            target = processor.build(target, args[i]);

         }
      }

      ClientConfiguration parentConfiguration=(ClientConfiguration) target.getConfiguration();
      ClientInvocation clientInvocation = new MPClientInvocation(this.webTarget.getResteasyClient(), target.getUri(), //TODO improve!!!!
              new ClientRequestHeaders(parentConfiguration), parentConfiguration);
      clientInvocation.setClientInvoker(this);
      if (accepts != null)
      {
         clientInvocation.getHeaders().accept(accepts);
      }
      for (int i = 0; i < processors.length; i++)
      {
         if (processors != null && processors[i] instanceof InvocationProcessor)
         {
            InvocationProcessor processor = (InvocationProcessor)processors[i];
            processor.process(clientInvocation, args[i]);

         }
      }
      clientInvocation.setMethod(httpMethod);
      return clientInvocation;
   }
}
