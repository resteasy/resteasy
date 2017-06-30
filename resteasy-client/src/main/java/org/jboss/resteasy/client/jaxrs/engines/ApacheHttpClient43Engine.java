package org.jboss.resteasy.client.jaxrs.engines;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;

/**
 * An Apache HTTP engine for use with the new Builder Config style.
 * To accommodate the configuration style this class overrides the methods that get/set from the old Parameters
 * Otherwise only the old style parameters will get picked up.
 *
 * Consider using the factory ApacheHttpClient4EngineFactory instead of using this class directly
 */
public class ApacheHttpClient43Engine extends ApacheHttpClient4Engine
{

    public ApacheHttpClient43Engine()
    {
        super();
    }

    public ApacheHttpClient43Engine(final HttpHost defaultProxy) {
        super(defaultProxy);
    }

    public ApacheHttpClient43Engine(final HttpClient httpClient) {
        super(httpClient);
    }

    public ApacheHttpClient43Engine(final HttpClient httpClient, final boolean closeHttpClient)
    {
        super(httpClient, closeHttpClient);
    }

    /**
     * Creates a client engine instance using the specified {@link org.apache.http.client.HttpClient}
     * and {@link org.apache.http.protocol.HttpContext} instances.
     * Note that the same instance of httpContext is passed to the engine, which may store thread unsafe
     * attributes in it. It is hence recommended to override the HttpClient
     * <pre>execute(HttpUriRequest request, HttpContext context)</pre> method to perform a deep
     * copy of the context before executing the request.
     * 
     * @param httpClient     The http client
     * @param httpContext    The context to be used for executing requests
     */
    @Deprecated
    public ApacheHttpClient43Engine(final HttpClient httpClient, final HttpContext httpContext)
    {
        super(httpClient, httpContext);
    }
    
    public ApacheHttpClient43Engine(HttpClient httpClient, HttpContextProvider httpContextProvider)
    {
       this.httpClient = httpClient;
       this.httpContextProvider = httpContextProvider;
    }

    @Override
    protected HttpClient createDefaultHttpClient()
    {
        final HttpClientBuilder builder = HttpClientBuilder.create();
        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        if(defaultProxy != null)
        {
            requestBuilder.setProxy(defaultProxy);
        }
        builder.disableContentCompression();
        builder.setDefaultRequestConfig(requestBuilder.build());
        return builder.build();
    }

    @Override
    public HttpHost getDefaultProxy()
    {
        Configurable clientConfiguration = (Configurable) httpClient;
        return clientConfiguration.getConfig().getProxy();
    }

    @Override
    protected void setRedirectRequired(final ClientInvocation request, final HttpRequestBase httpMethod)
    {
        RequestConfig.Builder requestBuilder = RequestConfig.copy(getCurrentConfiguration(request, httpMethod));
        requestBuilder.setRedirectsEnabled(true);
        httpMethod.setConfig(requestBuilder.build());
    }

    @Override
    protected void setRedirectNotRequired(final ClientInvocation request, final HttpRequestBase httpMethod)
    {
        RequestConfig.Builder requestBuilder = RequestConfig.copy(getCurrentConfiguration(request, httpMethod));
        requestBuilder.setRedirectsEnabled(false);
        httpMethod.setConfig(requestBuilder.build());
    }

    private RequestConfig getCurrentConfiguration(final ClientInvocation request,
                                                  final HttpRequestBase httpMethod)
    {
        RequestConfig baseConfig;
        if (httpMethod != null && httpMethod.getConfig() != null)
        {
            baseConfig = httpMethod.getConfig();
        }
        else
        {
            ApacheHttpClient43Engine engine =
                ((ApacheHttpClient43Engine)request.getClient().httpEngine());
            baseConfig = ((Configurable)engine.getHttpClient()).getConfig();
            if (baseConfig == null) {
                Configurable clientConfiguration = (Configurable) httpClient;
                baseConfig = clientConfiguration.getConfig();
            }
        }
        return baseConfig;
    }
}
