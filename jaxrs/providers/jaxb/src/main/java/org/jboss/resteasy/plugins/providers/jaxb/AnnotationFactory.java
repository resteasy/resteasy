package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.annotations.providers.jaxb.WrappedMap;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AnnotationFactory
{
   public static Annotation[] createArray(final String elementName, final String namespace, final String prefix)
   {
      Annotation[] ann = {create(elementName, namespace, prefix)};
      return ann;
   }

   public static Wrapped create(final String elementName, final String namespace, final String prefix)
   {
      return new Wrapped()
      {
         public String element()
         {
            return elementName == null ? "" : elementName;
         }

         public String namespace()
         {
            return namespace == null ? "" : namespace;
         }

         public String prefix()
         {
            return prefix == null ? "" : prefix;
         }

         public Class<? extends Annotation> annotationType()
         {
            return Wrapped.class;
         }
      };
   }

   public static Annotation[] createArray(final String map, final String entry, final String key, final String namespace, final String prefix)
   {
      Annotation[] ann = {create(map, entry, key, namespace, prefix)};
      return ann;
   }

   public static WrappedMap create(final String map, final String entry, final String key, final String namespace, final String prefix)
   {
      return new WrappedMap()
      {
         public String map()
         {
            return map == null ? "" : map;
         }

         public String entry()
         {
            return entry == null ? "" : entry;
         }

         public String key()
         {
            return key == null ? "" : key;
         }

         public String namespace()
         {
            return namespace == null ? "" : namespace;
         }

         public String prefix()
         {
            return prefix == null ? "" : prefix;
         }

         public Class<? extends Annotation> annotationType()
         {
            return WrappedMap.class;
         }
      };
   }
}
