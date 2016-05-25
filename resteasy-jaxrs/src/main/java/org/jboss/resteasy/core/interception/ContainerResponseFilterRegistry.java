package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.util.Collections;
import java.util.List;

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
         BuiltResponse jaxrsResposne = ctx.getJaxrsResponse();
         ServerResponse serverResponse = new ServerResponse(jaxrsResposne);
         try
         {
            interceptor.postProcess(serverResponse);
         }
         finally
         {
            jaxrsResposne.setStatus(serverResponse.getStatus());
            jaxrsResposne.setAnnotations(serverResponse.getAnnotations());
            jaxrsResposne.setEntity(serverResponse.getEntity());
            jaxrsResposne.setMetadata(serverResponse.getMetadata());
            jaxrsResposne.setEntityClass(serverResponse.getEntityClass());
            jaxrsResposne.setGenericType(serverResponse.getGenericType());
         }

      }
   }

   @Override
   protected void sort(List<Match> matches)
   {
      Collections.sort(matches, new DescendingPrecedenceComparator());

   }

   public ContainerResponseFilterRegistry clone(ResteasyProviderFactory factory)
   {
      ContainerResponseFilterRegistry clone = new ContainerResponseFilterRegistry(factory, precedence);
      clone.interceptors.addAll(interceptors);
      return clone;
   }

   @Override
   public synchronized void registerClass(Class<? extends ContainerResponseFilter> declaring)
   {
      OnDemandInterceptorFactory factory = new OnDemandInterceptorFactory(declaring);
      factory.setIgnorePrematch(true);
      register(factory);
   }

   @Override
   public synchronized void registerClass(Class<? extends ContainerResponseFilter> declaring, int priority)
   {
      OnDemandInterceptorFactory factory = new OnDemandInterceptorFactory(declaring);
      factory.setIgnorePrematch(true);
      factory.setOrder(priority);
      register(factory);
   }

   @Override
   public synchronized void registerSingleton(ContainerResponseFilter interceptor)
   {
      SingletonInterceptorFactory factory = new SingletonInterceptorFactory(interceptor.getClass(), interceptor);
      factory.setIgnorePrematch(true);
      register(factory);
   }

   @Override
   public synchronized void registerSingleton(ContainerResponseFilter interceptor, int priority)
   {
      SingletonInterceptorFactory factory = new SingletonInterceptorFactory(interceptor.getClass(), interceptor);
      factory.setIgnorePrematch(true);
      factory.setOrder(priority);
      register(factory);
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
