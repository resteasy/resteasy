package org.jboss.resteasy.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.decorator.Decorator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.InjectionTarget;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.ProcessBean;
import jakarta.enterprise.inject.spi.ProcessInjectionTarget;
import jakarta.enterprise.inject.spi.ProcessSessionBean;
import jakarta.enterprise.inject.spi.WithAnnotations;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.Provider;
import org.jboss.resteasy.cdi.i18n.LogMessages;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.util.GetRestful;

/**
 * This Extension handles default scopes for discovered JAX-RS components. It
 * also observes ProcessInjectionTarget event and wraps InjectionTargets
 * representing JAX-RS components within JaxrsInjectionTarget. Furthermore, it
 * builds the sessionBeanInterface map which maps Session Bean classes to a
 * local interface. This map is used in CdiInjectorFactory during lookup of
 * Sesion Bean JAX-RS components.
 *
 * @author Jozef Hartinger
 *
 */
public class ResteasyCdiExtension implements Extension
{
   private static boolean active;

   private BeanManager beanManager;
   private static final String JAKARTA_EJB_STATELESS = "jakarta.ejb.Stateless";
   private static final String JAKARTA_EJB_SINGLETON = "jakarta.ejb.Singleton";

   private final List<Class> providers = new ArrayList<Class>();
   private final List<Class> resources = new ArrayList<Class>();

   // Scope literals
   public static final Annotation requestScopedLiteral = new AnnotationLiteral<RequestScoped>()
   {
      private static final long serialVersionUID = 3381824686081435817L;
   };
   public static final Annotation applicationScopedLiteral = new AnnotationLiteral<ApplicationScoped>()
   {
      private static final long serialVersionUID = -8211157243671012820L;
   };

   public static boolean isCDIActive()
   {
      return active;
   }

   private Map<Class<?>, Type> sessionBeanInterface = new HashMap<Class<?>, Type>();
   private boolean generateClientBean = true;

   /**
    * Obtain BeanManager reference for future use.
    *
    * @param event event
    * @param beanManager bean manager
    */
   public void observeBeforeBeanDiscovery(@Observes BeforeBeanDiscovery event, BeanManager beanManager)
   {
      this.beanManager = beanManager;
      final AnnotatedType<ContextProducers> producersAnnotatedType = beanManager.createAnnotatedType(ContextProducers.class);
      event.addAnnotatedType(producersAnnotatedType, ContextProducers.class.getCanonicalName());
      active = true;
   }

   /**
    * Process any client beans.
    *
    * @param pb the bean being processed
    */
   public void processClientBean(@Observes final ProcessBean<?> pb) {
      // this method will get notified if there is any bean of type `Client` created
      if (pb.getBean().getTypes().contains(Client.class)) {
         final Set<Annotation> qualifiers = pb.getBean()
                 .getQualifiers(); // you want to detect beans with no explicit qualifiers
         if (qualifiers.contains(Any.Literal.INSTANCE) && qualifiers.contains(Default.Literal.INSTANCE)) {
            generateClientBean = false;
         }
      }
   }


   /**
    * Registers a producer and disable for a {@link Client REST client}.
    *
    * @param event       the after bean discovery event
    * @param beanManager the bean manager
    */
   public void registerClientProducer(@Observes final AfterBeanDiscovery event, final BeanManager beanManager) {
      if (generateClientBean) {
         event.addBean().addTransitiveTypeClosure(Client.class)
                 .scope(ApplicationScoped.class)
                 .produceWith(instance -> ClientBuilder.newClient(RegisterBuiltin.getClientInitializedResteasyProviderFactory(getClassLoader())))
                 .disposeWith((client, instance) -> client.close());
      }
   }

