package org.jboss.resteasy.client.jaxrs.internal.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.client.WebTarget;

import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ProxyConfig;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

public class SubResourceInvoker implements MethodInvoker {
    final ProxyConfig config;
    final Class<?> iface;
    final Method method;
    final ResteasyWebTarget parent;
    Annotation[] jaxParams;
    boolean hasJaxParams;

    public SubResourceInvoker(final ResteasyWebTarget parent, final Method method, final ProxyConfig config) {
        this.config = config;
        this.method = method;
        this.iface = method.getReturnType();
        jaxParams = new Annotation[method.getParameterCount()];
        for (int i = 0; i < jaxParams.length; i++) {
            Annotation[] paramAnnotations = method.getParameterAnnotations()[i];
            for (Annotation annotation : paramAnnotations) {
                if (annotation instanceof PathParam || annotation instanceof MatrixParam) {
                    jaxParams[i] = annotation;
                    hasJaxParams = true;
                    break;
                }
            }
        }
        if (method.isAnnotationPresent(Path.class)) {
            this.parent = parent.path(method.getAnnotation(Path.class).value());
        } else {
            this.parent = parent;
        }
    }

    @Override
    public Object invoke(Object[] args) {
        WebTarget target = parent;
        if (hasJaxParams) {
            HashMap<String, Object> pathParams = new HashMap<String, Object>();
            for (int i = 0; i < jaxParams.length; i++) {
                if (jaxParams[i] instanceof PathParam) {
                    pathParams.put(((PathParam) jaxParams[i]).value(), args[i]);
                } else if (jaxParams[i] instanceof MatrixParam) {
                    target = target.matrixParam(((MatrixParam) jaxParams[i]).value(), args[i]);
                }
            }
            if (!pathParams.isEmpty()) {
                target = target.resolveTemplates(pathParams);
            }
        }
        return ProxyBuilder.proxy(iface, target, config);
    }
}
