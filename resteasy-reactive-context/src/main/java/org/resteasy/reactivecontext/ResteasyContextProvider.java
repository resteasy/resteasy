package org.resteasy.reactivecontext;

import java.util.Map;

import org.jboss.resteasy.core.ResteasyContext;

import io.reactiverse.reactivecontexts.core.ContextProvider;

public class ResteasyContextProvider implements ContextProvider<Map<Class<?>, Object>>
{

   @Override
   public Map<Class<?>, Object> install(Map<Class<?>, Object> state)
   {
      ResteasyContext.pushContextDataMap(state);
      return null;
   }

   @Override
   public void restore(Map<Class<?>, Object> previousState)
   {
      ResteasyContext.removeContextDataLevel();
   }

   @Override
   public Map<Class<?>, Object> capture()
   {
      return ResteasyContext.getContextDataMap();
   }

}
