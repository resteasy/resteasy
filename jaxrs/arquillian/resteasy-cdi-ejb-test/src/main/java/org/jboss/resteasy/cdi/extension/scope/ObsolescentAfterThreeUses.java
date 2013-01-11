package org.jboss.resteasy.cdi.extension.scope;

import javax.inject.Inject;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 11, 2012
 */
@PlannedObsolescenceScope(3)
public class ObsolescentAfterThreeUses implements Obsolescent
{
   @Inject private int secret;

   public int getSecret()
   {
      return secret;
   }
   
   public String toString()
   {
      return "ObsolescenceObject[" + System.identityHashCode(this) + "," + secret + "]";
   }
}

