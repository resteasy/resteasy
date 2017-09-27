package org.jboss.resteasy.spi.metadata;

import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMethod extends ResourceLocator
{
   private static final MediaType[] empty = {};
   protected Set<String> httpMethods = new HashSet<String>();
   protected MediaType[] produces = empty;
   protected MediaType[] consumes = empty;
   protected boolean asynchronous;

   public ResourceMethod(ResourceClass declaredClass, Method method, Method annotatedMethod)
   {
      super(declaredClass, method, annotatedMethod);
   }

   public Set<String> getHttpMethods()
   {
      return httpMethods;
   }

   public MediaType[] getProduces()
   {
      return produces;
   }

   public MediaType[] getConsumes()
   {
      return consumes;
   }

   public boolean isAsynchronous()
   {
      return asynchronous;
   }

   public void markAsynchronous()
   {
      asynchronous = true;
   }
}
