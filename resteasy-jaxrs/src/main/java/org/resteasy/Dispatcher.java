package org.resteasy;

import org.resteasy.specimpl.RequestImpl;
import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.Registry;
import org.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Dispatcher
{
   protected ResteasyProviderFactory providerFactory;
   protected ResourceMethodRegistry registry;

   public Dispatcher(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      this.registry = new ResourceMethodRegistry(providerFactory);
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public Registry getRegistry()
   {
      return registry;
   }

   public void invoke(HttpRequest in, HttpResponse response)
   {
      ResourceInvoker invoker = null;
      try
      {
         invoker = registry.getResourceInvoker(in, response);
      }
      catch (Failure e)
      {
         try
         {
            response.sendError(e.getErrorCode());
         }
         catch (IOException e1)
         {
            throw new RuntimeException(e1);
         }
         e.printStackTrace();
         return;
      }
      if (invoker == null)
      {
         try
         {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         return;
      }
      try
      {
         ResteasyProviderFactory.pushContext(HttpRequest.class, in);
         ResteasyProviderFactory.pushContext(HttpResponse.class, response);
         ResteasyProviderFactory.pushContext(HttpHeaders.class, in.getHttpHeaders());
         ResteasyProviderFactory.pushContext(UriInfo.class, in.getUri());
         ResteasyProviderFactory.pushContext(Request.class, new RequestImpl(in.getHttpHeaders(), in.getHttpMethod()));
         try
         {
            invoker.invoke(in, response);
         }
         catch (Failure e)
         {
            try
            {
               response.sendError(e.getErrorCode());
            }
            catch (IOException e1)
            {
               throw new RuntimeException(e1);
            }
            e.printStackTrace();
            return;
         }

      }
      catch (Exception e)
      {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         e.printStackTrace();
         return;
      }
      finally
      {
         ResteasyProviderFactory.clearContextData();
      }
   }

}
