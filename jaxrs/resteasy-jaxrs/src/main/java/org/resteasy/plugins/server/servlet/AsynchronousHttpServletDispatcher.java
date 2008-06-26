package org.resteasy.plugins.server.servlet;

import org.resteasy.Dispatcher;
import org.resteasy.specimpl.UriInfoImpl;
import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.Registry;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.ReadFromStream;
import org.resteasy.util.HttpResponseCodes;
import org.resteasy.util.HttpHeaderNames;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.DELETE;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AsynchronousHttpServletDispatcher extends HttpServlet
{
   protected Dispatcher dispatcher;

   public Dispatcher getDispatcher()
   {
      return dispatcher;
   }


   public void init(ServletConfig servletConfig) throws ServletException
   {
      ResteasyProviderFactory providerFactory = (ResteasyProviderFactory) servletConfig.getServletContext().getAttribute(ResteasyProviderFactory.class.getName());
      if (providerFactory == null)
      {
         providerFactory = new ResteasyProviderFactory();
         servletConfig.getServletContext().setAttribute(ResteasyProviderFactory.class.getName(), providerFactory);
      }

      dispatcher = (Dispatcher) servletConfig.getServletContext().getAttribute(Dispatcher.class.getName());
      if (dispatcher == null)
      {
         dispatcher = new Dispatcher(providerFactory);
         servletConfig.getServletContext().setAttribute(Dispatcher.class.getName(), dispatcher);
         servletConfig.getServletContext().setAttribute(Registry.class.getName(), dispatcher.getRegistry());
      }
   }

   public void setDispatcher(Dispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException
   {
      service(httpServletRequest.getMethod(), httpServletRequest, httpServletResponse);
   }

   /**
    * wrapper around service so we can test easily
    *
    * @param httpServletRequest
    * @param httpServletResponse
    * @throws javax.servlet.ServletException
    * @throws java.io.IOException
    */
   public void invoke(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException
   {
      service(httpServletRequest, httpServletResponse);
   }

   @Path("/")
   public static class AsynchronousResponses
   {
      protected ExecutorService executor;
      private Hashtable<String, Future<BridgeResponse>> jobs = new Hashtable<String, Future<BridgeResponse>>();
      private Dispatcher dispatcher;
      private String basePath;
      private AtomicLong counter = new AtomicLong(0);


      @Path("{job-id}")
      @DELETE
      public void remove(@PathParam("job-id") String jobId)
      {
         jobs.remove(jobId);
      }




      @Path("{job-id}")
      @GET
      public Response get(@QueryParam("wait") @DefaultValue("-1") long wait,
                          @QueryParam("nowait") @DefaultValue("false") boolean nowait,
                          @PathParam("job-id") String jobId)
      {
         Future<BridgeResponse> job = jobs.get(jobId);
         if (job == null) return Response.status(Response.Status.GONE).build();
         BridgeResponse response = null;
         if (nowait)
         {
            if (job.isDone())
            {
               try
               {
                  response = job.get();
               }
               catch (Exception e)
               {
                  return Response.serverError().build();
               }
            }
         }
         if (wait == -1)
         {
            try
            {
               response = job.get();
            }
            catch (Exception e)
            {
               return Response.serverError().build();
            }
         }
         else
         {
            try
            {
               response = job.get(wait, TimeUnit.MILLISECONDS);
            }
            catch (Exception e)
            {
               return Response.serverError().build();
            }
         }
         if (response == null)
         {
            return Response.status(Response.Status.ACCEPTED).build();
         }
         Response.ResponseBuilder builder = Response.status(response.getStatus());
         builder.entity(response.getOutput());
         for (String name : response.getOutputHeaders().keySet())
         {
            List values = response.getOutputHeaders().get(name);
            for (Object value : values)
            {
               builder.header(name, value);
            }
         }
         return builder.build();
      }

      public void postJob(final HttpRequest request, final HttpResponse response)
      {
         Callable<BridgeResponse> callable = new Callable<BridgeResponse>()
         {

            public BridgeResponse call() throws Exception
            {
               BridgeResponse theResponse = new BridgeResponse();


               try
               {
                  dispatcher.invoke(request, theResponse);
               }
               finally
               {
                  ResteasyProviderFactory.clearContextData();
               }
               return theResponse;
            }

         };
         Future<BridgeResponse> future  = executor.submit(callable);
         String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
         jobs.put(id, future);
         response.setStatus(HttpResponseCodes.SC_ACCEPTED);
         URI uri = request.getUri().getBaseUriBuilder().path(basePath, id).build();
         response.getOutputHeaders().add(HttpHeaderNames.LOCATION, uri);
      }

      public void oneway(final HttpRequest request, final HttpResponse response)
      {
         Runnable runnable = new Runnable()
         {

            public void run()
            {
               BridgeResponse theResponse = new BridgeResponse();


               try
               {
                  dispatcher.invoke(request, theResponse);
               }
               catch (Exception ignored) {}
               finally
               {
                  ResteasyProviderFactory.clearContextData();
               }
            }

         };
         executor.execute(runnable);
         response.setStatus(HttpResponseCodes.SC_ACCEPTED);
      }
   }


   public void service(String httpMethod, HttpServletRequest request, HttpServletResponse response) throws IOException
   {
      HttpHeaders headers = ServletUtil.extractHttpHeaders(request);
      UriInfoImpl uriInfo = ServletUtil.extractUriInfo(request);

      ByteArrayInputStream bais = new ByteArrayInputStream(ReadFromStream.readFromStream(1024, request.getInputStream()));

      final HttpRequest in = new HttpServletInputMessage(headers, bais, uriInfo, httpMethod.toUpperCase());


   }

}