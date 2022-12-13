package org.jboss.resteasy.test.client.proxy.resource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ProxyWithGenericReturnTypeInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<String> result = new ArrayList<String>();
        return result;
    }
}
