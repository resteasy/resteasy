package org.jboss.resteasy.microprofile.client;

import javax.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import java.lang.reflect.Method;

import static org.jboss.resteasy.microprofile.client.utils.ClientRequestContextUtils.getMethod;


@Priority(Integer.MIN_VALUE)
public class MethodInjectionFilter implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext requestContext) {
        Method method = getMethod(requestContext);
        requestContext.setProperty("org.eclipse.microprofile.rest.client.invokedMethod", method);
    }
}
