package org.jboss.resteasy.plugins.providers.jaxb;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlType;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.HashSet;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractJAXBContextFinder implements JAXBContextFinder
{
   protected static final String OBJECT_FACTORY_NAME = ".ObjectFactory";
   @Context
   protected Providers providers;

   public static class CacheKey
   {
      private Class[] classes;
      private int hashCode;

      public CacheKey(Class[] classes)
      {
         this.classes = classes;
         for (Class clazz : classes) hashCode += clazz.hashCode();
      }

      @Override
      public boolean equals(Object o)
      {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;

         CacheKey cacheKey = (CacheKey) o;

         if (hashCode != cacheKey.hashCode) return false;
         if (classes.length != cacheKey.classes.length) return false;

         for (Class clazz : classes)
         {
            boolean found = false;
            for (Class compare : cacheKey.classes)
            {
               if (compare.equals(clazz))
               {
                  found = true;
                  break;
               }
            }
            if (!found) return false;
         }
         return true;
      }

      @Override
      public int hashCode()
      {
         return hashCode;
      }
   }

   public JAXBContext findProvidedJAXBContext(Class<?> type, MediaType mediaType)
           throws JAXBException
   {
      JAXBContext jaxb = null;
      ContextResolver<JAXBContext> resolver = providers.getContextResolver(JAXBContext.class, mediaType);
      if (resolver != null)
      {
         jaxb = resolver.getContext(type);
         if (jaxb != null) return jaxb;
      }
      return jaxb;
   }

   public static String getPackageName(Class<?> type)
   {
      String packageName;
      int packageSeparator = type.getName().lastIndexOf('.');
      if (packageSeparator != -1)
      {
         packageName = type.getName().substring(0, packageSeparator);
      }
      else
      {
         packageName = type.getName();
      }
      return packageName;
   }

   public static Class<?> findDefaultObjectFactoryClass(Class<?> type)
   {
      XmlType typeAnnotation = type.getAnnotation(XmlType.class);
      if (typeAnnotation == null) return null;
      if (!typeAnnotation.factoryClass().equals(XmlType.DEFAULT.class)) return null;
      final StringBuilder b = new StringBuilder(getPackageName(type));
      b.append(OBJECT_FACTORY_NAME);
      Class<?> factoryClass;
      try
      {
         if (System.getSecurityManager() == null)
         {
            factoryClass = Thread.currentThread().getContextClassLoader().loadClass(b.toString());
         }
         else
         {
            factoryClass = AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>()
            {
               @Override
               public Class<?> run() throws ClassNotFoundException
               {
                  return Thread.currentThread().getContextClassLoader().loadClass(b.toString());
               }
            });
         }
      }
      catch (PrivilegedActionException | ClassNotFoundException e)
      {
         return null;
      }
      if (factoryClass.isAnnotationPresent(XmlRegistry.class)) return factoryClass;
      return null;
   }

   protected abstract JAXBContext createContextObject(Annotation[] parameterAnnotations, Class... classes) throws JAXBException;
   protected abstract JAXBContext createContextObject(Annotation[] parameterAnnotations, String contextPath) throws JAXBException;

   public JAXBContext createXmlTypeContext(Annotation[] parameterAnnotations, Class... classes) throws JAXBException
   {

      HashSet<String> packages = new HashSet<String>();
      for (Class type : classes)
      {
         packages.add(type.getPackage().getName());
      }
      boolean first = true;
      StringBuilder contextPath = new StringBuilder();
      for (String pkg : packages)
      {
         if (first) first = false;
         else contextPath.append(':');
         contextPath.append(pkg);
      }
      return createContextObject(parameterAnnotations, contextPath.toString());
   }

	@Override
	public JAXBContext createContext(Annotation[] parameterAnnotations, Class... classes) throws JAXBException
 {
		Set<Class<?>> classes1 = Collections.emptySet();
		if (classes != null && classes.length != 0) {
			classes1 = new HashSet<Class<?>>();
			for (Class<?> type : classes) {
				if (type == null)
					continue;
				classes1.add(type);
               Class<?> factory = findDefaultObjectFactoryClass(type);
               if (factory != null)
                  classes1.add(factory);
            }
		}
		Class<?>[] classArray = classes1.toArray(new Class[classes1.size()]);
		return createContextObject(parameterAnnotations, classArray);
	}

   public JAXBContextFinder getContext(Class<?> type)
   {
      return this;
   }
}
