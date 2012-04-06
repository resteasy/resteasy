package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.plugins.delegates.CookieHeaderDelegate;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.HeaderHelper;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.LocaleHelper;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.WeightedLanguage;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.RequestHeaders;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RequestHeadersImpl implements RequestHeaders
{
   protected CaseInsensitiveMap<Object> headers;
   protected ResteasyProviderFactory providerFactory;
   protected List<MediaType> acceptableMediaTypes;
   protected List<Locale> acceptableLanguages;
   protected Map<String, Cookie> cookies;
   protected MultivaluedMap<String, String> asMap;

   public RequestHeadersImpl(CaseInsensitiveMap<Object> headers, ResteasyProviderFactory providerFactory)
   {
      this.headers = headers;
      this.providerFactory = providerFactory;
   }

   public CaseInsensitiveMap<Object> getInternalHeaders()
   {
      return headers;
   }

   @Override
   public Set<Link> getLinks()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public boolean hasLink(String relation)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Link getLink(String relation)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Link.Builder getLinkBuilder(String relation)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Date getDate()
   {
      Object d = headers.getFirst(HttpHeaders.DATE);
      if (d == null) return null;
      if (d instanceof Date) return (Date) d;
      return DateUtil.parseDate(d.toString());
   }

   @Override
   public String getHeader(String name)
   {
      Object val = headers.getFirst(name);
      if (val == null) return null;

      return HeaderHelper.toHeaderString(val, providerFactory);
   }

   @Override
   public MultivaluedMap<String, String> asMap()
   {
      if (asMap != null) return asMap;
      asMap = new CaseInsensitiveMap<String>();
      for (Map.Entry<String, List<Object>> entry : headers.entrySet())
      {
         for (Object obj : entry.getValue())
         {
            asMap.add(entry.getKey(), HeaderHelper.toHeaderString(obj, providerFactory));
         }
      }
      return asMap;
   }

   @Override
   public List<String> getHeaderValues(String name)
   {
      List<Object> vals = headers.get(name);
      if (vals == null) return new ArrayList<String>();
      List<String> values = new ArrayList<String>();
      for (Object val : vals)
      {
         values.add(HeaderHelper.toHeaderString(val, providerFactory));
      }
      return values;
   }

   @Override
   public Locale getLanguage()
   {
      Object val = headers.getFirst(HttpHeaders.CONTENT_LANGUAGE);
      if (val == null) return null;
      if (val instanceof Locale) return (Locale)val;
      return LocaleHelper.extractLocale(val.toString());
   }

   @Override
   public int getLength()
   {
      Object val = headers.getFirst(HttpHeaders.CONTENT_LENGTH);
      if (val == null) return -1;
      return Integer.parseInt(val.toString());
   }

   @Override
   public MediaType getMediaType()
   {
      Object val = headers.getFirst(HttpHeaders.CONTENT_TYPE);
      if (val == null) return null;
      if (val instanceof MediaType) return (MediaType)val;
      return MediaType.valueOf(val.toString());
   }

   @Override
   public List<MediaType> getAcceptableMediaTypes()
   {
      if (acceptableMediaTypes != null) return acceptableMediaTypes;
      acceptableMediaTypes = new ArrayList<MediaType>();
      List<Object> accepts = headers.get(HttpHeaderNames.ACCEPT);
      if (accepts == null) return acceptableMediaTypes;

      for (Object accept : accepts)
      {
         if (accept instanceof MediaType)
         {
            acceptableMediaTypes.add((MediaType)accept);
         }
         else if (accept instanceof String)
         {
            acceptableMediaTypes.addAll(MediaTypeHelper.parseHeader((String) accept));
         }
      }
      MediaTypeHelper.sortByWeight(acceptableMediaTypes);
      return acceptableMediaTypes;
   }

   @Override
   public List<Locale> getAcceptableLanguages()
   {
      if (acceptableLanguages != null) return acceptableLanguages;
      acceptableLanguages = new ArrayList<Locale>();
      List<Object> accepts = headers.get(HttpHeaderNames.ACCEPT_LANGUAGE);
      if (accepts == null) return acceptableLanguages;

      List<WeightedLanguage> languages = new ArrayList<WeightedLanguage>();


      for (Object accept : accepts)
      {
         if (accept instanceof Locale)
         {
            languages.add((new WeightedLanguage((Locale)accept, 1.0F)));
         }
         else if (accept instanceof String)
         {
            String[] split = ((String)accept).split(",");
            for (String val : split) languages.add(WeightedLanguage.parse(val));
         }
      }
      Collections.sort(languages);

      for (WeightedLanguage lang : languages) this.acceptableLanguages.add(lang.getLocale());
      return acceptableLanguages;
   }

   @Override
   public Map<String, Cookie> getCookies()
   {
      if (cookies != null) return cookies;
      cookies = new HashMap<String, Cookie>();
      List<Object> cookieHeaders = headers.get(HttpHeaders.COOKIE);
      if (cookieHeaders == null) return cookies;

      for (Object cookie : cookieHeaders)
      {
         if (cookie instanceof Cookie)
         {
            Cookie cook = (Cookie)cookie;
            cookies.put(cook.getName(), cook);
         }
         else if (cookie instanceof String)
         {
            CookieHeaderDelegate d = new CookieHeaderDelegate();
            Cookie cook = (Cookie)d.fromString((String)cookie);
            cookies.put(cook.getName(), cook);
         }
      }
      return cookies;
   }


}
