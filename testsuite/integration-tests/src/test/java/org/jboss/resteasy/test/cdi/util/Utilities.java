package org.jboss.resteasy.test.cdi.util;


import org.jboss.resteasy.test.cdi.extensions.resource.CDIExtensionsBoston;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Qualifier;

@Singleton
public class Utilities {
    @Inject
    private BeanManager beanManager;

    public static Class<? extends Annotation> getScopeAnnotation(Class<?> c) {

        return getScopeAnnotation(c.getAnnotations());
    }

    public static Class<? extends Annotation> getScopeAnnotation(Annotation[] annotations) {
        for (int i = 0; i < annotations.length; i++) {
            Class<? extends Annotation> annotationType = annotations[i].annotationType();
            Annotation[] typeAnnotations = annotationType.getAnnotations();
            for (int j = 0; j < typeAnnotations.length; j++) {
                if (NormalScope.class.equals(typeAnnotations[j].annotationType())) {
                    return annotationType;
                }
            }
        }
        return null;
    }

    public static Class<? extends Annotation> getScopeAnnotation(Set<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            Annotation[] typeAnnotations = annotationType.getAnnotations();
            for (int j = 0; j < typeAnnotations.length; j++) {
                if (NormalScope.class.equals(typeAnnotations[j].annotationType())) {
                    return annotationType;
                }
            }
        }
        return null;
    }

    public static Set<Annotation> getQualifiers(Class<?> clazz) {
        return getQualifiers(clazz.getAnnotations());
    }

    public static Set<Annotation> getQualifiers(Annotation[] annotations) {
        HashSet<Annotation> result = new HashSet<Annotation>();
        for (int i = 0; i < annotations.length; i++) {
            Class<?> annotationType = annotations[i].annotationType();
            Annotation[] typeAnnotations = annotationType.getAnnotations();
            for (int j = 0; j < typeAnnotations.length; j++) {
                if (Qualifier.class.equals(typeAnnotations[j].annotationType())) {
                    result.add(annotations[i]);
                    break;
                }
            }
        }
        return result;
    }

    public static boolean hasQualifier(Class<?> clazz, Class<?> qualifier) {
        Annotation[] annotations = clazz.getAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            if (qualifier.equals(annotations[i].annotationType())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasQualifier(Set<Annotation> annotations, Class<?> qualifier) {
        for (Annotation annotation : annotations) {
            if (qualifier.equals(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBoston(Class<?> clazz) {
        return hasQualifier(clazz, CDIExtensionsBoston.class);
    }

    public static Set<Type> getTypeClosure(Class<?> clazz) {
        HashSet<Type> set = new HashSet<Type>();
        accumulateTypes(set, clazz);
        return set;
    }

    static void accumulateTypes(Set<Type> set, Class<?> clazz) {
        set.add(clazz);
        if (clazz.getSuperclass() != null) {
            accumulateTypes(set, clazz.getSuperclass());
        }
        for (Class<?> c : clazz.getInterfaces()) {
            accumulateTypes(set, c);
        }
    }

    public static Set<Annotation> getAnnotationSet(Class<?> clazz) {
        return new HashSet<Annotation>(Arrays.asList(clazz.getAnnotations()));
    }

    public static boolean isAnnotationPresent(Class<?> clazz, Class<? extends Annotation> annotationType) {
        for (Annotation annotation : clazz.getAnnotations()) {
            if (annotation.annotationType().equals(annotationType)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType) {
        for (Annotation annotation : clazz.getAnnotations()) {
            if (annotation.annotationType().equals(annotationType)) {
                return (T) annotation;
            }
        }
        return null;
    }

    public boolean isApplicationScoped(Class<?> c) {
        return testScope(c, ApplicationScoped.class);
    }

    public boolean isDependentScoped(Class<?> c) {
        return testScope(c, Dependent.class);
    }

    public boolean isRequestScoped(Class<?> c) {
        return testScope(c, RequestScoped.class);
    }

    public boolean isSessionScoped(Class<?> c) {
        return testScope(c, SessionScoped.class);
    }

    public boolean testScope(Class<?> c, Class<?> scopeClass) {
        Class<? extends Annotation> annotation = getScope(c);
        if (annotation == null) {
            return false;
        }
        return annotation.isAssignableFrom(scopeClass);
    }

    public Class<? extends Annotation> getScope(Class<?> c) {
        Set<Bean<?>> beans = beanManager.getBeans(c);
        if (beans != null && !beans.isEmpty()) {
            Iterator<Bean<?>> it = beans.iterator();
            if (it.hasNext()) {
                Bean<?> bean = beans.iterator().next();
                return bean.getScope();
            }
        }
        return null;
    }
}
