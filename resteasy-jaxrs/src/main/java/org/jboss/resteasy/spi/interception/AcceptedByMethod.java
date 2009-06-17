package org.jboss.resteasy.spi.interception;

import java.lang.reflect.Method;

/**
 * Implemented interface that can trigger the addition of an interceptor
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface AcceptedByMethod
{
   public boolean accept(Class declaring, Method method);
}
