package org.jboss.resteasy.plugins.guice.ext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.guice.RequestScoped;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * Binds the {@link RequestScoped} to the current HTTP request and
 * makes all the classes available via the {@link Context} annotation injectable.
 */
public class RequestScopeModule extends AbstractModule
{
   @Override
   protected void configure()
   {
      bindScope(RequestScoped.class, new Scope()
      {
         @Override
         public <T> Provider<T> scope(final Key<T> key, final Provider<T> creator)
         {
            return new Provider<T>()
            {
               @SuppressWarnings("unchecked")
               @Override
               public T get()
               {
                  Class<T> instanceClass = (Class<T>) key.getTypeLiteral().getType();
                  T instance = ResteasyContext.getContextData(instanceClass);

                  if (instance == null) {
                     instance = creator.get();
                     ResteasyContext.pushContext(instanceClass, instance);
                  }

                  return instance;
               }

               @Override
               public String toString() {
                  return String.format("%s[%s]", creator, super.toString());
               }
            };
         }
      });

      bind(HttpServletRequest.class).toProvider(new ResteasyContextProvider<HttpServletRequest>(HttpServletRequest.class)).in(RequestScoped.class);
      bind(HttpServletResponse.class).toProvider(new ResteasyContextProvider<HttpServletResponse>(HttpServletResponse.class)).in(RequestScoped.class);
      bind(Request.class).toProvider(new ResteasyContextProvider<Request>(Request.class)).in(RequestScoped.class);
      bind(HttpHeaders.class).toProvider(new ResteasyContextProvider<HttpHeaders>(HttpHeaders.class)).in(RequestScoped.class);
      bind(UriInfo.class).toProvider(new ResteasyContextProvider<UriInfo>(UriInfo.class)).in(RequestScoped.class);
      bind(SecurityContext.class).toProvider(new ResteasyContextProvider<SecurityContext>(SecurityContext.class)).in(RequestScoped.class);
//      bind(ServletConfig.class).toProvider(new ResteasyContextProvider<ServletConfig>(ServletConfig.class)).in(Singleton.class);
//      bind(ServletContext.class).toProvider(new ResteasyContextProvider<ServletContext>(ServletContext.class)).in(Singleton.class);
   }

   private static class ResteasyContextProvider<T> implements Provider<T> {

      private final Class<T> instanceClass;

      ResteasyContextProvider(final Class<T> instanceClass)
      {
         this.instanceClass = instanceClass;
      }

      @Override
      public T get() {
         return ResteasyContext.getContextData(instanceClass);
      }
   }
}
