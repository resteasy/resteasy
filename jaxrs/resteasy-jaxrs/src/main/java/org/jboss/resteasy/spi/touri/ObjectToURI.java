package org.jboss.resteasy.spi.touri;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class encapsulates how an object can be converted to a URI string. There
 * are three basic ways to perform the transformation:
 * </p>
 * <ol>
 * <li>adding a @URITemplate("your-uri-template") to an object
 * <li>having an object extend URIable and have the object perform custom logic
 * <li>registering a custom URIResolver
 * </ol>
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */

public class ObjectToURI
{

   private static ObjectToURI instance = null;

   static
   {
      instance = new ObjectToURI();
      instance.defaultResolvers.add(new URIableURIResolver());
      instance.defaultResolvers.add(new URITemplateAnnotationResolver());
      instance.defaultResolvers.add(new MappedByAnnotationResolver());
   }

   public static ObjectToURI getInstance()
   {
      return instance;
   }

   private List<URIResolver> resolvers = new ArrayList<URIResolver>();
   private List<URIResolver> defaultResolvers = new ArrayList<URIResolver>();

   public void registerURIResolver(URIResolver uriResolver)
   {
      this.resolvers.add(uriResolver);
   }

   public String resolveURI(Object object)
   {
      String result = getResolver(object, resolvers);
      if (result == null)
      {
         result = getResolver(object, defaultResolvers);
      }
      return result;
   }

   private String getResolver(Object object, List<URIResolver> resolvers)
   {
      Class<? extends Object> type = object.getClass();
      for (URIResolver resolver : resolvers)
      {
         if (resolver.handles(type))
         {
            return resolver.resolveURI(object);
         }
      }
      return null;
   }
}
