package org.jboss.resteasy.plugins.server.tjws;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.util.Encode;
import org.jboss.resteasy.util.HttpServletRequestDelegate;

import javax.servlet.http.HttpServletRequest;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * fix bug in non-encoded getRequestURI and URL
 * as well as the correct context Path.
 * 
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated See resteasy-undertow module.
 */
@Deprecated
public class PatchedHttpServletRequest extends HttpServletRequestDelegate
{
   private URI encodedURI;
   private URL encodedURL;
   private String contextPath;

   public PatchedHttpServletRequest(HttpServletRequest delegate, String contextPath)
   {
      super(delegate);
      this.contextPath = contextPath;
      URL url = null;
      try
      {
         url = new URL(delegate.getRequestURL().toString());
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException(e);
      }
      String buf = extractURI(url);
      try
      {
         encodedURI = URI.create(buf);
      }
      catch (Exception e)
      {
         throw new RuntimeException(Messages.MESSAGES.unableToCreateURI(buf), e);
      }
      try
      {
         encodedURL = new URL(encodedURI.toString());
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private static String extractURI(URL url)
   {
      StringBuffer buffer = new StringBuffer(url.getProtocol()).append("://");
      if (url.getHost() != null) buffer.append(url.getHost());
      if (url.getPort() != -1 && url.getPort() != 80) buffer.append(":").append(Integer.toString(url.getPort()));
      if (url.getPath() != null)
         buffer.append(Encode.encodePath(url.getPath()));
      if (url.getQuery() != null) buffer.append("?").append(url.getQuery());
      if (url.getRef() != null) buffer.append("#").append(Encode.encodeFragment(url.getRef()));
      String buf = buffer.toString();
      return buf;
   }

   @Override
   public String getContextPath()
   {
      return contextPath;
   }

   @Override
   public String getRequestURI()
   {
      return encodedURI.getRawPath();
   }

   @Override
   public StringBuffer getRequestURL()
   {
      return new StringBuffer(encodedURL.toString());
   }

}
