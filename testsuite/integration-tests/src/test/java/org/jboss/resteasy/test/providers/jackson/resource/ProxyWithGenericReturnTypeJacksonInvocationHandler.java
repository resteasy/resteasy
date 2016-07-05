package org.jboss.resteasy.test.providers.jackson.resource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ProxyWithGenericReturnTypeJacksonInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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
