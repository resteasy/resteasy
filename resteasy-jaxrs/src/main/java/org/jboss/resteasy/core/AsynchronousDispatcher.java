package org.jboss.resteasy.core;

import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class AsynchronousDispatcher extends SynchronousDispatcher
{
   protected ExecutorService executor;
   private int threadPoolSize = 1000;
   private Hashtable<String, Future<MockHttpResponse>> jobs = new Hashtable<String, Future<MockHttpResponse>>();
   private String basePath = "/asynch/jobs";
   private AtomicLong counter = new AtomicLong(0);


   public AsynchronousDispatcher()
   {
   }

   /**
    * Set the base path to find jobs
    *
    * @param basePath
    */
   public void setBasePath(String basePath)
   {
      this.basePath = basePath;
   }

   /**
    * Fixed thread pool size of asynchronous delivery
    *
    * @param threadPoolSize
    */
   public void setThreadPoolSize(int threadPoolSize)
   {
      this.threadPoolSize = threadPoolSize;
   }

   /**
    * Plug in your own executor to process requests
    *
    * @param executor
    */
   public void setExecutor(ExecutorService executor)
   {
      this.executor = executor;
   }

   public void start()
   {
      if (executor == null) executor = Executors.newFixedThreadPool(threadPoolSize);
      registry.addSingletonResource(this, basePath);
   }

   public void stop()
   {
      executor.shutdown();
   }

   @Path("{job-id}")
   @DELETE
   public void remove(@PathParam("job-id")String jobId)
   {
      jobs.remove(jobId);
   }

   @Path("{job-id}")
   @POST
   public Response readAndRemove(@QueryParam("wait") @DefaultValue("-1")long wait,
                                 @QueryParam("nowait") @DefaultValue("false")boolean nowait,
                                 @PathParam("job-id")String jobId)
   {
      return process(wait, nowait, jobId, true);
   }

   @Path("{job-id}")
   @GET
   public Response get(@QueryParam("wait") @DefaultValue("-1")long wait,
                       @QueryParam("nowait") @DefaultValue("false")boolean nowait,
                       @PathParam("job-id")String jobId)
   {
      return process(wait, nowait, jobId, false);
   }

   protected Response process(long wait, boolean nowait, String jobId, boolean eatJob)
   {
      Future<MockHttpResponse> job = jobs.get(jobId);
      if (job == null) return Response.status(Response.Status.GONE).build();
      MockHttpResponse response = null;

      // I don't want to wait forever!
      if (wait <= 0) nowait = true;

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
      if (eatJob) jobs.remove(jobId);
      return builder.build();
   }

   public void invokeSuper(HttpRequest request, HttpResponse response)
   {
      super.invoke(request, response);
   }

   public void invoke(HttpRequest in, HttpResponse response)
   {
      if (in.getUri().getQueryParameters().get("asynch") != null)
      {
         postJob(in, response);
      }
      else if (in.getUri().getQueryParameters().get("oneway") != null)
      {
         oneway(in, response);
      }
      else
      {
         super.invoke(in, response);
      }
   }

   public void postJob(HttpRequest request, HttpResponse response)
   {
      final MockHttpRequest in;
      try
      {
         in = MockHttpRequest.deepCopy(request);
      }
      catch (IOException e)
      {
         throw new Failure(e, 500);
      }
      Callable<MockHttpResponse> callable = new Callable<MockHttpResponse>()
      {

         public MockHttpResponse call() throws Exception
         {
            MockHttpResponse theResponse = new MockHttpResponse();

            try
            {
               invokeSuper(in, theResponse);
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

   public void oneway(HttpRequest request, HttpResponse response)
   {
      final MockHttpRequest in;
      try
      {
         in = MockHttpRequest.deepCopy(request);
      }
      catch (IOException e)
      {
         throw new Failure(e, 500);
      }
      Runnable runnable = new Runnable()
      {

         public void run()
         {
            MockHttpResponse theResponse = new MockHttpResponse();


            try
            {
               invokeSuper(in, theResponse);
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
