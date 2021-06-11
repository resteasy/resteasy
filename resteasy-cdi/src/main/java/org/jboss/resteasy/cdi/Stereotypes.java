package org.jboss.resteasy.cdi;

import org.jboss.logging.Logger;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyDeploymentObserver;
import org.jboss.resteasy.spi.util.AnnotationResolver;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the collected information about JAX-RS annotations in
 * {@link javax.enterprise.inject.Stereotype} definitions per deployment
 * based on the CDI runtime
 */
public class Stereotypes implements ResteasyDeploymentObserver
{
    private static final String JAVAX_WS_RS = "javax.ws.rs";
    private static final Logger log = Logger.getLogger(Stereotypes.class);

    private Map<Integer, ResteasyCDIDeployment> cdiDeployments = new HashMap<>();

    private Stereotypes() {
        AnnotationResolver.setInstance(new CDIAnnotationResolver());
        ResteasyDeploymentImpl.registerObserver(this);
    }

    private static final Stereotypes instance = new Stereotypes();

    public static Stereotypes getInstance()
    {
        return instance;
    }

    public void addStereotype(Class<? extends Annotation> stereotypeClass, Class<?> clazz, BeanManager beanManager)
    {
        ResteasyCDIDeployment cdiDeployment = getCDIDeployment();
        Set<Annotation> jaxRsAnnotations = collectJaxRsAnnotations(beanManager.getStereotypeDefinition(stereotypeClass), beanManager);
        if (cdiDeployment != null)
        {
            if (Utils.getAnnotation(Provider.class, jaxRsAnnotations) != null && clazz == null){
                throw new IllegalStateException(Messages.MESSAGES.providerStereotypeOnMethod(stereotypeClass.getCanonicalName()));
            }
            if (Utils.getAnnotation(Path.class, jaxRsAnnotations) != null && clazz != null)
            {
                cdiDeployment.addResource(clazz);
            } else if (Utils.getAnnotation(Provider.class, jaxRsAnnotations) != null && clazz != null)
            {
                cdiDeployment.addProvider(clazz);
            }
            cdiDeployment.addStereotype(stereotypeClass, jaxRsAnnotations);
        }
    }

    private ResteasyCDIDeployment getCDIDeployment()
    {
        Integer key = getCDIKey();

        if (key == null)
        {
            return null;
        }

        return cdiDeployments.computeIfAbsent(key, k -> new ResteasyCDIDeployment());
    }

    private static Set<Annotation> collectJaxRsAnnotations(Set<Annotation> annotations, BeanManager beanManager)
    {
        Set<Annotation> jaxRsAnnotations = new HashSet<>();

        for (Annotation annotation : annotations)
        {
            if (isJaxRsAnnotation(annotation))
            {
                jaxRsAnnotations.add(annotation);
            } else if (beanManager.isStereotype(annotation.annotationType()))
            {
                Set<Annotation> stereotypeDefinition = beanManager.getStereotypeDefinition(annotation.annotationType());
                jaxRsAnnotations.addAll(collectJaxRsAnnotations(stereotypeDefinition, beanManager));
            }
        }

        return jaxRsAnnotations;
    }

    private static boolean isJaxRsAnnotation(Annotation annotation)
    {
        return annotation.annotationType().getName().startsWith(JAVAX_WS_RS);
    }

    public Map<Class<? extends Annotation>, Set<Annotation>> getStereotypes()
    {
        ResteasyCDIDeployment cdiDeployment = getCDIDeployment();
        return (cdiDeployment != null) ? Collections.unmodifiableMap(cdiDeployment.getStereotypes()) : Collections.emptyMap();
    }

    @Override
    public void start(ResteasyDeployment deployment)
    {
        ResteasyCDIDeployment cdiDeployment = getCDIDeployment();

        if (cdiDeployment == null)
        {
            return;
        }

        List<Class<?>> resourceClasses = cdiDeployment.getResourceClasses();
        if (resourceClasses != null)
        {
            for (Class<?> resource : resourceClasses)
            {
                deployment.getRegistry().addPerRequestResource(resource);
            }
        }

        List<Class<?>> providerClasses = cdiDeployment.getProviderClasses();
        if (providerClasses != null)
        {
            for (Class<?> provider : providerClasses)
            {
                deployment.getProviderFactory().registerProvider(provider);
            }
        }
    }

    @Override
    public void stop(ResteasyDeployment deployment)
    {
        ResteasyCDIDeployment resteasyCDIDeployment = cdiDeployments.get(getCDIKey());
        if (resteasyCDIDeployment != null){
            resteasyCDIDeployment.close();
            cdiDeployments.remove(resteasyCDIDeployment);
        }
    }

    private Integer getCDIKey()
    {
        try
        {
            return CDI.current() != null ? CDI.current().hashCode() : null;
        } catch (IllegalStateException e)
        {
            // CDI context is not available
            return null;
        }
    }
}