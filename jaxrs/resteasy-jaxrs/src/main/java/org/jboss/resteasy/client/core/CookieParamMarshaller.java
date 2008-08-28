package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.specimpl.UriBuilderImpl;

import javax.ws.rs.core.Cookie;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CookieParamMarshaller implements Marshaller
{
   private String cookieName;

   public CookieParamMarshaller(String cookieName)
   {
      this.cookieName = cookieName;
   }

   public String getCookieName()
   {
      return cookieName;
   }

   public void buildUri(Object object, UriBuilderImpl uri)
   {
   }

   public void setHeaders(Object object, HttpMethodBase httpMethod)
   {
      Cookie cookie = null;
      if (object instanceof Cookie) cookie = (Cookie) object;
      else cookie = new Cookie(cookieName, object.toString());

      httpMethod.setRequestHeader("Cookie", cookie.toString());
   }

   public void buildRequest(Object object, HttpMethodBase httpMethod)
   {
   }
}
