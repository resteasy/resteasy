package org.resteasy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientProxy implements InvocationHandler {
    private Map<Method, ClientInvoker> methodMap;

    public ClientProxy(Map<Method, ClientInvoker> methodMap) {
        this.methodMap = methodMap;
    }

    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        return methodMap.get(method).invoke(args);
    }
}
