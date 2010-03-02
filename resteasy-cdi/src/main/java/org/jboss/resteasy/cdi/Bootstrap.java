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
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.inject.spi.ProcessSessionBean;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Scope;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Extension prepares and registers CdiInjectorFactory with RESTEasy. It also handles default
 * scopes for discovered JAX-RS components. Furthermore, this Extension observes ProcessInjectionTarget
 * event and wraps InjectionTargets representing JAX-RS components within JaxrsInjectionTarget. 
 * 
 * @author Jozef Hartinger
 * 
 */
public class Bootstrap implements Extension
{
   private final Logger log = LoggerFactory.getLogger(Bootstrap.class);
   private InjectorFactory injectorFactoryDelegate;
   private CdiInjectorFactory injectorFactory;
   
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
    * This method prepares CdiInjectorFactory instance which is needed during 
    * ProcessInjectionTarget phase and at runtime. 
    */
   public void observeBeforeBeanDiscovery(@Observes BeforeBeanDiscovery event)
   {
      log.debug("Creating CdiInjectorFactory.");
      injectorFactoryDelegate = ResteasyProviderFactory.getInstance().getInjectorFactory();
      injectorFactory = new CdiInjectorFactory(injectorFactoryDelegate);
   }
   
   /**
    * Register CdiInjectorFactory with RESTEasy
    * 
    */
   public void observeAfterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager manager)
   {
      log.debug("Registering CdiInjectorFactory.");
      
      injectorFactory.init(sessionBeanInterface, manager);
      ResteasyProviderFactory.getInstance().setInjectorFactory(injectorFactory);
   }

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
         Provider providerAnnotation = type.getAnnotation(Provider.class);

         if (providerAnnotation != null)
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
      if (isJaxrsComponent(event.getAnnotatedType().getJavaClass()))
      {
         event.setInjectionTarget(wrapInjectionTarget(event));
      }
   }
   
   protected <T> InjectionTarget<T> wrapInjectionTarget(ProcessInjectionTarget<T> event)
   {
      PropertyInjector propertyInjector = injectorFactoryDelegate.createPropertyInjector(event.getAnnotatedType().getJavaClass());
      return new JaxrsInjectionTarget<T>(event.getInjectionTarget(), propertyInjector);
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
}
