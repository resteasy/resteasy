package org.jboss.resteasy.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.inject.spi.ProcessSessionBean;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Scope;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Extension handles default scopes for discovered JAX-RS components. It also observes ProcessInjectionTarget
 * event and wraps InjectionTargets representing JAX-RS components within JaxrsInjectionTarget. Furthermore, it builds
 * the sessionBeanInterface map which maps Session Bean classes to a local interface. This map is used in CdiInjectorFactory
 * during lookup of Sesion Bean JAX-RS components.
 * 
 * @author Jozef Hartinger
 * 
 */
public class ResteasyCdiExtension implements Extension
{
   private final Logger log = LoggerFactory.getLogger(ResteasyCdiExtension.class);
   
   // Scope literals
   public static final Annotation requestScopedLiteral = new AnnotationLiteral<RequestScoped>()
   {
      private static final long serialVersionUID = 3381824686081435817L;
   };
   public static final Annotation applicationScopedLiteral = new AnnotationLiteral<ApplicationScoped>()
   {
      private static final long serialVersionUID = -8211157243671012820L;
   };
   
   private Map<Class<?>, Class<?>> sessionBeanInterface = new HashMap<Class<?>, Class<?>>();

   /**
    * Set a default scope for each CDI bean which is a JAX-RS Resource, 
    * Provider or Application subclass.
    * 
    */
   public <T> void observeResources(@Observes ProcessAnnotatedType<T> event)
   {
      AnnotatedType<T> type = event.getAnnotatedType();

      if (!type.getJavaClass().isInterface())
      {
         /**
         if (type.isAnnotationPresent(Stateless.class))
         {
            return; // Do not modify the scope of a Stateless Session Bean
         }
         **/
         if (type.isAnnotationPresent(Provider.class))
         {
            log.debug("Discovered CDI bean which is a JAX-RS provider {}.", type.getJavaClass().getCanonicalName());
            event.setAnnotatedType(wrapAnnotatedType(type, applicationScopedLiteral));
         }
         else if (GetRestful.isRootResource(type.getJavaClass()))
         {
            log.debug("Discovered CDI bean which is a JAX-RS resource {}.", type.getJavaClass().getCanonicalName());
            event.setAnnotatedType(wrapAnnotatedType(type, requestScopedLiteral));
         }
         else if (Application.class.isAssignableFrom(type.getJavaClass()))
         {
            log.debug("Discovered CDI bean which is javax.ws.rs.core.Application subclass {}.", type.getJavaClass().getCanonicalName());
            event.setAnnotatedType(wrapAnnotatedType(type, applicationScopedLiteral));
         }
      }
   }
   
   protected <T> AnnotatedType<T> wrapAnnotatedType(AnnotatedType<T> type, Annotation scope)
   {
      if (hasScopeDefined(type.getJavaClass()))
      {
         log.debug("Bean {} has a scope defined.", type.getJavaClass());
         return type; // leave it as it is
      }
      else
      {
         log.debug("Bean {} does not have the scope defined. Binding to {}.", type.getJavaClass(), scope);
         return new JaxrsAnnotatedType<T>(type, scope);
      }
   }
   
   /**
    * Wrap InjectionTarget of JAX-RS components within JaxrsInjectionTarget which
    * takes care of JAX-RS property injection.
    */
   public <T> void observeInjectionTarget(@Observes ProcessInjectionTarget<T> event)
   {
      if (event.getAnnotatedType() == null)
      {  // check for resin's bug http://bugs.caucho.com/view.php?id=3967
         log.warn("ProcessInjectionTarget.getAnnotatedType() returned null. As a result, JAX-RS property injection will not work.");
         return;
      }
      
      if (isJaxrsComponent(event.getAnnotatedType().getJavaClass()))
      {
         event.setInjectionTarget(wrapInjectionTarget(event));
      }
   }
   
   protected <T> InjectionTarget<T> wrapInjectionTarget(ProcessInjectionTarget<T> event)
   {
      return new JaxrsInjectionTarget<T>(event.getInjectionTarget(), event.getAnnotatedType().getJavaClass());
   }
   
   /**
    * Observes ProcessSessionBean events and creates a (Bean class -> Local interface) map for
    * Session beans with local interfaces. This map is necessary since RESTEasy identifies a bean 
    * class as JAX-RS components while CDI requires a local interface to be used for lookup.
    */
   public <T> void observeSessionBeans(@Observes ProcessSessionBean<T> event)
   {
      Bean<Object> sessionBean = event.getBean();
      
      if (isJaxrsComponent(sessionBean.getBeanClass()))
      {
         addSessionBeanInterface(sessionBean);
      }
   }
   
   private void addSessionBeanInterface(Bean<?> bean)
   {
      for (Type type : bean.getTypes())
      {
         if ((type instanceof Class<?>) && ((Class<?>) type).isInterface())
         {
            sessionBeanInterface.put(bean.getBeanClass(),(Class<?>) type);
            break; // TODO we only pick up the first interface
         }
      }
   }
   
   /**
    * Find out if a given class is a JAX-RS component
    * 
    * @return true if and only if a give class is a JAX-RS resource, provider
    * or javax.ws.rs.core.Application subclass.
    */
   private boolean isJaxrsComponent(Class<?> clazz)
   {
      Provider providerAnnotation = clazz.getAnnotation(Provider.class);
      return ((providerAnnotation != null) || (GetRestful.isRootResource(clazz)) || (Application.class.isAssignableFrom(clazz)));
   }
   
   /**
    * Find out if a given class has is explicitly bound to a scope.
    * 
    * @return true if and only if a given class is annotated with a scope annotation
    * or with a stereotype which (transitively) declares a scope
    */
   public boolean hasScopeDefined(Class<?> clazz)
   {
      for (Annotation annotation : clazz.getAnnotations())
      {
         if (isScope(annotation))
         {
            return true;
         }
         if (isStereotype(annotation))
         {
            if (hasScopeDefined(annotation.annotationType()))
            {
               return true;
            }
         }
      }
      return false;
   }
   
   /**
    * Find out if a given annotation is a scope.
    * @return true if and only if a given annotation is a scope
    */
   private boolean isScope(Annotation annotation)
   {
      Class<?> annotationType = annotation.annotationType();
      return ((annotationType.isAnnotationPresent(NormalScope.class)) || (annotationType.isAnnotationPresent(Scope.class)));
   }
   
   /**
    * Find out if a given annotation is a stereotype.
    * @return true if and only if a given annotation is a stereotype
    */
   private boolean isStereotype(Annotation annotation)
   {
      return annotation.annotationType().isAnnotationPresent(Stereotype.class);
   }

   public Map<Class<?>, Class<?>> getSessionBeanInterface()
   {
      return sessionBeanInterface;
   }
}
