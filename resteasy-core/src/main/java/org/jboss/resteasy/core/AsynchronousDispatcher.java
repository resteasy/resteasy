package org.jboss.resteasy.core;

import java.io.IOException;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.concurrent.ContextualExecutorService;
import org.jboss.resteasy.concurrent.ContextualExecutors;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.ResourceInvoker;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpHeaderNames;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class AsynchronousDispatcher extends SynchronousDispatcher {
    private static class Cache extends LinkedHashMap<String, Future<MockHttpResponse>> {
        private int maxSize = 100;

        Cache(final int maxSize) {
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Future<MockHttpResponse>> stringFutureEntry) {
            return size() > maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }
    }

    private static class SecureRandomWrapper {
        private static final int DEFAULT_MAX_USES = 100;
        private SecureRandom random;
        private int maxUses = -1;
        private int uses = 0; // uses > maxUses so that context parameters will get checked upon first use.

        public synchronized int nextInt() {
            if (++uses > maxUses) {
                reset();
            }
            return random.nextInt();
        }

        private void reset() {
            if (maxUses < 0) {
                maxUses = getMaxUses();
            }
            random = new SecureRandom();
            random.nextBytes(new byte[20]); // Causes SecureRandom to self seed.
            uses = 0;
        }

        private int getMaxUses() {
            maxUses = DEFAULT_MAX_USES;
            ResteasyConfiguration context = ResteasyContext.getContextData(ResteasyConfiguration.class);
            if (context != null) {
                String s = context.getParameter(ResteasyContextParameters.RESTEASY_SECURE_RANDOM_MAX_USE);
                if (s != null) {
                    try {
                        maxUses = Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        LogMessages.LOGGER.invalidFormat(ResteasyContextParameters.RESTEASY_SECURE_RANDOM_MAX_USE,
                                Integer.toString(DEFAULT_MAX_USES));
                    }
                }
            }
            return maxUses;
        }
    }

    private ContextualExecutorService executor;
    private int threadPoolSize = 100;
    private Map<String, Future<MockHttpResponse>> jobs;
    private Cache cache;
    private String basePath = "/asynch/jobs";
    private SecureRandomWrapper counter;
    private long maxWaitMilliSeconds = 300000;
    private int maxCacheSize = 100;

    public AsynchronousDispatcher(final ResteasyProviderFactory providerFactory) {
        super(providerFactory);
        counter = new SecureRandomWrapper();
    }

    public AsynchronousDispatcher(final ResteasyProviderFactory providerFactory, final ResourceMethodRegistry registry) {
        super(providerFactory, registry);
        counter = new SecureRandomWrapper();
    }

    /**
     * Max response cache size default is 100.
     *
     * @param maxCacheSize max cache size
     */
    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        if (cache != null)
            cache.setMaxSize(maxCacheSize);
    }

    /**
     * Maximum wait time. This overrides any wait query parameter.
     *
     * @param maxWaitMilliSeconds max wait time in millis
     */
    public void setMaxWaitMilliSeconds(long maxWaitMilliSeconds) {
        this.maxWaitMilliSeconds = maxWaitMilliSeconds;
    }

    /**
     * Set the base path to find jobs.
     *
     * @param basePath base path
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    /**
     * Fixed thread pool size of asynchronous delivery.
     *
     * @param threadPoolSize thread pool size
     */
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    /**
     * Plug in your own executor to process requests.
     *
     * @param executor executor service
     */
    public void setExecutor(ExecutorService executor) {
        this.executor = ContextualExecutors.wrap(executor, true);
    }

    public void start() {
        cache = new Cache(maxCacheSize);
        jobs = Collections.synchronizedMap(cache);
        if (executor == null)
            executor = ContextualExecutors.wrap(Executors.newFixedThreadPool(threadPoolSize), false);
        registry.addSingletonResource(this, basePath);
    }

    public void stop() {
        if (!executor.isManaged()) {
            executor.shutdown();
        }
    }

    @Path("{job-id}")
    @DELETE
    public void remove(@PathParam("job-id") String jobId) {
        jobs.remove(jobId);
    }

    @Path("{job-id}")
    @POST
    public Response readAndRemove(@QueryParam("wait") @DefaultValue("-1") long wait,
            @PathParam("job-id") String jobId) {
        return process(wait, jobId, true);
    }

    @Path("{job-id}")
    @GET
    public Response get(@QueryParam("wait") @DefaultValue("-1") long wait,
            @PathParam("job-id") String jobId) {
        return process(wait, jobId, false);
    }

    protected Response process(long wait, String jobId, boolean eatJob) {
        Future<MockHttpResponse> job = jobs.get(jobId);
        if (job == null)
            return Response.status(Response.Status.GONE).build();
        MockHttpResponse response = null;
        boolean nowait = false;
        // I don't want to wait forever!
        if (wait <= 0)
            nowait = true;

        if (nowait) {
            if (job.isDone()) {
                try {
                    response = job.get();
                } catch (Exception e) {
                    return Response.serverError().build();
                }
            }
        } else {
            if (wait > maxWaitMilliSeconds)
                wait = maxWaitMilliSeconds;
            try {
                response = job.get(wait, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                return Response.serverError().build();
            } catch (ExecutionException e) {
                return Response.serverError().build();
            } catch (TimeoutException e) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            }
        }
        if (response == null) {
            return Response.status(Response.Status.ACCEPTED).build();
        }
        Response.ResponseBuilder builder = Response.status(response.getStatus());
        builder.entity(response.getOutput());
        for (String name : response.getOutputHeaders().keySet()) {
            List values = response.getOutputHeaders().get(name);
            for (Object value : values) {
                builder.header(name, value);
            }
        }
        if (eatJob)
            jobs.remove(jobId);
        return builder.build();
    }

    public boolean isAsynchrnousRequest(HttpRequest in) {
        MultivaluedMap<String, String> queryParameters = in.getUri().getQueryParameters();
        return queryParameters.get("asynch") != null || queryParameters.get("oneway") != null;
    }

    public void invokeSuper(HttpRequest in, HttpResponse response, ResourceInvoker invoker) {
        super.invoke(in, response, invoker);
    }

    public void invoke(HttpRequest in, HttpResponse response, ResourceInvoker invoker) {
        MultivaluedMap<String, String> queryParameters = in.getUri().getQueryParameters();
        if (queryParameters.get("asynch") != null) {
            postJob(in, response, invoker);
        } else if (queryParameters.get("oneway") != null) {
            oneway(in, response, invoker);
        } else {
            super.invoke(in, response, invoker);
        }
    }

    public void postJob(HttpRequest request, HttpResponse response, final ResourceInvoker invoker) {
        final MockHttpRequest in;
        try {
            in = MockHttpRequest.deepCopy(request);
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
        Callable<MockHttpResponse> callable = new Callable<MockHttpResponse>() {

            public MockHttpResponse call() throws Exception {
                MockHttpResponse theResponse = new MockHttpResponse();

                try {
                    pushContextObjects(in, theResponse);
                    invokeSuper(in, theResponse, invoker);
                } finally {
                    clearContextData();
                }
                return theResponse;
            }

        };
        Future<MockHttpResponse> future = executor.submit(callable);
        String id = "" + System.currentTimeMillis() + "-" + counter.nextInt();
        jobs.put(id, future);
        response.setStatus(HttpResponseCodes.SC_ACCEPTED);
        URI uri = request.getUri().getBaseUriBuilder().path(basePath).path(id).build();
        response.getOutputHeaders().add(HttpHeaderNames.LOCATION, uri);
    }

    public void oneway(HttpRequest request, HttpResponse response, final ResourceInvoker invoker) {
        LogMessages.LOGGER.inOneWay();
        final MockHttpRequest in;
        try {
            in = MockHttpRequest.deepCopy(request);
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
        Runnable runnable = new Runnable() {

            public void run() {
                LogMessages.LOGGER.runningJob();
                MockHttpResponse theResponse = new MockHttpResponse();

                try {
                    pushContextObjects(in, theResponse);
                    invokeSuper(in, theResponse, invoker);
                } catch (Exception ignored) {
                    LogMessages.LOGGER.failedToInvokeAsynchronously(ignored);
                } finally {
                    clearContextData();
                }
            }

        };
        executor.execute(runnable);
        response.setStatus(HttpResponseCodes.SC_ACCEPTED);
    }
}
