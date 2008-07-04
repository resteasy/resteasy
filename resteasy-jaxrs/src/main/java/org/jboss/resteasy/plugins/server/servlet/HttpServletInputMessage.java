package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.util.HttpRequestImpl;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;

/**
 * Abstraction for an inbound http request on the server, or a response from a server to a client
 * <p/>
 * We have this abstraction so that we can reuse marshalling objects in a client framework and serverside framework
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServletInputMessage extends HttpRequestImpl
{

   public HttpServletInputMessage(HttpHeaders httpHeaders, InputStream inputStream, UriInfo uri, String httpMethod)
   {
      super(inputStream, httpHeaders, httpMethod, uri);
   }

}
