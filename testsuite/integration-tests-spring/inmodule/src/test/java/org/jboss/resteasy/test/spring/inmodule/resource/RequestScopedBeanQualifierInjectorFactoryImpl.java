package org.jboss.resteasy.test.spring.inmodule.resource;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.util.FindAnnotation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

@Provider
public class RequestScopedBeanQualifierInjectorFactoryImpl extends InjectorFactoryImpl implements
        BeanFactoryAware {
    BeanFactory beanFactory;

    @SuppressWarnings("rawtypes")
    @Override
    public ValueInjector createParameterExtractor(Class injectTargetClass,
                                                  AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory) {
        final Qualifier qualifier = FindAnnotation.findAnnotation(annotations, Qualifier.class);
        if (qualifier == null) {
            return super.createParameterExtractor(injectTargetClass, injectTarget, type,
                    genericType, annotations, factory);
        } else {
            return new ValueInjector() {
                public Object inject(HttpRequest request, HttpResponse response) {
                    return beanFactory.getBean(qualifier.value());
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
        final Qualifier qualifier = FindAnnotation.findAnnotation(parameter.getAnnotations(), Qualifier.class);
        if (qualifier == null) {
            return super.createParameterExtractor(parameter, providerFactory);
        } else {
            return new ValueInjector() {
                public Object inject(HttpRequest request, HttpResponse response) {
                    return beanFactory.getBean(qualifier.value());
                }

                public Object inject() {
                    // do nothing.
                    return null;
                }
            };
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
