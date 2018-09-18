package org.resteasy.reactivecontext;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.server.servlet.Cleanables;

import io.reactiverse.reactivecontexts.core.Context;

@Priority(-1000)
@PreMatching
@Provider
public class ContextFilter implements ContainerRequestFilter
{

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      Context context = ResteasyContext.getContextData(Context.class);
      Cleanables cleanables = ResteasyContext.getContextData(Cleanables.class);
      Context.setThreadInstance(context);
      cleanables.addCleanable(() -> Context.clearThreadInstance());
   }

}
