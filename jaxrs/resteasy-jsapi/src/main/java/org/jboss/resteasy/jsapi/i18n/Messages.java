package org.jboss.resteasy.jsapi.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.resteasy.core.ResourceMethodInvoker;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 27, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 11500;

   @Message(id = BASE + 0, value = "Impossible to generate JSAPI for subresource returned by method {0}.{1} since return type is not a static JAXRS resource type", format=Format.MESSAGE_FORMAT)
   String impossibleToGenerateJsapi(String className, String methodName);
   
   @Message(id = BASE + 5, value = " Invoker: %s")
   String invoker(ResourceMethodInvoker invoker);
   
   @Message(id = BASE + 10, value = "JSAPIServlet loaded")
   String jsapiServletLoaded();
   
   @Message(id = BASE + 15, value = "Loading JSAPI Servlet")
   String loadingJSAPIServlet();

   @Message(id = BASE + 20, value = "Overriding @Consumes annotation in favour of application/x-www-form-urlencoded due to the presence of @FormParam")
   String overridingConsumes();
   
   @Message(id = BASE + 25, value = "Path: %s")
   String path(String uri);
   
   @Message(id = BASE + 30, value = "Query %s")
   String query(String query);

   @Message(id = BASE + 35, value = "REST.apiURL = '%s';")
   String restApiUrl(String uri);
   
   @Message(id = BASE + 45, value = "Serving %s")
   String serving(String pathinfo);

   @Message(id = BASE + 50, value = "// start JAX-RS API")
   String startJaxRsApi();
   
   @Message(id = BASE + 55, value = "// start RESTEasy client API")
   String startResteasyClient();
   
   @Message(id = BASE + 60, value ="There are no Resteasy deployments initialized yet to scan from.  Either set the load-on-startup on each Resteasy servlet, or, if in an EE environment like JBoss or Wildfly, you'll have to do an invocation on each of your REST services to get the servlet loaded.")
   String thereAreNoResteasyDeployments();
}
