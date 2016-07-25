package org.jboss.resteasy.test.cdi.extensions.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.test.cdi.util.Utilities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

/**
 * A BostonBean is just like other beans, only much, much better.
 *
 * BostonBeans are handled by the CDI extension BostonBeanExtension, and are implemented by classes
 * annotated with @Boston.
 */
public class CDIExtensionsBostonBean<T> implements Bean<T> {
    private static Logger log = Logger.getLogger(CDIExtensionsBostonBean.class);

    private Class<T> clazz;
    private String className;
    private InjectionTarget<T> injectionTarget;

    private Set<Type> types;
    private Set<Annotation> qualifiers;
    private Class<? extends Annotation> scope;
    private Set<InjectionPoint> injectionPoints;

    public CDIExtensionsBostonBean(final Class<T> clazz, final InjectionTarget<T> injectionTarget) {
        this.clazz = clazz;
        this.className = clazz.getSimpleName();
        this.injectionTarget = injectionTarget;
        types = Utilities.getTypeClosure(clazz);
        qualifiers = Utilities.getQualifiers(clazz);
        injectionPoints = injectionTarget.getInjectionPoints();
        scope = Utilities.getScopeAnnotation(clazz);
        if (scope == null) {
            if (Utilities.isAnnotationPresent(clazz, Path.class)) {
                scope = RequestScoped.class;
            } else if (Utilities.isAnnotationPresent(clazz, Provider.class)) {
                scope = ApplicationScoped.class;
            } else {
                scope = RequestScoped.class;
            }
        }
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        log.info("BostonBean[" + className + "].create()");
        T instance = injectionTarget.produce(creationalContext);
        log.info("BostonBean[" + className + "].create() raw instance: " + instance);
        injectionTarget.inject(instance, creationalContext);
        injectionTarget.postConstruct(instance);
        log.info("BostonBean[" + className + "].create(): cooked instance: " + instance);
        return instance;
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        log.info("BostonBean[" + className + "].destroy()");
        creationalContext.release();
    }

    @Override
    public Set<Type> getTypes() {
        log.info("BostonBean[" + className + "].getTypes()");
        return types;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        log.info("BostonBean[" + className + "].getQualifiers()");
        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        log.info("BostonBean[" + className + "].getScope()");
        return scope;
    }

    @Override
    public String getName() {
        log.info("BostonBean[" + className + "].getName()");
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        log.info("BostonBean[" + className + "].getStereotypes()");
        return new HashSet<Class<? extends Annotation>>();
    }

    @Override
    public Class<?> getBeanClass() {
        log.info("BostonBean[" + className + "].getBeanClass()");
        return clazz;
    }

    @Override
    public boolean isAlternative() {
        log.info("BostonBean[" + className + "].isAlternative()");
        return false;
    }

    @Override
    public boolean isNullable() {
        log.info("BostonBean[" + className + "].isNullable()");
        return false;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        log.info("BostonBean[" + className + "].getInjectionPoints()");
        return injectionPoints;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("BostonBean[").append(clazz).append('\r').
                append("            scope: ").append(scope).append('\r').
                append("            types: ");
        Iterator<Type> it1 = types.iterator();
        while (it1.hasNext()) {
            sb.append(it1.next()).append('\r').
                    append("                   ");
        }
        sb.append('\r').
                append("       qualifiers: ");
        Iterator<Annotation> it2 = qualifiers.iterator();
        while (it2.hasNext()) {
            sb.append(it2.next()).append('\r').
                    append("                   ");
        }
        sb.append('\r').
                append(" injection points: ");
        Iterator<InjectionPoint> it3 = getInjectionPoints().iterator();
        while (it3.hasNext()) {
            sb.append(it3.next()).append('\r').
                    append("                   ");
        }
        sb.append('\r').
                append("]");
        return sb.toString();
    }
}
