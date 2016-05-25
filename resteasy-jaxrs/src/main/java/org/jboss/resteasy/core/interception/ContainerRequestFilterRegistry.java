package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContainerRequestFilterRegistry extends JaxrsInterceptorRegistry<ContainerRequestFilter>
{
   protected LegacyPrecedence precedence;

   public ContainerRequestFilterRegistry(ResteasyProviderFactory providerFactory, LegacyPrecedence precedence)
   {
      super(providerFactory, ContainerRequestFilter.class);
      this.precedence = precedence;
   }

   public ContainerRequestFilterRegistry clone(ResteasyProviderFactory factory)
   {
      ContainerRequestFilterRegistry clone = new ContainerRequestFilterRegistry(factory, precedence);
      clone.interceptors.addAll(interceptors);
      return clone;
   }

   private static class ContainerRequestFilterFacade implements ContainerRequestFilter
   {
      protected final PreProcessInterceptor interceptor;

      private ContainerRequestFilterFacade(PreProcessInterceptor interceptor)
      {
         this.interceptor = interceptor;
      }

      @Override
      public void filter(ContainerRequestContext requestContext) throws IOException
      {
         PostMatchContainerRequestContext ctx = (PostMatchContainerRequestContext)requestContext;
         ServerResponse response = interceptor.preProcess(ctx.getHttpRequest(), ctx.getResourceMethod());
         if (response != null)
         {
            requestContext.abortWith(response);
         }
      }
   }

   @Override
   public ContainerRequestFilter[] postMatch(Class declaring, AccessibleObject target)
   {
      return super.postMatch(declaring, target);
   }

   public void registerLegacy(Class<? extends PreProcessInterceptor> decl)
   {
      register(new LegacyPerMethodInterceptorFactory(decl, precedence)
      {
         @Override
         public Match postMatch(Class declaring, AccessibleObject target)
         {
            Object obj = super.getLegacyMatch(declaring, target);
            if (obj == null) return null;
            PreProcessInterceptor interceptor = (PreProcessInterceptor)obj;
            return new Match(new ContainerRequestFilterFacade(interceptor), order);
         }

      });
   }

   public void registerLegacy(PreProcessInterceptor interceptor)
   {
      register(new LegacySingletonInterceptorFactory(interceptor.getClass(), interceptor, precedence)
      {
         @Override
         public Match postMatch(Class declaring, AccessibleObject target)
         {
            Object obj = super.getLegacyMatch(declaring, target);
            if (obj == null) return null;
            PreProcessInterceptor interceptor = (PreProcessInterceptor)obj;
            return new Match(new ContainerRequestFilterFacade(interceptor), order);
         }
      });

   }
}
