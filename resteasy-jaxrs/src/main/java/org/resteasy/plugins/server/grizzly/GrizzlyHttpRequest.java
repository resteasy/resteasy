package org.resteasy.plugins.server.grizzly;

import org.resteasy.util.HttpRequestImpl;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GrizzlyHttpRequest extends HttpRequestImpl
{
   public GrizzlyHttpRequest(HttpHeaders httpHeaders, InputStream inputStream, UriInfo uri, String httpMethod)
   {
      super(inputStream, httpHeaders, httpMethod, uri);
   }
}
