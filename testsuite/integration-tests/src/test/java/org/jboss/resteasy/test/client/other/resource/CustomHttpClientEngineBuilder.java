package org.jboss.resteasy.test.client.other.resource;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.client.jaxrs.engines.ClientHttpEngineBuilder43;

public class CustomHttpClientEngineBuilder extends ClientHttpEngineBuilder43 {

    @Override
    protected ClientHttpEngine createEngine(HttpClientConnectionManager cm, RequestConfig.Builder rcBuilder,
            HttpHost defaultProxy, int responseBufferSize, HostnameVerifier verifier, SSLContext theContext) {
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(rcBuilder.build())
                .setProxy(defaultProxy)
                .disableContentCompression().build();
        ApacheHttpClient43Engine engine = new ApacheHttpClient43Engine(httpClient, true);
        engine.setResponseBufferSize(responseBufferSize);
        engine.setHostnameVerifier(verifier);
        // this may be null.  We can't really support this with Apache Client.
        engine.setSslContext(theContext);
        return engine;
    }
}
