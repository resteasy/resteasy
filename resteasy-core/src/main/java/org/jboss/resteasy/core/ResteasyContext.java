package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.util.ThreadLocalStack;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public final class ResteasyContext
{
   public interface CloseableContext extends AutoCloseable {
      @Override
      void close();
   }

   private static final ThreadLocalStack<Map<Class<?>, Object>> contextualData = new ThreadLocalStack<Map<Class<?>, Object>>();
   private static final Map<String, Servlet> servletMap = new HashMap<String, Servlet>();
   private static final Map<Servlet, ServletContext> servletContextMap = new HashMap<Servlet, ServletContext>();

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
      Map<Class<?>, Object> contextDataMap = getContextDataMap(false);
      if (contextDataMap == null) {
         return null;
      }
      return (T) contextDataMap.get(type);
   }

   /**
    * Gets the current context for the type. If not found in the current context a {@linkplain IllegalArgumentException}
    * is thrown.
    *
    * @param type the type to lookup in the context map
    * @param <T>  the type of the lookup
    *
    * @return the context data
    *
    * @throws IllegalArgumentException if the type is not found in the current context
    */
   public static <T> T getRequiredContextData(final Class<T> type) {
      final T result = getContextData(type);
      if (result == null) {
         throw Messages.MESSAGES.requiredContextParameterNotFound();
      }
      return result;
   }

   /**
    * Gets the current context for the type. If the context does not exist the value is resolved from the {@code newValue}
    * supplier and pushed to the current context.
    *
    * @param type     the type to lookup in the context map
    * @param newValue the new value if the value was not already setup in the value map
    * @param <T>      the type to lookup
    *
    * @return the context data
    */
   public static <T> T computeIfAbsent(final Class<T> type, final Supplier<T> newValue) {
      return (T) getContextDataMap().computeIfAbsent(type, (value) -> newValue.get());
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

   public static CloseableContext addCloseableContextDataLevel(Map<Class<?>,Object> data)
   {
      pushContextDataMap(data);
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

   public static void addServlet(String name, Servlet servlet, ServletContext servletContext)
   {
      servletMap.put(name, servlet);
      servletContextMap.put(servlet, servletContext);
   }

   public static void removeServlet(String name)
   {
      servletContextMap.remove(servletMap.get(name));
      servletMap.remove(name);
   }

   public static Servlet getServlet(String name)
   {
      return servletMap.get(name);
   }

   public static ServletContext getServletContext(Servlet servlet)
   {
      return servletContextMap.get(servlet);
   }
}

