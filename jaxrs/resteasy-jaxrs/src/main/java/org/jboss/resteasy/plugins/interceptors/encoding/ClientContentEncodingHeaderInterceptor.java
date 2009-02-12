package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.core.interception.AcceptedByMethod;
import org.jboss.resteasy.core.interception.ClientInterceptor;

import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ClientInterceptor
@Provider
public class ClientContentEncodingHeaderInterceptor extends ContentEncodingHeaderInterceptor implements AcceptedByMethod
{
   public boolean accept(Class declaring, Method method)
   {
      if (declaring == null || method == null) return false;

      for (Annotation[] annotations : method.getParameterAnnotations())
      {
         if (hasEncodingAnnotation(annotations)) return true;
      }
      return hasEncodingAnnotation(declaring.getClass().getAnnotations());
   }
}
