package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContainerResponseFilterRegistry extends JaxrsInterceptorRegistry<ContainerResponseFilter>
{
   protected LegacyPrecedence precedence;

   public ContainerResponseFilterRegistry(ResteasyProviderFactory providerFactory, LegacyPrecedence precedence)
   {
      super(providerFactory, ContainerResponseFilter.class);
      this.precedence = precedence;
   }

   private static class ContainerResponseFilterFacade implements ContainerResponseFilter
   {
      protected final PostProcessInterceptor interceptor;

      private ContainerResponseFilterFacade(PostProcessInterceptor interceptor)
      {
         this.interceptor = interceptor;
      }

      @Override
      public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
      {
         ContainerResponseContextImpl ctx = (ContainerResponseContextImpl)responseContext;
         interceptor.postProcess(ctx.getServerResponse());
      }
   }


   public void registerLegacy(Class<? extends PostProcessInterceptor> decl)
   {
      register(new LegacyPerMethodInterceptorFactory(decl, precedence)
      {
         @Override
         public Match postMatch(Class declaring, AccessibleObject target)
         {
            Object obj = getLegacyMatch(declaring, target);
            if (obj == null) return null;
            PostProcessInterceptor interceptor = (PostProcessInterceptor)obj;
            return new Match(new ContainerResponseFilterFacade(interceptor), order);
         }

      });
   }

   public void registerLegacy(PostProcessInterceptor interceptor)
   {
      register(new LegacySingletonInterceptorFactory(interceptor.getClass(), interceptor, precedence)
      {
         @Override
         public Match postMatch(Class declaring, AccessibleObject target)
         {
            Object obj = getLegacyMatch(declaring, target);
            if (obj == null) return null;
            PostProcessInterceptor interceptor = (PostProcessInterceptor)obj;
            return new Match(new ContainerResponseFilterFacade(interceptor), order);
         }
      });

   }
}
