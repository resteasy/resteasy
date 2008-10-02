package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.util.Encode;
import org.jboss.resteasy.util.HttpRequestImpl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
   protected HttpServletRequest request;


   public HttpServletInputMessage(HttpServletRequest request, HttpHeaders httpHeaders, InputStream inputStream, UriInfo uri, String httpMethod)
   {
      super(inputStream, httpHeaders, httpMethod, uri);
      this.request = request;
   }

   @Override
   public MultivaluedMap<String, String> getFormParameters()
   {
      if (formParameters != null) return formParameters;
      formParameters = Encode.encode(getDecodedFormParameters());
      return formParameters;
   }

   @Override
   public MultivaluedMap<String, String> getDecodedFormParameters()
   {
      if (decodedFormParameters != null) return decodedFormParameters;
      decodedFormParameters = new MultivaluedMapImpl<String, String>();
      Map<String, String[]> params = request.getParameterMap();
      for (Map.Entry<String, String[]> entry : params.entrySet())
      {
         String name = entry.getKey();
         String[] values = entry.getValue();
         MultivaluedMap<String, String> queryParams = uri.getQueryParameters();
         List<String> queryValues = queryParams.get(name);
         if (queryValues == null)
         {
            for (String val : values) decodedFormParameters.add(name, val);
         }
         else
         {
            for (String val : values)
            {
               if (!queryValues.contains(val))
               {
                  decodedFormParameters.add(name, val);
               }
            }
         }
      }
      return decodedFormParameters;

   }
}
