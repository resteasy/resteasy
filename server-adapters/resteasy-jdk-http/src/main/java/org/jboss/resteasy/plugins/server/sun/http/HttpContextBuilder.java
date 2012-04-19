package org.jboss.resteasy.plugins.server.sun.http;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * Helper class to create a ResteasyDeployment and bind it to an HttpContext of an HttpServer.  Setting the SecurityDomain
 * will turn on Basic Authentication.  Right now, only BasicAuthentication is supported.
 *
 * HttpContext.getAttributes() data is available within Providers and Resources by injecting a ResteasyConfiguration interface
 *
 * <pre>
     HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 10);
     contextBuilder = new HttpContextBuilder();
     contextBuilder.getDeployment().getActualResourceClasses().add(SimpleResource.class);
     HttpContext context = contextBuilder.bind(httpServer);
     context.getAttributes().put("some.config.info", "42");
     httpServer.start();

     contextBuilder.cleanup();
     httpServer.stop(0);
   </pre>
 * Create your HttpServer the way you want then use the org.jboss.resteasy.plugins.server.sun.http.HttpContextBuilder to initialize Resteasy
 * and bind it to an HttpContext.  The HttpContext attributes are available by injecting in a org.jboss.resteasy.spi.ResteasyConfiguration
 * interface using @Context within your provider and resource classes.
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpContextBuilder
{
   protected ResteasyDeployment deployment = new ResteasyDeployment();
   protected String path = "/";
   protected ResteasyHttpHandler handler;
   protected SecurityDomain securityDomain;
   protected HttpContext boundContext;

   public ResteasyDeployment getDeployment()
   {
      return deployment;
   }

   public void setDeployment(ResteasyDeployment deployment)
   {
      this.deployment = deployment;
   }

   public String getPath()
   {
      return path;
   }

   /**
    * Path to bind context
    *
    * @param path
    */
   public void setPath(String path)
   {
      this.path = path;
      if (!this.path.startsWith("/"))
      {
         this.path = "/" + path;
      }
   }

   public SecurityDomain getSecurityDomain()
   {
      return securityDomain;
   }

   /**
    * Will turn on Basic Authentication
    *
    * @param securityDomain
    */
   public void setSecurityDomain(SecurityDomain securityDomain)
   {
      this.securityDomain = securityDomain;
   }

   public HttpContext bind(HttpServer server)
   {
      handler = new ResteasyHttpHandler();
      boundContext = server.createContext(path, handler);
      HttpContextResteasyConfiguration config = new HttpContextResteasyConfiguration(boundContext);
      deployment.getDefaultContextObjects().put(ResteasyConfiguration.class, config);
      if (securityDomain != null)
      {
         boundContext.getFilters().add(new BasicAuthFilter(securityDomain));
      }
      deployment.start();
      handler.setDispatcher(deployment.getDispatcher());
      handler.setProviderFactory(deployment.getProviderFactory());
      return boundContext;

   }

   public void cleanup()
   {
      deployment.stop();
   }
}
