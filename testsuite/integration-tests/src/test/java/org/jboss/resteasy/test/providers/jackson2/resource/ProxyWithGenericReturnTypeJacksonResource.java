package org.jboss.resteasy.test.providers.jackson2.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class ProxyWithGenericReturnTypeJacksonResource {

    protected static final Logger logger = Logger.getLogger(ProxyWithGenericReturnTypeJacksonResource.class.getName());

    @Produces("text/plain")
    @Path("test")
    public ProxyWithGenericReturnTypeJacksonSubResourceSubIntf resourceLocator() {
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{ProxyWithGenericReturnTypeJacksonSubResourceSubIntf.class},
                new TestInvocationHandler());

        return ProxyWithGenericReturnTypeJacksonSubResourceSubIntf.class.cast(proxy);
    }

    static class TestInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            logger.info("entered proxied subresource");
            logger.info("method: " + method.getName());
            logger.info("generic return type: " + method.getGenericReturnType());
            logger.info("type of return type: " + method.getGenericReturnType().getClass());
            if ("resourceMethod".equals(method.getName())) {
                List<ProxyWithGenericReturnTypeJacksonAbstractParent> l = new ArrayList<ProxyWithGenericReturnTypeJacksonAbstractParent>();
                ProxyWithGenericReturnTypeJacksonType1 first = new ProxyWithGenericReturnTypeJacksonType1();
                first.setId(1);
                first.setName("MyName");
                l.add(first);

                ProxyWithGenericReturnTypeJacksonType2 second = new ProxyWithGenericReturnTypeJacksonType2();
                second.setId(2);
                second.setNote("MyNote");
                l.add(second);
                return l;
            }

            if ("resourceMethodOne".equals(method.getName())) {
                ProxyWithGenericReturnTypeJacksonType1 first = new ProxyWithGenericReturnTypeJacksonType1();
                first.setId(1);
                first.setName("MyName");
                return first;
            }

            return null;
        }
    }
}
