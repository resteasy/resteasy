package org.jboss.resteasy.core.interception;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface RequestInterceptor
{
   void invoke(RequestContext ctx);
}
