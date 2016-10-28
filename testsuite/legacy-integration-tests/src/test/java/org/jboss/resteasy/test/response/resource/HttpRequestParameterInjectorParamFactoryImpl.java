package org.jboss.resteasy.test.response.resource;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.util.FindAnnotation;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

public class HttpRequestParameterInjectorParamFactoryImpl extends InjectorFactoryImpl {
    @SuppressWarnings("unchecked")
    @Override
    public ValueInjector createParameterExtractor(Class injectTargetClass,
                                                  AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory) {
        final HttpRequestParameterInjectorClassicParam param = FindAnnotation.findAnnotation(annotations, HttpRequestParameterInjectorClassicParam.class);
        if (param == null) {
            return super.createParameterExtractor(injectTargetClass, injectTarget, type,
                    genericType, annotations, factory);
        } else {
            return new ValueInjector() {
                public Object inject(HttpRequest request, HttpResponse response) {
                    return ResteasyProviderFactory.getContextData(HttpServletRequest.class)
                            .getParameter(param.value());
                }

                public Object inject() {
                    // do nothing.
                    return null;
                }
            };
        }
    }

    @Override
    public ValueInjector createParameterExtractor(Parameter parameter, ResteasyProviderFactory providerFactory) {
        final HttpRequestParameterInjectorClassicParam param = FindAnnotation.findAnnotation(parameter.getAnnotations(), HttpRequestParameterInjectorClassicParam.class);
        if (param == null) {
            return super.createParameterExtractor(parameter, providerFactory);
        } else {
            return new ValueInjector() {
                public Object inject(HttpRequest request, HttpResponse response) {
                    return ResteasyProviderFactory.getContextData(HttpServletRequest.class)
                            .getParameter(param.value());
                }

                public Object inject() {
                    // do nothing.
                    return null;
                }
            };
        }
    }
}
