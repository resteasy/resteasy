package org.jboss.resteasy.plugins.validation.cdi;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Set;

import javax.decorator.Decorator;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.util.GetRestful;

/**
 * 
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 7, 2014
 */
public class ResteasyValidationCdiExtension implements Extension
{
   public static final Annotation requestScopedLiteral = new AnnotationLiteral<RequestScoped>()
   {
      private static final long serialVersionUID = 3381824686081435817L;
   };
   
   private final Logger log = Logger.getLogger(ResteasyValidationCdiExtension.class);
   private BeanManager beanManager;

   /**
    * Obtain BeanManager reference for future use.
    */
   public void observeBeforeBeanDiscovery(@Observes BeforeBeanDiscovery event, BeanManager beanManager)
   {
      this.beanManager = beanManager;
   }
   
   /**
    * Set a default scope for each CDI bean which is a JAX-RS Resource.
    *
    * @param event
    * @param beanManager
    */
   public <T> void observeResources(@Observes ProcessAnnotatedType<T> event, BeanManager beanManager)
   {
       AnnotatedType<T> annotatedType = event.getAnnotatedType();
       if(!annotatedType.getJavaClass().isInterface()
               // This check is redundant for CDI 1.1 containers but required for CDI 1.0
               && GetRestful.isRootResource(annotatedType.getJavaClass())
               && !annotatedType.isAnnotationPresent(Decorator.class)) // ?????
       {
           log.debug("Discovered CDI bean which is a JAX-RS resource {0}.", annotatedType.getJavaClass().getCanonicalName());
           event.setAnnotatedType(wrapAnnotatedType(annotatedType));
           AnnotatedType<?> at = event.getAnnotatedType();
           Set<Annotation> as =  at.getAnnotations();
           for (Iterator<Annotation> it = as.iterator(); it.hasNext(); )
           {
              Annotation a = it.next();
              log.info(a.annotationType().getName());
           }
       }
   }
   
   protected <T> AnnotatedType<T> wrapAnnotatedType(AnnotatedType<T> type)
   {
      log.debug("Adding @ResteasyCdiValidationAnnotation to bean {0}.", type.getJavaClass());
      return new ResteasyValidationCdiAnnotatedType<T>(type);
   }
}
