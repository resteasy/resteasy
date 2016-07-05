package org.jboss.resteasy.util;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.LoggableFailure;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WeightedLanguage implements Comparable<WeightedLanguage>
{
   private float weight = 1.0f;
   private String language;
   private Locale locale;
   private Map<String, String> params;

   public WeightedLanguage(Locale locale, float weight)
   {
      this.locale = locale;
      this.weight = weight;
   }

   private WeightedLanguage(String lang, Map<String, String> parameters)
   {
      this.language = lang;
      this.params = parameters;
      this.locale = LocaleHelper.extractLocale(lang);
      if (params != null)
      {
         String q = params.get("q");
         if (q != null)
         {
            weight = getQWithParamInfo(this, q);
         }
      }
   }

   public float getWeight()
   {
      return weight;
   }

   public Locale getLocale()
   {
      return locale;
   }

   public int compareTo(WeightedLanguage o)
   {
      WeightedLanguage type2 = this;
      WeightedLanguage type1 = o;

      if (type1.weight < type2.weight) return -1;
      if (type1.weight > type2.weight) return 1;

      return 0;
   }

   public String toString()
   {
      String rtn = language;
      if (params == null || params.size() == 0) return rtn;
      for (String name : params.keySet())
      {
         String val = params.get(name);
         rtn += ";" + name + "=\"" + val + "\"";
      }
      return rtn;
   }


   public static WeightedLanguage parse(String lang)
   {
      String params = null;
      int idx = lang.indexOf(";");
      if (idx > -1)
      {
         params = lang.substring(idx + 1).trim();
         lang = lang.substring(0, idx);
      }
      HashMap<String, String> typeParams = new HashMap<String, String>();
      if (params != null && !params.equals(""))
      {
         int start = 0;
         while (start < params.length())
         {
            start = HeaderParameterParser.setParam(typeParams, params, start);
         }
      }
      return new WeightedLanguage(lang, typeParams);
   }


   private static float getQWithParamInfo(WeightedLanguage lang, String val)
   {
      try
      {
         if (val != null)
         {
            float rtn = Float.valueOf(val);
            if (rtn > 1.0F)
               throw new LoggableFailure(Messages.MESSAGES.qValueCannotBeGreaterThan1(lang.toString()), HttpResponseCodes.SC_BAD_REQUEST);
            return rtn;
         }
      }
      catch (NumberFormatException e)
      {
         throw new LoggableFailure(Messages.MESSAGES.mediaTypeQWeightedLanguageMustBeFloat(lang), HttpResponseCodes.SC_BAD_REQUEST);
      }
      return 1.0f;
   }

   @Override
   public boolean equals(Object obj)
   {
      return super.equals(obj);
   }

}
