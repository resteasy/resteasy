package org.jboss.resteasy.plugins.providers;


import java.lang.reflect.Constructor;

import java.util.Collection;
import jakarta.ws.rs.ext.ParamConverter;

import org.jboss.resteasy.core.StringParameterInjector;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

/**
 * @author Marek Kopecky mkopecky@redhat.com
 * @author Ron Sigal rsigal@redhat.com
 */
public class MultiValuedCollectionParamConverter extends MultiValuedAbstractParamConverter implements ParamConverter<Collection<?>>
{
   private Constructor<?> constructor;

   public MultiValuedCollectionParamConverter(final StringParameterInjector stringParameterInjector, final String separator, final Constructor<?> constructor)
   {
      super(stringParameterInjector, separator);
      this.constructor = constructor;
   }

   @Override
   public String toString(Collection<?> value)
   {
      if (value == null)
      {
         return null;
      }
      return stringify(value);
   }

   @Override
   public Collection<?> fromString(String param)
   {
      try
      {
         Collection<?> c = (Collection<?>) constructor.newInstance();
         return parse(c, param.split(separator));
      }
      catch (Exception e)
      {
         throw new RuntimeException(Messages.MESSAGES.unableToParse(param));
      }
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private Collection<?> parse(Collection c, String[] params) throws Exception
   {
      for (String param : params)
      {
         c.add(stringParameterInjector.extractValue(param));
      }
      return c;
   }
}
