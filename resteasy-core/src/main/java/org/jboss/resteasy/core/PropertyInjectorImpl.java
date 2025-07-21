package org.jboss.resteasy.core;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ValueInjector;
import org.jboss.resteasy.spi.util.FindAnnotation;
import org.jboss.resteasy.spi.util.MethodHashing;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PropertyInjectorImpl implements PropertyInjector {
    protected HashMap<Field, ValueInjector> fieldMap = new HashMap<Field, ValueInjector>();

    private static class SetterMethod {
        private SetterMethod(final Method method, final ValueInjector extractor) {
            this.method = method;
            this.extractor = extractor;
        }

        public Method method;
        public ValueInjector extractor;
    }

    protected List<SetterMethod> setters = new ArrayList<SetterMethod>();
    protected HashMap<Long, Method> setterhashes = new HashMap<Long, Method>();
    protected Class<?> clazz;

    public PropertyInjectorImpl(final Class<?> clazz, final ResteasyProviderFactory factory) {
        this.clazz = clazz;

        populateMap(clazz, factory);
    }

    protected void populateMap(Class<?> clazz, ResteasyProviderFactory factory) {
        for (Field field : clazz.getDeclaredFields()) {
            Annotation[] annotations = field.getAnnotations();
            if (annotations == null || annotations.length == 0)
                continue;
            Class<?> type = field.getType();
            Type genericType = field.getGenericType();

            ValueInjector extractor = getParameterExtractor(clazz, factory, field, field.getName(), annotations, type,
                    genericType);
            if (extractor != null) {
                if (!Modifier.isPublic(field.getModifiers())) {
                    field.setAccessible(true);
                }
                fieldMap.put(field, extractor);
            }
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.getName().startsWith("set"))
                continue;
            if (method.getParameterCount() != 1)
                continue;

            Annotation[] annotations = method.getAnnotations();
            if (annotations == null || annotations.length == 0)
                continue;

            Class<?> type = method.getParameterTypes()[0];
            Type genericType = method.getGenericParameterTypes()[0];

            String propertyName = Introspector.decapitalize(method.getName().substring(3));

            ValueInjector extractor = getParameterExtractor(clazz, factory, method, propertyName, annotations, type,
                    genericType);
            if (extractor != null) {
                long hash = 0;
                try {
                    hash = MethodHashing.methodHash(method);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (!Modifier.isPrivate(method.getModifiers())) {
                    Method older = setterhashes.get(hash);
                    if (older != null)
                        continue;
                }

                if (!Modifier.isPublic(method.getModifiers())) {
                    method.setAccessible(true);
                }
                setters.add(new SetterMethod(method, extractor));
                setterhashes.put(hash, method);
            }

        }
        if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class))
            populateMap(clazz.getSuperclass(), factory);

    }

    private ValueInjector getParameterExtractor(Class<?> clazz, ResteasyProviderFactory factory,
            AccessibleObject accessibleObject,
            String defaultName, Annotation[] annotations, Class<?> type, Type genericType) {
        boolean extractBody = (FindAnnotation.findAnnotation(annotations, Body.class) != null);
        ValueInjector injector = factory.getInjectorFactory().createParameterExtractor(clazz, accessibleObject, defaultName,
                type, genericType,
                annotations, extractBody, factory);
        return injector;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CompletionStage<Void> inject(HttpRequest request, HttpResponse response, Object target, boolean unwrapAsync)
            throws Failure {
        CompletionStage<Void> ret = null;
        for (Map.Entry<Field, ValueInjector> entry : fieldMap.entrySet()) {
            Object injectValue = entry.getValue().inject(request, response, unwrapAsync);
            if (injectValue != null && injectValue instanceof CompletionStage) {
                if (ret == null)
                    ret = CompletableFuture.completedFuture(null);
                ret = ret.thenCompose(v -> ((CompletionStage<Object>) injectValue)
                        .thenAccept(value -> {
                            try {
                                entry.getKey().set(target, CompletionStageHolder.resolve(value));
                            } catch (IllegalAccessException e) {
                                throw new InternalServerErrorException(e);
                            }
                        }));
            } else {
                try {
                    entry.getKey().set(target, CompletionStageHolder.resolve(injectValue));
                } catch (IllegalAccessException e) {
                    throw new InternalServerErrorException(e);
                }
            }
        }
        for (SetterMethod setter : setters) {
            Object injectedValue = setter.extractor.inject(request, response, unwrapAsync);
            if (injectedValue != null && injectedValue instanceof CompletionStage) {
                if (ret == null)
                    ret = CompletableFuture.completedFuture(null);
                ret = ret.thenCompose(v -> ((CompletionStage<Object>) injectedValue)
                        .thenAccept(value -> {
                            try {
                                setter.method.invoke(target, CompletionStageHolder.resolve(value));
                            } catch (IllegalAccessException e) {
                                throw new InternalServerErrorException(e);
                            } catch (InvocationTargetException e) {
                                throw new ApplicationException(e.getCause());
                            }
                        }));
            } else {
                try {
                    setter.method.invoke(target, CompletionStageHolder.resolve(injectedValue));
                } catch (IllegalAccessException e) {
                    throw new InternalServerErrorException(e);
                } catch (InvocationTargetException e) {
                    throw new ApplicationException(e.getCause());
                }

            }
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CompletionStage<Void> inject(Object target, boolean unwrapAsync) {
        CompletionStage<Void> ret = null;
        for (Map.Entry<Field, ValueInjector> entry : fieldMap.entrySet()) {
            Object injectedValue = entry.getValue().inject(unwrapAsync);
            if (injectedValue != null && injectedValue instanceof CompletionStage) {
                if (ret == null)
                    ret = CompletableFuture.completedFuture(null);
                ret = ret.thenCompose(v -> ((CompletionStage<Object>) injectedValue)
                        .thenAccept(value -> {
                            try {
                                entry.getKey().set(target, CompletionStageHolder.resolve(value));
                            } catch (IllegalAccessException e) {
                                throw new InternalServerErrorException(e);
                            }
                        }));

            } else {
                try {
                    entry.getKey().set(target, CompletionStageHolder.resolve(injectedValue));
                } catch (IllegalAccessException e) {
                    throw new InternalServerErrorException(e);
                }

            }
        }
        for (SetterMethod setter : setters) {
            Object injectedValue = setter.extractor.inject(unwrapAsync);
            if (injectedValue != null && injectedValue instanceof CompletionStage) {
                if (ret == null)
                    ret = CompletableFuture.completedFuture(null);
                ret = ret.thenCompose(v -> ((CompletionStage<Object>) injectedValue)
                        .thenAccept(value -> {
                            try {
                                setter.method.invoke(target, CompletionStageHolder.resolve(value));
                            } catch (IllegalAccessException e) {
                                throw new InternalServerErrorException(e);
                            } catch (InvocationTargetException e) {
                                throw new ApplicationException(e.getCause());
                            }
                        }));

            } else {
                try {
                    setter.method.invoke(target, CompletionStageHolder.resolve(injectedValue));
                } catch (IllegalAccessException e) {
                    throw new InternalServerErrorException(e);
                } catch (InvocationTargetException e) {
                    throw new ApplicationException(e.getCause());
                }

            }
        }
        return ret;
    }
}
