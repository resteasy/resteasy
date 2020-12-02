package org.jboss.resteasy.plugins.providers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.Separator;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.StringParameterInjector;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.util.Types;

/**
 * @author Marek Kopecky mkopecky@redhat.com
 * @author Ron Sigal rsigal@redhat.com
 */
@Provider
public class MultiValuedParamConverterProvider implements ParamConverterProvider
{
   private static final Pattern PROXY_REGEX = Pattern.compile("\\p{Punct}|\\[\\p{Punct}+\\]");

   @SuppressWarnings("unchecked")
   @Override
   public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations)
   {
      if (!isApplicable(annotations))
      {
         return null;
      }
      String separator = getSeparator(annotations);
      Class<?> paramType = getHeaderParam(annotations);
      if (Collection.class.isAssignableFrom(rawType))
      {
         if (!PROXY_REGEX.matcher(separator).matches())
         {
            LogMessages.LOGGER.invalidRegex(rawType.getName(), separator);
            return null;
         }
         Class<?> type = null;
         try
         {
            type = Types.getTypeArgument(genericType);
            if (type == null)
            {
               return null;
            }
         }
         catch (Exception e)
         {
            return null;
         }
         Constructor<?> constructor = getConstructor(rawType);
         if (constructor == null)
         {
            return null;
         }
         ResteasyProviderFactory factory = ResteasyContext.getContextData(ResteasyProviderFactory.class);
         StringParameterInjector stringParameterInjector = new StringParameterInjector(type, null, null, paramType, null, null, annotations, factory);
         return (ParamConverter<T>) new MultiValuedCollectionParamConverter(stringParameterInjector, separator, constructor);
      }
      else if (rawType.isArray())
      {
         Class<?> type = rawType.getComponentType();
         if (type.isArray() || Collection.class.isAssignableFrom(type))
         {
            return null;
         }
         ResteasyProviderFactory factory = ResteasyContext.getContextData(ResteasyProviderFactory.class);
         StringParameterInjector stringParameterInjector = new StringParameterInjector(type, null, null, paramType, null, null, annotations, factory);
         return (ParamConverter<T>) new MultiValuedArrayParamConverter(stringParameterInjector, separator, rawType);
      }
      return null;
   }

   //////////////////////////////////////////////////////////////////////////
   private boolean isApplicable(Annotation[] annotations)
   {
      if (annotations == null)
      {
         return false;
      }
      for (Annotation a : annotations)
      {
         if (a instanceof Separator)
         {
            return true;
         }
      }
      return false;
   }

   private Constructor<?> getConstructor(Class<?> clazz)
   {
      try
      {
         if (List.class.equals(clazz) || ArrayList.class.equals(clazz))
         {
            return ArrayList.class.getConstructor();
         }
         else if (SortedSet.class.equals(clazz) || TreeSet.class.equals(clazz))
         {
            return TreeSet.class.getConstructor();
         }
         else if (Set.class.equals(clazz) || HashSet.class.equals(clazz))
         {
            return HashSet.class.getConstructor();
         }
         else
         {
            return null;
         }
      }
      catch (NoSuchMethodException e)
      {
         return null;
      }
   }

   private String getSeparator(Annotation[] annotations)
   {
      for (Annotation a : annotations)
      {
         if (a instanceof Separator)
         {
            if ("".equals(((Separator) a).value()))
            {
               break;
            }
            return ((Separator) a).value();
         }
      }

      for (Annotation a : annotations)
      {
         if (a instanceof CookieParam)
         {
            return "-";
         }
      }
      return ",";
   }

   private Class<?> getHeaderParam(Annotation[] annotations)
   {
      if (annotations == null)
      {
         return null;
      }
      for (Annotation a : annotations)
      {
         if (a instanceof HeaderParam)
         {
            return HeaderParam.class;
         }
      }
      return null;
   }
}
