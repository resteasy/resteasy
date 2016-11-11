package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.lang.reflect.Proxy;

@Path("/")
public class ProxyWithGenericReturnTypeResource {
    @Produces("text/plain")
    @Path("test")
    public ProxyWithGenericReturnTypeSubResourceSubIntf resourceLocator() {
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{ProxyWithGenericReturnTypeSubResourceSubIntf.class},
                new ProxyWithGenericReturnTypeInvocationHandler());

        return ProxyWithGenericReturnTypeSubResourceSubIntf.class.cast(proxy);
    }
}
