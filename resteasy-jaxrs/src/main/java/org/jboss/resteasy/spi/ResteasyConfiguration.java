package org.jboss.resteasy.spi;

import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * Context injectable interface that allows you to get at configuration information.  If resteasy is deployed
 * within a WAR this will allow you to reference servlet context, filter, and servlet parameters.
 */
public interface ResteasyConfiguration
{
   /**
    * i.e. Servlet init-param first is searched, then servlet context.
    *
    * @param name parameter name
    * @return parameter value
    */
   String getParameter(String name);

   Set<String> getParameterNames();

   /**
    * Only provide parameter from a servlet or filter init param.
    *
    * @param name parameter name
    * @return parameter value
    */
   String getInitParameter(String name);

   Set<String> getInitParameterNames();
}
