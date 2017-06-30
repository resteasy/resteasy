package org.jboss.resteasy.client.jaxrs.engines.factory;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.client.jaxrs.i18n.LogMessages;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;

/**
 * This factory determines what Engine should be used with the supplied httpClient
 * If no Httpclient is specified we use the new config style engine if allowed
 */
public class ApacheHttpClient4EngineFactory
{
    private ApacheHttpClient4EngineFactory()
    {

    }

    public static ClientHttpEngine createWithDefaultProxy(HttpHost defaultProxy)
    {
        if(isConfigurableAvailable())
        {
            ApacheHttpClient4Engine engine = new ApacheHttpClient43Engine(defaultProxy);
            //We have to check that the HttpClient to be used has the configurable interface
            if(isUsingOldStyleConfiguration(engine.getHttpClient()))
            {
                LogMessages.LOGGER.warn(Messages.MESSAGES.pleaseConsiderUnpdating());
                engine.close();
                return new ApacheHttpClient4Engine(defaultProxy);
            }
            return engine;
        }
        else
        {
            return new ApacheHttpClient4Engine(defaultProxy);
        }
    }

    public static ClientHttpEngine create()
    {
       return createWithDefaultProxy(null);
    }

    public static ClientHttpEngine create(HttpClient httpClient)
    {
        if(isUsingOldStyleConfiguration(httpClient))
        {
            return new ApacheHttpClient4Engine(httpClient);
        }
        else
        {
            return new ApacheHttpClient43Engine(httpClient);
        }
    }

    public static ClientHttpEngine create(HttpClient httpClient, boolean closeHttpClient)
    {
        if(isUsingOldStyleConfiguration(httpClient))
        {
            return new ApacheHttpClient4Engine(httpClient,closeHttpClient);
        }
        else
        {
            return new ApacheHttpClient43Engine(httpClient, closeHttpClient);
        }
    }

    public static ClientHttpEngine create(HttpClient httpClient, HttpContext httpContext)
    {
        if(isUsingOldStyleConfiguration(httpClient))
        {
            return new ApacheHttpClient4Engine(httpClient,httpContext);
        }
        else
        {
            return new ApacheHttpClient43Engine(httpClient, httpContext);
        }
    }

    private static boolean isUsingOldStyleConfiguration(HttpClient client)
    {
        /**
        if(!isConfigurableAvailable())
        {
            return true;
        }

        if(!(client instanceof Configurable)) // Yep, they could be using a new style config with a client that we can't actually use
        {
            LogMessages.LOGGER.warn("Please consider updating the version of Apache HttpClient being used.  Version 4.3.6+ is recommended.");
            return true;
        }

        RequestConfig config = ((Configurable) client).getConfig();
        return config == null;
        **/
        boolean isOld = true;
        try {
            client.getParams();
            LogMessages.LOGGER.warn(Messages.MESSAGES.pleaseConsiderUnpdating());

        } catch (UnsupportedOperationException e) {
            isOld = false;
        }

        return isOld;
    }

    private static boolean isConfigurableAvailable()
    {
        try
        {
            Class.forName("org.apache.http.client.methods.Configurable");
            return true;
        }
        catch (ClassNotFoundException e)
        {
           return false;
        }
    }
}
