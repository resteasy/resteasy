package org.jboss.resteasy.test.providers.jackson.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.lang.reflect.Proxy;

@Path("/")
public class ProxyWithGenericReturnTypeJacksonResource {
    @Produces("text/plain")
    @Path("test")
    public ProxyWithGenericReturnTypeJacksonSubResourceSubIntf resourceLocator() {
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{ProxyWithGenericReturnTypeJacksonSubResourceSubIntf.class},
                new ProxyWithGenericReturnTypeJacksonInvocationHandler());

        return ProxyWithGenericReturnTypeJacksonSubResourceSubIntf.class.cast(proxy);
    }
}
