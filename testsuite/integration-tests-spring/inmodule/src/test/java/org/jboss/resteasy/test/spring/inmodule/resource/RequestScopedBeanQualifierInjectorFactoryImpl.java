package org.jboss.resteasy.test.spring.inmodule.resource;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.spi.ValueInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.spi.util.FindAnnotation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Provider
public class RequestScopedBeanQualifierInjectorFactoryImpl extends InjectorFactoryImpl implements
      BeanFactoryAware {
   BeanFactory beanFactory;

   @SuppressWarnings("rawtypes")
   @Override
   public ValueInjector createParameterExtractor(Class injectTargetClass,
                                                  AccessibleObject injectTarget, String defaultName, Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory) {
      final Qualifier qualifier = FindAnnotation.findAnnotation(annotations, Qualifier.class);
      if (qualifier == null) {
         return super.createParameterExtractor(injectTargetClass, injectTarget, defaultName, type,
               genericType, annotations, factory);
      } else {
         return new ValueInjector() {
            @Override
            public CompletionStage<Object> inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
               return CompletableFuture.completedFuture(beanFactory.getBean(qualifier.value()));
            }

            @Override
            public CompletionStage<Object> inject(boolean unwrapAsync) {
               // do nothing.
               return CompletableFuture.completedFuture(null);
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
            @Override
            public CompletionStage<Object> inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
               return CompletableFuture.completedFuture(beanFactory.getBean(qualifier.value()));
            }

            @Override
            public CompletionStage<Object> inject(boolean unwrapAsync) {
               // do nothing.
               return CompletableFuture.completedFuture(null);
            }
         };
      }
   }

   @Override
   public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
      this.beanFactory = beanFactory;
   }
}
