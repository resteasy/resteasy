package org.jboss.resteasy.microprofile.config;

public class ResteasyConfigFactory
{
   private static volatile ResteasyConfig config;

   public static ResteasyConfig getConfig()
   {
      if (config != null)
      {
         return config;
      }
      synchronized (ResteasyConfigFactory.class)
      {
         if (config != null)
         {
            return config;
         }
         config = new ResteasyConfig();
         return config;
      }
   }
}
