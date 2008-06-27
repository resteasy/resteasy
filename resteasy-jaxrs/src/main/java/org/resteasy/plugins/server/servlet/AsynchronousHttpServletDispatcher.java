package org.resteasy.plugins.server.servlet;

import org.resteasy.Dispatcher;
import org.resteasy.specimpl.UriInfoImpl;
import org.resteasy.spi.HttpRequest;
import org.resteasy.util.ReadFromStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsynchronousHttpServletDispatcher extends HttpServletDispatcher
{
   protected Dispatcher asynchDispatcher;

   public void init(ServletConfig servletConfig) throws ServletException
   {
      super.init(servletConfig);

   }

   public void service(String httpMethod, HttpServletRequest request, HttpServletResponse response) throws IOException
   {
      HttpHeaders headers = ServletUtil.extractHttpHeaders(request);
      UriInfoImpl uriInfo = ServletUtil.extractUriInfo(request);

      ByteArrayInputStream bais = new ByteArrayInputStream(ReadFromStream.readFromStream(1024, request.getInputStream()));

      final HttpRequest in = new HttpServletInputMessage(headers, bais, uriInfo, httpMethod.toUpperCase());

      // TODO not finished yet!  Work in progress!
   }

}