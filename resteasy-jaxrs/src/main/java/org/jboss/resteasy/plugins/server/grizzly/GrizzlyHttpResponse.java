package org.jboss.resteasy.plugins.server.grizzly;

import com.sun.grizzly.tcp.http11.GrizzlyResponse;
import com.sun.grizzly.util.http.Cookie;

import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GrizzlyHttpResponse implements HttpResponse
{
   protected GrizzlyResponse response;
   protected int status = 200;
   protected MultivaluedMap<String, Object> outputHeaders;

   public GrizzlyHttpResponse(GrizzlyResponse response, ResteasyProviderFactory factory)
   {
      this.response = response;
      outputHeaders = new GrizzlyResponseHeaders(response, factory);
   }

   public int getStatus()
   {
      return status;
   }

   public void setStatus(int status)
   {
      this.status = status;
      this.response.setStatus(status);
   }

   public MultivaluedMap<String, Object> getOutputHeaders()
   {
      return outputHeaders;
   }

   public OutputStream getOutputStream() throws IOException
   {
      return response.getOutputStream();
   }

   public void addNewCookie(NewCookie cookie)
   {
      Cookie cook = new Cookie(cookie.getName(), cookie.getValue());
      cook.setMaxAge(cookie.getMaxAge());
      cook.setVersion(cookie.getVersion());
      if (cookie.getDomain() != null) cook.setDomain(cookie.getDomain());
      if (cookie.getPath() != null) cook.setPath(cookie.getPath());
      cook.setSecure(cookie.isSecure());
      if (cookie.getComment() != null) cook.setComment(cookie.getComment());
      response.addCookie(cook);
   }

   public void sendError(int status) throws IOException
   {
      response.sendError(status);
   }

   public void sendError(int status, String message) throws IOException
   {
      response.sendError(status, message);
   }
}
