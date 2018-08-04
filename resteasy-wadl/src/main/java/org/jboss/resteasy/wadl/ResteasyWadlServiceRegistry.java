package org.jboss.resteasy.wadl;

import org.jboss.resteasy.spi.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.util.GetRestful;
import org.jboss.resteasy.wadl.i18n.LogMessages;
import org.jboss.resteasy.wadl.i18n.Messages;

import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class ResteasyWadlServiceRegistry {

   private ResourceMethodRegistry registry;

   public ResteasyProviderFactory getProviderFactory() {
      return providerFactory;
   }

   private ResteasyProviderFactory providerFactory;

   private ResteasyWadlServiceRegistry parent;

   private Map<String, ResteasyWadlResourceMetaData> resources;

   private List<ResteasyWadlServiceRegistry> locators;

   private ResourceLocator locator;

   private String uri;

   private String functionPrefix;

   public ResteasyWadlServiceRegistry(ResteasyWadlServiceRegistry parent, ResourceMethodRegistry registry,
                                       ResteasyProviderFactory providerFactory, ResourceLocator locator) {
      this.parent = parent;
      this.registry = registry;
      this.providerFactory = providerFactory;
      this.locator = locator;
      if (locator != null) {
         Method method = locator.getMethod();
         Path methodPath = method.getAnnotation(Path.class);
         Class<?> declaringClass = method.getDeclaringClass();
         Path classPath = declaringClass.getAnnotation(Path.class);
         this.uri = ResteasyWadlMethodMetaData.appendURIFragments(parent, classPath, methodPath);
         if (parent.isRoot())
            this.functionPrefix = declaringClass.getSimpleName() + "." + method.getName();
         else
            this.functionPrefix = parent.getFunctionPrefix() + "." + method.getName();
      }
      scanRegistry();
   }

   private void scanRegistry() {
      resources = new HashMap<>();

      locators = new ArrayList<>();

      for (Map.Entry<String, List<ResourceInvoker>> entry : registry.getBounded().entrySet()) {
         List<ResourceInvoker> invokers = entry.getValue();
         for (ResourceInvoker invoker : invokers) {
            if (invoker instanceof ResourceMethodInvoker) {
               ResteasyWadlMethodMetaData methodMetaData = new ResteasyWadlMethodMetaData(this, (ResourceMethodInvoker) invoker);
               ResteasyWadlResourceMetaData resourceMetaData = resources.get(methodMetaData.getKlassUri());
               if (resourceMetaData == null) {
                  resourceMetaData = new ResteasyWadlResourceMetaData(methodMetaData.getKlassUri());
                  resources.put(methodMetaData.getKlassUri(), resourceMetaData);
               }
               resourceMetaData.addMethodMetaData(methodMetaData);
            } else if (invoker instanceof ResourceLocator) {
               ResourceLocator locator = (ResourceLocator) invoker;
               Method method = locator.getMethod();
               Class<?> locatorType = method.getReturnType();
               Class<?>[] locatorResourceTypes = GetRestful.getSubResourceClasses(locatorType);
               for (Class<?> locatorResourceType : locatorResourceTypes) {
                  if (locatorResourceType == null) {
                     // FIXME: we could generate an error for the client, which would be more informative than
                     // just logging this
                     LogMessages.LOGGER.warn(Messages.MESSAGES.impossibleToGenerateWADL(method.getDeclaringClass().getName(), method.getName())); // skip this
                     continue;
                  }
                  ResourceMethodRegistry locatorRegistry = new ResourceMethodRegistry(providerFactory);
                  locatorRegistry.addResourceFactory(null, null, locatorResourceType);
                  locators.add(new ResteasyWadlServiceRegistry(this, locatorRegistry, providerFactory, locator));
               }
            }
         }
      }
   }

   public Map<String, ResteasyWadlResourceMetaData> getResources() {
      return resources;
   }

   public List<ResteasyWadlServiceRegistry> getLocators() {
      return locators;
   }

   public String getUri() {
      return uri;
   }

   public boolean isRoot() {
      return parent == null;
   }

   public String getFunctionPrefix() {
      return functionPrefix;
   }

   public void collectResourceMethodsUntilRoot(List<Method> methods) {
      if (isRoot())
         return;
      methods.add(locator.getMethod());
      parent.collectResourceMethodsUntilRoot(methods);
   }

}
