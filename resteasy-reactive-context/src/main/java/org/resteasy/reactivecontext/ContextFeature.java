package org.resteasy.reactivecontext;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import io.reactiverse.reactivecontexts.core.Context;

@Provider
public class ContextFeature implements Feature
{

   @Override
   public boolean configure(FeatureContext context)
   {
      // this is tied to the deployment, which is what we want for the reactive context
      if(context.getConfiguration().getRuntimeType() == RuntimeType.CLIENT)
         return false;
      Dispatcher dispatcher = ResteasyProviderFactory.getContextData(Dispatcher.class);
      if(dispatcher == null) {
         System.err.println("Creating new context for "+context.getConfiguration().getRuntimeType()+" deployment because it looks like we're in a special thread");
         return false;
      }
      dispatcher.getDefaultContextObjects().put(Context.class, new Context());
      return true;
   }

}
