package org.jboss.resteasy.util;

import org.jboss.resteasy.microprofile.config.ResteasyConfigFactory;
import org.jboss.resteasy.microprofile.config.ResteasyConfig.SOURCE;

/**
 * Utility class that provides a port number for the Resteasy embedded container.
 *
 * @author <a href="justin@justinedelson.com">Justin Edelson</a>
 * @version $Revision$
 */
public class PortProvider
{
   private static final int DEFAULT_PORT = 8081;

   private static final String ENV_VAR_NAME = "RESTEASY_PORT";

   private static final String PROPERTY_NAME = "org.jboss.resteasy.port";

   private static final String DEFAULT_HOST = "localhost";

   private static final String ENV_VAR_HOSTNAME = "RESTEASY_HOST";

   private static final String PROPERTY_HOSTNAME = "org.jboss.resteasy.host";

   /**
   /**
    * Look up the configured port number, first checking an environment variable (RESTEASY_PORT),
    * then a system property (org.jboss.resteasy.port), and finally the default port (8081).
    *
    * @return the port number specified in either the environment or system properties
    */
   public static int getPort()
   {
      int port = -1;
      String property =  ResteasyConfigFactory.getConfig().getValue(ENV_VAR_NAME, SOURCE.ENV, null);
      if (property != null)
      {
         try
         {
            port = Integer.parseInt(property);
         }
         catch (NumberFormatException e)
         {
         }
      }

      if (port == -1)
      {
         property =  ResteasyConfigFactory.getConfig().getValue(PROPERTY_NAME, SOURCE.SYSTEM, null);
         if (property != null)
         {
            try
            {
               port = Integer.parseInt(property);
            }
            catch (NumberFormatException e)
            {
            }
         }
      }

      if (port == -1)
      {
         port = DEFAULT_PORT;
      }
      return port;
   }

   /**
    * Look up the configured hostname, first checking an environment variable (RESTEASY_HOST),
    * then a system property (org.jboss.resteasy.host), and finally the default hostname (localhost).
    *
    * @return the host specified in either the environment or system properties
    */
   public static String getHost()
   {
      String host = null;
      String property = ResteasyConfigFactory.getConfig().getValue(ENV_VAR_HOSTNAME, SOURCE.ENV, null);

      if (property != null)
      {
         host = property;
      }

      if (host == null)
      {
         property = ResteasyConfigFactory.getConfig().getValue(PROPERTY_HOSTNAME, SOURCE.SYSTEM, null);
         if (property != null)
         {
            host = property;
         }
      }

      if (host == null)
      {
         host = DEFAULT_HOST;
      }
      return host;
   }
}
