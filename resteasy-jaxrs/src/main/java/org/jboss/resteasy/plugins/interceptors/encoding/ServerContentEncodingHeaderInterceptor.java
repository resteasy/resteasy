package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.core.interception.AcceptedByMethod;
import org.jboss.resteasy.core.interception.ServerInterceptor;

import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ServerInterceptor
@Provider
public class ServerContentEncodingHeaderInterceptor extends ContentEncodingHeaderInterceptor implements AcceptedByMethod
{
   public boolean accept(Class declaring, Method method)
   {
      return hasEncodingAnnotation(method.getAnnotations()) || hasEncodingAnnotation(declaring.getClass().getAnnotations());
   }
}