package org.jboss.resteasy.core;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DefaultInterceptors
{
   public static Class[] defaultInterceptors = {ResourceMethodSecurityInterceptor.class};

}
