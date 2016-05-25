package org.jboss.resteasy.cdi.extension.scope;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 11, 2012
 */
public class PlannedObsolescenceContext implements Context
{
   private HashMap<Contextual<?>, Wrapper<?>> map = new HashMap<Contextual<?>, Wrapper<?>>();

   @Override
   public Class<? extends Annotation> getScope()
   {
      return PlannedObsolescenceScope.class;
   }

   @Override
   public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext)
   {
      @SuppressWarnings("unchecked")
      Wrapper<T> w = (Wrapper<T>) map.get(contextual);
      if (w != null)
      {
         T o = w.object;
         Class<?> c = o.getClass();
         PlannedObsolescenceScope scope = c.getAnnotation(PlannedObsolescenceScope.class);
         if (scope == null)
         {
            throw new RuntimeException("Error");
         }
         if (++w.counter > scope.value())
         {
            contextual.destroy(o, creationalContext);
            creationalContext.release();
            o = contextual.create(creationalContext);
            map.put(contextual, new Wrapper<T>(creationalContext, o));
         }
         return o;
      }
      else
      {
         T o = contextual.create(creationalContext);
         map.put(contextual, new Wrapper<T>(creationalContext, o));
         return o;
      }
   }

   /**
    * Technically, this version of get() should not create a new object, but,
    * for purposes of the test, we destroy and recreate an object when it is
    * obsolete.
    */
   @Override
   public <T> T get(Contextual<T> contextual)
   {
      @SuppressWarnings("unchecked")
      Wrapper<T> w = (Wrapper<T>) map.get(contextual);
      if (w == null)
      {
         return null;
      }
      
      T o = w.object;
      Class<?> c = o.getClass();
      PlannedObsolescenceScope scope = c.getAnnotation(PlannedObsolescenceScope.class);
      if (scope == null)
      {
         throw new RuntimeException("Error");
      }
      CreationalContext<T> creationalContext = w.creationalContext;
      if (++w.counter > scope.value())
      {
         contextual.destroy(o, creationalContext);
         creationalContext.release();
         map.remove(contextual);
//         o = contextual.create(creationalContext);
//         map.put(contextual, new Wrapper<T>(creationalContext, o));
         o = null;
      }
      return o;
   }

   @Override
   public boolean isActive()
   {
      // TODO How should this be determined?
      return true;
   }
   
   static class Wrapper<T>
   {
      int counter;
      CreationalContext<T> creationalContext;
      T object;
      
      Wrapper(CreationalContext<T> creationalContext, T object)
      {
         this.creationalContext = creationalContext;
         this.object = object;
         counter = 1;
      }
   }
}

