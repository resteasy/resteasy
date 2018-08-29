package org.jboss.resteasy.plugins.server.sun.http;

import org.jboss.logging.Logger;
import org.jboss.resteasy.util.PortProvider;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.sun.http.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyDeployment;

import java.util.Hashtable;

/**
 * Sun HttpServerContainer
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServerContainer
{

   private static final Logger LOG = Logger.getLogger(HttpServerContainer.class);

   public static SunHttpJaxrsServer sun;

   public static ResteasyDeployment start() throws Exception
   {
      return start("");
   }

   public static ResteasyDeployment start(String bindPath) throws Exception
   {
      return start(bindPath, null, null, null);
   }

   public static ResteasyDeployment start(String bindPath, Hashtable<String,String> initParams) throws Exception
   {
      return start(bindPath, null, initParams, null);
   }

   public static ResteasyDeployment start(String bindPath, Hashtable<String,String> initParams, Hashtable<String,String> contextParams) throws Exception
   {
      return start(bindPath, null, initParams, contextParams);
   }
   
   public static void start(ResteasyDeployment deployment) throws Exception
   {
      LOG.info(Messages.MESSAGES.embeddedContainerStart());
      sun = new SunHttpJaxrsServer();
      sun.setDeployment(deployment);
      sun.setPort(PortProvider.getPort());
      sun.setRootResourcePath("");
      sun.setSecurityDomain(null);
      sun.start();
   }

   public static ResteasyDeployment start(String bindPath, SecurityDomain domain) throws Exception
   {
      return start(bindPath, domain, null, null);
   }
   
   public static ResteasyDeployment start(String bindPath, SecurityDomain domain, Hashtable<String,String> initParams, Hashtable<String,String> contextParams) throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setSecurityEnabled(true);
      return start(bindPath, domain, deployment, initParams, contextParams);
   }

   public static ResteasyDeployment start(String bindPath, SecurityDomain domain, ResteasyDeployment deployment, Hashtable<String,String> initParams, Hashtable<String,String> contextParams) throws Exception
   {
      sun = new SunHttpJaxrsServer();
      sun.setDeployment(deployment);
      sun.setPort(PortProvider.getPort());
      sun.setRootResourcePath(bindPath);
      sun.setSecurityDomain(domain);
      sun.start();
      return sun.getDeployment();
   }

   public static void stop() throws Exception
   {
      LOG.info(Messages.MESSAGES.embeddedContainerStop());
      if (sun != null)
      {
         try
         {
            sun.stop();
         }
         catch (Exception e)
         {

         }
      }
      sun = null;
   }
   
   public static void main(String[] args) throws Exception {
	   start();
   }

}
