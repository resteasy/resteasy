package org.jboss.resteasy.cdi;

import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.util.AnnotationResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Extension of the {@link AnnotationResolver} providing annotation
 * resolution also in CDI stereotypes
 */
public class CDIAnnotationResolver extends AnnotationResolver
{
    @Override
    public Class getClassWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation)
    {
        Class annotatedClass = super.getClassWithAnnotation(clazz, annotation);

        if (annotatedClass == null && findAnnotationInStereotypes(annotation, null, clazz) != null)
        {
            return clazz;
        }

        return annotatedClass;
    }

    @Override
    public <T extends Annotation> T getAnnotationFromClass(Class<T> annotationClass, Class<?> clazz)
    {
        T annotation = super.getAnnotationFromClass(annotationClass, clazz);

        if (annotation == null)
        {
            annotation = findAnnotationInStereotypes(annotationClass, null, clazz);
        }

        return annotation;
    }

    @Override
    public <T extends Annotation> T getAnnotationFromResourceMethod(Class<T> annotationClass, Method method, ResourceClass resourceClass)
    {
        T annotation = super.getAnnotationFromResourceMethod(annotationClass, method, resourceClass);

        if (annotation == null)
        {
            return findAnnotationInStereotypes(annotationClass, method, resourceClass.getClazz());
        }

        return annotation;
    }

    @Override
    public <T extends Annotation> T getAnnotationFromMethod(Class<T> annotationClass, Method method)
    {
        T annotation = super.getAnnotationFromMethod(annotationClass, method);

        if (annotation == null)
        {
            return findAnnotationInStereotypes(annotationClass, method, null);
        }

        return annotation;
    }

    private <T extends Annotation> T findAnnotationInStereotypes(Class<T> annotationClass, Method method, Class<?> clazz)
    {
        Map<Class<? extends Annotation>, Set<Annotation>> stereotypes = Stereotypes.getInstance().getStereotypes();

        if (stereotypes.isEmpty())
        {
            return null;
        }

        List<Class<T>> resourceStereotypes = collectStereotypes(method, clazz, stereotypes);

        for (Class<T> stereotype : resourceStereotypes)
        {
            T annotation = Utils.getAnnotation(annotationClass, stereotypes.get(stereotype));
            if (annotation != null)
            {
                return annotation;
            }
        }

        return null;
    }

    @SuppressWarnings(value = "unchecked")
    private <T extends Annotation> List<Class<T>> collectStereotypes(Method method, Class<?> clazz, Map<Class<? extends Annotation>, Set<Annotation>> stereotypes)
    {
        List<Annotation> annotations = new ArrayList<>();

        if (method != null)
        {
            annotations.addAll(Arrays.asList(method.getAnnotations()));
            annotations.addAll(Arrays.asList(method.getDeclaringClass().getAnnotations()));
        }
        if (clazz != null)
        {
            annotations.addAll(Arrays.asList(clazz.getAnnotations()));
        }

        List<Class<T>> resourceStereotypes = new ArrayList<>();

        for (Annotation annotation : annotations)
        {
            if (stereotypes.containsKey(annotation.annotationType()))
            {
                resourceStereotypes.add((Class<T>) annotation.annotationType());
            }
        }

        return resourceStereotypes;
    }
}