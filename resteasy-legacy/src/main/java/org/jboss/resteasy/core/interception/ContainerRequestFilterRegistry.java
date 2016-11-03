package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.core.interception.jaxrs.PostMatchContainerRequestContext;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated Use org.jboss.resteasy.core.interception.jaxrs.ContainerRequestFilterRegistry instead.
 */
@Deprecated
public class ContainerRequestFilterRegistry extends org.jboss.resteasy.core.interception.jaxrs.ContainerRequestFilterRegistry
{
   protected LegacyPrecedence precedence;

   public ContainerRequestFilterRegistry(ResteasyProviderFactory providerFactory, LegacyPrecedence precedence)
   {
      super(providerFactory);
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
   
   public abstract class AbstractLegacyInterceptorFactory extends AbstractInterceptorFactory
   {
      protected LegacyPrecedence precedence;

      protected AbstractLegacyInterceptorFactory(Class declaring, LegacyPrecedence precedence)
      {
         super(declaring);
         this.precedence = precedence;
      }

      @Override
      protected void setPrecedence(Class<?> declaring)
      {
         order = precedence.calculateOrder(declaring);
      }

      @Override
      public Match preMatch()
      {
         return null;
      }

      public Object getLegacyMatch(Class declaring, AccessibleObject target)
      {
         Object interceptor = getInterceptor();
         if (interceptor instanceof AcceptedByMethod)
         {
            if (target == null || !(target instanceof Method)) return null;
            Method method = (Method) target;
            if (((AcceptedByMethod) interceptor).accept(declaring, method))
            {
               return interceptor;
            } else
            {
               return null;
            }
         }
         return interceptor;
      }

   }

   protected class LegacySingletonInterceptorFactory extends AbstractLegacyInterceptorFactory
   {
      protected Object interceptor;

      public LegacySingletonInterceptorFactory(Class declaring, Object interceptor, LegacyPrecedence precedence)
      {
         super(declaring, precedence);
         this.interceptor = interceptor;
         setPrecedence(declaring);
      }

      @Override
      protected void initialize()
      {
         providerFactory.injectProperties(interceptor);
      }

      @Override
      protected Object getInterceptor()
      {
         checkInitialize();
         return interceptor;
      }
   }

   protected class LegacyPerMethodInterceptorFactory extends AbstractLegacyInterceptorFactory
   {

      public LegacyPerMethodInterceptorFactory(Class declaring, LegacyPrecedence precedence)
      {
         super(declaring, precedence);
         setPrecedence(declaring);
      }

      @Override
      protected void initialize()
      {
      }

      @Override
      protected Object getInterceptor()
      {
         Object interceptor = createInterceptor();
         providerFactory.injectProperties(interceptor);
         return interceptor;
      }
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
