package org.resteasy.plugins.client.httpclient;

import org.apache.commons.httpclient.HttpClient;
import org.resteasy.ClientInvoker;
import org.resteasy.ClientProxy;
import org.resteasy.util.IsHttpMethod;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.ext.ProviderFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ProxyFactory {

    public static <T> T create(Class<T> clazz, String base) {
        return create(clazz, base, new HttpClient());
    }

    public static <T> T create(Class<T> clazz, String base, HttpClient client) {
        try {
            return create(clazz, new URI(base), client, ProviderFactory.getInstance());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T create(Class<T> clazz, URI baseUri, HttpClient httpClient, ProviderFactory providerFactory) {
        HashMap<Method, ClientInvoker> methodMap = new HashMap<Method, ClientInvoker>();

        for (Method method : clazz.getMethods()) {
            ClientInvoker invoker = null;
            Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
            if (httpMethods == null)
                throw new RuntimeException("Method must be annotated with an http method annotation @GET, etc..");
            if (httpMethods.size() != 1)
                throw new RuntimeException("You may only annotate a method with only one http method annotation");

            if (httpMethods.contains(HttpMethod.GET))
                invoker = new GetInvoker(httpClient, clazz, method, providerFactory);
            else if (httpMethods.contains(HttpMethod.PUT))
                invoker = new PutInvoker(httpClient, clazz, method, providerFactory);
            else if (httpMethods.contains(HttpMethod.POST))
                invoker = new PostInvoker(httpClient, clazz, method, providerFactory);
            else if (httpMethods.contains(HttpMethod.DELETE))
                invoker = new DeleteInvoker(httpClient, clazz, method, providerFactory);
            else throw new RuntimeException("@" + httpMethods.iterator().next() + " is not supported yet");

            invoker.setBaseUri(baseUri);
            methodMap.put(method, invoker);
        }

        Class<?>[] intfs = {clazz};

        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), intfs, new ClientProxy(methodMap));
    }
}