   /**
    * Set a default scope for each CDI bean which is a JAX-RS Resource.
    *
    * @param <T> type
    * @param event event
    * @param beanManager bean manager
    */
   public <T> void observeResources(@WithAnnotations({Path.class}) @Observes ProcessAnnotatedType<T> event, BeanManager beanManager)
   {
      setBeanManager(beanManager);
      AnnotatedType<T> annotatedType = event.getAnnotatedType();

      if(!annotatedType.getJavaClass().isInterface()
               && !isSessionBean(annotatedType)
               // This check is redundant for CDI 1.1 containers but required for CDI 1.0
               && GetRestful.isRootResource(annotatedType.getJavaClass())
               && !annotatedType.isAnnotationPresent(Decorator.class))
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.discoveredCDIBeanJaxRsResource(annotatedType.getJavaClass().getCanonicalName()));
         event.setAnnotatedType(wrapAnnotatedType(annotatedType, requestScopedLiteral));
         this.resources.add(annotatedType.getJavaClass());
      }
   }

   /**
    * Set a default scope for each CDI bean which is a JAX-RS Provider.
    *
    * @param <T> type
    * @param event event
    * @param beanManager bean manager
    */
   public <T> void observeProviders(@WithAnnotations({Provider.class}) @Observes ProcessAnnotatedType<T> event, BeanManager beanManager)
   {
      setBeanManager(beanManager);
      AnnotatedType<T> annotatedType = event.getAnnotatedType();

      if(!annotatedType.getJavaClass().isInterface()
               && !isSessionBean(annotatedType)
               // This check is redundant for CDI 1.1 containers but required for CDI 1.0
         && annotatedType.isAnnotationPresent(Provider.class)
         && !isUnproxyableClass(annotatedType.getJavaClass()))
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.discoveredCDIBeanJaxRsProvider(annotatedType.getJavaClass().getCanonicalName()));
         event.setAnnotatedType(wrapAnnotatedType(annotatedType, applicationScopedLiteral));
         this.providers.add(annotatedType.getJavaClass());
      }
   }

   /**
    * Set a default scope for each CDI bean which is a JAX-RS Application subclass.
    *
    * @param <T> type
    * @param event event
    * @param beanManager bean manager
    */
   public <T extends Application> void observeApplications(@Observes ProcessAnnotatedType<T> event, BeanManager beanManager)
   {
      setBeanManager(beanManager);
      AnnotatedType<T> annotatedType = event.getAnnotatedType();

      if(!isSessionBean(annotatedType))
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.discoveredCDIBeanApplication(annotatedType.getJavaClass().getCanonicalName()));
         event.setAnnotatedType(wrapAnnotatedType(annotatedType, applicationScopedLiteral));
      }
   }

   protected <T> AnnotatedType<T> wrapAnnotatedType(AnnotatedType<T> type, Annotation scope)
   {
      if (Utils.isScopeDefined(type, beanManager))
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.beanHasScopeDefined(type.getJavaClass()));
         return type; // leave it as it is
      }
      else
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.beanDoesNotHaveScopeDefined(type.getJavaClass(), scope));
         return new JaxrsAnnotatedType<T>(type, scope);
      }
   }

   /**
    * Wrap InjectionTarget of JAX-RS components within JaxrsInjectionTarget
    * which takes care of JAX-RS property injection.
    *
    * @param <T> type
    * @param event event
    */
   public <T> void observeInjectionTarget(@Observes ProcessInjectionTarget<T> event)
   {
      if (event.getAnnotatedType() == null)
      { // check for resin's bug http://bugs.caucho.com/view.php?id=3967
         LogMessages.LOGGER.warn(Messages.MESSAGES.annotatedTypeNull());
         return;
      }

      if (Utils.isJaxrsComponent(event.getAnnotatedType().getJavaClass()))
      {
         event.setInjectionTarget(wrapInjectionTarget(event));
      }
   }

   protected <T> InjectionTarget<T> wrapInjectionTarget(ProcessInjectionTarget<T> event)
   {
      return new JaxrsInjectionTarget<T>(event.getInjectionTarget(), event.getAnnotatedType().getJavaClass());
   }

   /**
    * Observes ProcessSessionBean events and creates a (Bean class {@literal ->} Local
    * interface) map for Session beans with local interfaces. This map is
    * necessary since RESTEasy identifies a bean class as JAX-RS components
    * while CDI requires a local interface to be used for lookup.
    *
    * @param <T> type
    * @param event event
    *
    */
   public <T> void observeSessionBeans(@Observes ProcessSessionBean<T> event)
   {
      Bean<Object> sessionBean = event.getBean();

      if (Utils.isJaxrsComponent(sessionBean.getBeanClass()))
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
            Class<?> clazz = (Class<?>) type;
            if (Utils.isJaxrsAnnotatedClass(clazz))
            {
               sessionBeanInterface.put(bean.getBeanClass(), type);
               LogMessages.LOGGER.debug(Messages.MESSAGES.typeWillBeUsedForLookup(type, bean.getBeanClass()));
               return;
            }
         }
      }
      LogMessages.LOGGER.debug(Messages.MESSAGES.noLookupInterface(bean.getBeanClass()));
   }

   public Map<Class<?>, Type> getSessionBeanInterface()
   {
      return sessionBeanInterface;
   }

   private boolean isSessionBean(AnnotatedType<?> annotatedType)
   {
      for (Annotation annotation : annotatedType.getAnnotations())
      {
         Class<?> annotationType = annotation.annotationType();
         if (annotationType.getName().equals(JAKARTA_EJB_STATELESS) || annotationType.getName().equals(JAKARTA_EJB_SINGLETON))
         {
            LogMessages.LOGGER.debug(Messages.MESSAGES.beanIsSLSBOrSingleton(annotatedType.getJavaClass()));
            return true; // Do not modify scopes of SLSBs and Singletons
         }
      }
      return false;
   }

   private void setBeanManager(BeanManager beanManager)
   {
      if (this.beanManager == null) {
         // this may happen if Solder Config receives BBD first
         this.beanManager = beanManager;
      }
   }

   public List<Class> getProviders()
   {
      return providers;
   }

   public List<Class> getResources()
   {
      return resources;
   }

   /**
    * Check for select case of unproxyable bean type.
    * (see CDI 2.0 spec, section 3.11)
    * @param clazz
    * @return
    */
   private boolean isUnproxyableClass(Class clazz) {
      // Unproxyable bean type: classes which are declared final,
      // or expose final methods,
      // or have no non-private no-args constructor
      return isFinal(clazz) ||
            hasNonPrivateNonStaticFinalMethod(clazz) ||
            hasNoNonPrivateNoArgsConstructor(clazz);
   }

   private boolean isFinal(Class clazz) {
      return Modifier.isFinal(clazz.getModifiers());
   }

   // Adapted from weld-core-impl:3.0.5.Final's Reflections.getNonPrivateNonStaticFinalMethod()
   private boolean hasNonPrivateNonStaticFinalMethod(Class<?> type) {
      for (Class<?> clazz = type; clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
         for (Method method : clazz.getDeclaredMethods()) {
            if (isFinal(method) && !isPrivate(method) && !isStatic(method)) {
               return true;
            }
         }
      }
      return false;
   }

   private boolean hasNoNonPrivateNoArgsConstructor(Class<?> clazz) {
      Constructor<?> constructor;
      try {
         constructor = clazz.getConstructor();
      } catch (NoSuchMethodException exception) {
         return true;
      }

      // Note: this probably can only be private if the provider also has
      // a non-private @Context constructor, which is unlikely but possible.
      return isPrivate(constructor);
   }

   private boolean isFinal(Member member) {
      return Modifier.isFinal(member.getModifiers());
   }

   private boolean isPrivate(Member member) {
      return Modifier.isPrivate(member.getModifiers());
   }

   private boolean isStatic(Member member) {
      return Modifier.isStatic(member.getModifiers());
   }

   private static ClassLoader getClassLoader() {
      if (System.getSecurityManager() == null) {
         ClassLoader result = Thread.currentThread().getContextClassLoader();
         if (result == null) {
            result = ResteasyCdiExtension.class.getClassLoader();
         }
         return result;
      }
      return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> {
         ClassLoader result = Thread.currentThread().getContextClassLoader();
         if (result == null) {
            result = ResteasyCdiExtension.class.getClassLoader();
         }
         return result;
      });
   }
}
