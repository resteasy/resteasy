package org.jboss.resteasy.test.client.proxy.resource;

import java.lang.reflect.Proxy;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
public class ProxyWithGenericReturnTypeResource {
    @Produces("text/plain")
    @Path("test")
    public ProxyWithGenericReturnTypeSubResourceSubIntf resourceLocator() {
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[] { ProxyWithGenericReturnTypeSubResourceSubIntf.class },
                new ProxyWithGenericReturnTypeInvocationHandler());

        return ProxyWithGenericReturnTypeSubResourceSubIntf.class.cast(proxy);
    }
}
