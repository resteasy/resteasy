package org.jboss.resteasy.cdi;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This ConstructorInjector implementation uses CDI's BeanManager to obtain
 * a contextual instance of a bean.
 *
 * @author Jozef Hartinger
 */
public class CdiConstructorInjector implements ConstructorInjector {
   private final BeanManager manager;
   private final Type type;
   private final Bean<?> bean;
   private final boolean applicationScoped;

   private static final Logger log = Logger.getLogger(CdiConstructorInjector.class);

   public CdiConstructorInjector(Type type, BeanManager manager) {
      this.type = type;
      this.manager = manager;
      Set<Bean<?>> beans = manager.getBeans(type);

      if (beans.size() > 1) {
         Set<Bean<?>> modifiableBeans = new HashSet<Bean<?>>();
         modifiableBeans.addAll(beans);
         // Ambiguous dependency may occur if a resource has subclasses
         // Therefore we remove those beans
         for (Iterator<Bean<?>> iterator = modifiableBeans.iterator(); iterator.hasNext(); ) {
            Bean<?> bean = iterator.next();
            if (!bean.getBeanClass().equals(type) && !bean.isAlternative()) {
               // remove Beans that have clazz in their type closure but not as a base class
               iterator.remove();
            }
         }
         beans = modifiableBeans;
      }

      log.debug("Beans found for {0} : {1}", type, beans);

      bean = manager.resolve(beans);
      applicationScoped = Application.class.isAssignableFrom(bean.getBeanClass()) || bean.getBeanClass().isAnnotationPresent(Provider.class);
   }

   public Object construct() {

      CreationalContext<?> context = manager.createCreationalContext(bean);
      Object reference =  manager.getReference(bean, type, context);
      if(applicationScoped) {
         JaxrsCdiLifecycleListener.addApplicationScopedObject(context);
      } else {
         JaxrsCdiLifecycleListener.addObject(context);
      }
      return reference;
   }

   public Object construct(HttpRequest request, HttpResponse response) throws Failure, WebApplicationException, ApplicationException {
      return construct();
   }

   public Object[] injectableArguments() {
      return new Object[0];
   }

   public Object[] injectableArguments(HttpRequest request, HttpResponse response) throws Failure {
      return injectableArguments();
   }
}
