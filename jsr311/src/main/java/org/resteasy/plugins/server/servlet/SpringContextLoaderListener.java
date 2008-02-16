package org.resteasy.plugins.server.servlet;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SpringContextLoaderListener extends ContextLoaderListener
{
   protected ContextLoader createContextLoader()
   {
      return new SpringContextLoader();
   }
}
