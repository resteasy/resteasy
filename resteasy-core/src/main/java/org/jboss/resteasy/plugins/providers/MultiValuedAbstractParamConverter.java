package org.jboss.resteasy.plugins.providers;


import java.util.Collection;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import org.jboss.resteasy.core.StringParameterInjector;

/**
 * @author Marek Kopecky mkopecky@redhat.com
 * @author Ron Sigal rsigal@redhat.com
 */
public abstract class MultiValuedAbstractParamConverter
{
   protected StringParameterInjector stringParameterInjector;
   @SuppressWarnings("rawtypes")
   protected ParamConverter paramConverter;
   @SuppressWarnings("rawtypes")
   protected HeaderDelegate headerDelegate;
   protected String separator;

   public MultiValuedAbstractParamConverter(final StringParameterInjector stringParameterInjector, final String separator)
   {
      this.stringParameterInjector = stringParameterInjector;
      this.paramConverter = stringParameterInjector.getParamConverter();
      this.headerDelegate = stringParameterInjector.getHeaderDelegate();
      this.separator = separator;
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////

   @SuppressWarnings({ "unchecked" })
   protected String stringify(Collection<?> value)
   {
      char separatorChar = getSeparatorChar();
      StringBuilder sb = new StringBuilder();
      boolean first = true;
      for (Object v : value)
      {
         if (first)
         {
            first = false;
         }
         else
         {
            sb.append(separatorChar);
         }
         if (v instanceof String)
         {
            sb.append(v);
         }
         else if (paramConverter != null)
         {
            sb.append(paramConverter.toString(v));
         }
         else if (headerDelegate != null)
         {
            sb.append(headerDelegate.toString(v));
         }
         else
         {
            sb.append(v.toString());
         }
      }
      return sb.toString();
   }

   protected char getSeparatorChar()
   {
      if (separator.charAt(0) == '[')
      {
         return separator.charAt(1);
      }
      else
      {
         return separator.charAt(0);
      }
   }
}
