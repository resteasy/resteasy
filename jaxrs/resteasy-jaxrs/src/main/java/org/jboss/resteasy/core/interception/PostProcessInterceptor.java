package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.ServerResponse;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface PostProcessInterceptor
{
   void postProcess(ServerResponse response);
}
