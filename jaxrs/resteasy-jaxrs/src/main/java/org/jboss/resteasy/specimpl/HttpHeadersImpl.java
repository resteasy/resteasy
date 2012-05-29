package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.util.LocaleHelper;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.WeightedLanguage;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpHeadersImpl implements HttpHeaders
{

   private MultivaluedMap<String, String> requestHeaders;
   private List<MediaType> acceptableMediaTypes;
   private MediaType mediaType;
   private Locale language;
   private Map<String, Cookie> cookies;
   private List<Locale> acceptableLanguages;

   public MultivaluedMap<String, String> getRequestHeaders()
   {
      return requestHeaders;
   }

   public void setRequestHeaders(MultivaluedMap<String, String> requestHeaders)
   {
      this.requestHeaders = requestHeaders;
   }

   public List<MediaType> getAcceptableMediaTypes()
   {
      return acceptableMediaTypes;
   }

   public void setAcceptableMediaTypes(List<MediaType> acceptableMediaTypes)
   {
      this.acceptableMediaTypes = acceptableMediaTypes;
      if (acceptableMediaTypes != null) MediaTypeHelper.sortByWeight(acceptableMediaTypes);
   }

   public MediaType getMediaType()
   {
      return mediaType;
   }

   public void setMediaType(MediaType mediaType)
   {
      this.mediaType = mediaType;
   }

   public Locale getLanguage()
   {
      return language;
   }

   public void setLanguage(String language)
   {
      if (language == null) return;
      this.language = LocaleHelper.extractLocale(language);
   }

   public void setAcceptableLanguages(List<String> acceptableLanguages)
   {
      if (acceptableLanguages == null)
      {
         this.acceptableLanguages = null;
      }
      else
      {
         this.acceptableLanguages = new ArrayList<Locale>(acceptableLanguages.size());
         List<WeightedLanguage> languages = new ArrayList<WeightedLanguage>(acceptableLanguages.size());
         for (String lang : acceptableLanguages)
         {
            languages.add(WeightedLanguage.parse(lang));
         }
         Collections.sort(languages);

         for (WeightedLanguage lang : languages) this.acceptableLanguages.add(lang.getLocale());
      }
   }

   public Map<String, Cookie> getCookies()
   {
      return cookies;
   }

   public void setCookies(Map<String, Cookie> cookies)
   {
      this.cookies = cookies;
   }

   public List<String> getRequestHeader(String name)
   {
      return requestHeaders.get(name);
   }

   public List<Locale> getAcceptableLanguages()
   {
      if (acceptableLanguages == null) acceptableLanguages = new ArrayList<Locale>();
      return acceptableLanguages;
   }

   @Override
   public Date getDate()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public int getLength()
   {
      throw new NotImplementedYetException();
   }
}
