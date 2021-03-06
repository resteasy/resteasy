package org.jboss.resteasy.core;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.ws.rs.BadRequestException;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.util.ThreadLocalStack;

@SuppressWarnings("unchecked")
public final class ResteasyContext
{
   public interface CloseableContext extends AutoCloseable {
      @Override
      void close();
   }

   private static final ThreadLocalStack<Map<Class<?>, Object>> contextualData = new ThreadLocalStack<Map<Class<?>, Object>>();

   private static final int maxForwards = 20;

   public static <T> void pushContext(Class<T> type, T data)
   {
      getContextDataMap().put(type, data);
   }

   public static void pushContextDataMap(Map<Class<?>, Object> map)
   {
      contextualData.push(map);
   }

   public static Map<Class<?>, Object> getContextDataMap()
   {
      return getContextDataMap(true);
   }

   public static <T> T getContextData(Class<T> type)
   {
      return (T) getContextDataMap().get(type);
   }

   public static <T> T popContextData(Class<T> type)
   {
      return (T) getContextDataMap().remove(type);
   }

   public static void clearContextData()
   {
      contextualData.clear();
   }

   public static Map<Class<?>, Object> getContextDataMap(boolean create)
   {
      Map<Class<?>, Object> map = contextualData.get();
      if (map == null && create)
      {
         contextualData.setLast(map = new HashMap<Class<?>, Object>());
      }
      return map;
   }

   public static Map<Class<?>, Object> addContextDataLevel()
   {
      if (getContextDataLevelCount() == maxForwards)
      {
         throw new BadRequestException(
               Messages.MESSAGES.excededMaximumForwards(getContextData(UriInfo.class).getPath()));
      }
      Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
      contextualData.push(map);
      return map;
   }

   public static CloseableContext addCloseableContextDataLevel()
   {
      addContextDataLevel();
      return () -> removeContextDataLevel();
   }

   public static int getContextDataLevelCount()
   {
      return contextualData.size();
   }

   public static void removeContextDataLevel()
   {
      contextualData.pop();
   }

   public static Object searchContextData(Object o)
   {
      for (int i = contextualData.size() - 1; i >= 0; i--)
      {
         Map<Class<?>, Object> map = contextualData.get(i);
         if (map.containsKey(o))
         {
            return map.get(o);
         }
      }
      return null;
   }
}
