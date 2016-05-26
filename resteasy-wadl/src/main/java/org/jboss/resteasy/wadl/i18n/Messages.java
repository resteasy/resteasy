package org.jboss.resteasy.wadl.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.logging.annotations.Message.Format;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright January 6, 2016
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 19000;
   
   @Message(id = BASE + 0, value = "Impossible to generate WADL for subresource returned by method {0}.{1} since return type is not a static JAXRS resource type", format=Format.MESSAGE_FORMAT)
   String impossibleToGenerateWADL(String className, String methodName);

   @Message(id = BASE + 5, value = "Loading ResteasyWadlServlet")
   String loadingResteasyWadlServlet();
   
   @Message(id = BASE + 10, value = "There are no Resteasy deployments initialized yet to scan from. Either set the load-on-startup on each Resteasy servlet, or, if in an EE environment like JBoss or Wildfly, you'll have to do an invocation on each of your REST services to get the servlet loaded.")
   String noResteasyDeployments();
   
   @Message(id = BASE + 15, value = "Overriding @Consumes annotation in favour of application/x-www-form-urlencoded due to the presence of @FormParam")
   String overridingConsumesAnnotation();

   @Message(id = BASE + 20, value = "Path: %s")
   String path(String key);
   
   @Message(id = BASE + 25, value = "Query %s")
   String query(String query);
   
   @Message(id = BASE + 30, value = "ResteasyWadlServlet loaded")
   String resteasyWadlServletLoaded();
   
   @Message(id = BASE + 35, value = "Serving %s")
   String servingPathInfo(String pathInfo);
   
   @Message(id = BASE + 36, value = "Error while processing WADL")
   String cantProcessWadl();
}
