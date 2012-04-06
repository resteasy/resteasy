package org.jboss.resteasy.plugins.delegates;

import org.jboss.resteasy.util.LocaleHelper;

import javax.ws.rs.ext.RuntimeDelegate;
import java.util.Locale;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LocaleDelegate implements RuntimeDelegate.HeaderDelegate<Locale>
{
   public Locale fromString(String value) throws IllegalArgumentException
   {
      if (value == null) throw new IllegalArgumentException("Locale value is null");
      return LocaleHelper.extractLocale(value);
   }

   public String toString(Locale value)
   {
      return LocaleHelper.toLanguageString(value);
   }

}
