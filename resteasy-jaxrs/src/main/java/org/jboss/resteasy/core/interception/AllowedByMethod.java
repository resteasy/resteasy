package org.jboss.resteasy.core.interception;

import java.lang.reflect.Method;

/**
 * Implemented interface that can trigger the addition of an interceptor
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface AllowedByMethod
{
   public boolean accept(Class declaring, Method method);
}
