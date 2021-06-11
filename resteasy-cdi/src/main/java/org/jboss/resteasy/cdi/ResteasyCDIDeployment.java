package org.jboss.resteasy.cdi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Data object containing CDI related data for Resteasy deployment
 */
public class ResteasyCDIDeployment
{
    private final Map<Class<? extends Annotation>, Set<Annotation>> stereotypes = new HashMap<>();
    private List<Class<?>> resourceClasses = new ArrayList<>();
    private List<Class<?>> providerClasses = new ArrayList<>();

    public void addStereotype(Class<? extends Annotation> stereotypeClass, Set<Annotation> annotations)
    {
        stereotypes.put(stereotypeClass, annotations);
    }

    public Map<Class<? extends Annotation>, Set<Annotation>> getStereotypes()
    {
        return Collections.unmodifiableMap(stereotypes);
    }

    void addResource(Class<?> clazz)
    {
        resourceClasses.add(clazz);
    }

    void addProvider(Class<?> clazz)
    {
        providerClasses.add(clazz);
    }

    List<Class<?>> getResourceClasses()
    {
        return Collections.unmodifiableList(resourceClasses);
    }

    List<Class<?>> getProviderClasses()
    {
        return Collections.unmodifiableList(providerClasses);
    }

    void close()
    {
        resourceClasses = new ArrayList<>();
        providerClasses = new ArrayList<>();
    }
}