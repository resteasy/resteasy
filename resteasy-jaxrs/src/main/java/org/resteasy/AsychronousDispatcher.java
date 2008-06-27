package org.resteasy;

import org.resteasy.mock.MockHttpResponse;
import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.HttpHeaderNames;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class AsychronousDispatcher
{
   protected ExecutorService executor;
   private Hashtable<String, Future<MockHttpResponse>> jobs = new Hashtable<String, Future<MockHttpResponse>>();
   private Dispatcher dispatcher;
   private String basePath;
   private AtomicLong counter = new AtomicLong(0);


   @Path("{job-id}")
   @DELETE
   public void remove(@PathParam("job-id")String jobId)
   {
      jobs.remove(jobId);
   }

   @Path("{job-id}")
   @GET
   public Response get(@QueryParam("wait") @DefaultValue("-1")long wait,
                       @QueryParam("nowait") @DefaultValue("false")boolean nowait,
                       @PathParam("job-id")String jobId)
   {
      Future<MockHttpResponse> job = jobs.get(jobId);
      if (job == null) return Response.status(Response.Status.GONE).build();
      MockHttpResponse response = null;
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
      Callable<MockHttpResponse> callable = new Callable<MockHttpResponse>()
      {

         public MockHttpResponse call() throws Exception
         {
            MockHttpResponse theResponse = new MockHttpResponse();


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
      Future<MockHttpResponse> future = executor.submit(callable);
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
            MockHttpResponse theResponse = new MockHttpResponse();


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
