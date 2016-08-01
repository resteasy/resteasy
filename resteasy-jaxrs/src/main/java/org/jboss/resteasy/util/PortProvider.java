/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.resteasy.util;


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
      String property = System.getenv(ENV_VAR_NAME);
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
         property = System.getProperty(PROPERTY_NAME);
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
      String property = System.getenv(ENV_VAR_HOSTNAME);
      if (property != null)
      {
         host = property;
      }

      if (host == null)
      {
         property = System.getProperty(PROPERTY_HOSTNAME);
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
