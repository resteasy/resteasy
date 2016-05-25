package org.jboss.resteasy.cdi.util;

import java.util.Random;
import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 11, 2012
 */
public class UtilityProducer
{
   static private Random random = new Random();
   
   @Produces
   public Logger produceLog(InjectionPoint injectionPoint)
   {
      return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
   }
   
   @Produces
   public int randomInt()
   {
      return random.nextInt();
   }
}
