package org.jboss.resteasy.context;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

import org.eclipse.microprofile.context.spi.ContextManagerProvider;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.Dispatcher;

@Provider
public class ContextFeature implements Feature
{
   @Override
   public boolean configure(FeatureContext context)
   {
      // this is tied to the deployment, which is what we want for the reactive context
      if(context.getConfiguration().getRuntimeType() == RuntimeType.CLIENT)
         return false;
      Dispatcher dispatcher = ResteasyContext.getContextData(Dispatcher.class);
      if(dispatcher == null) {
         // this can happen, but it means we're not able to find a deployment
         return false;
      }
      // Make sure we have context propagation for this class loader
      ContextManagerProvider.instance().getContextManager();
      return true;
   }

}
