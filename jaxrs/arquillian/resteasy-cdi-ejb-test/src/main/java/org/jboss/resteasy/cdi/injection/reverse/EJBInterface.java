package org.jboss.resteasy.cdi.injection.reverse;

import javax.ejb.Remote;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 29, 2012
 */
@Remote
public interface EJBInterface
{
   public void setUp(String key);
   public boolean test(String key);
   public Class<?> theClass();
   public boolean theSame(EJBInterface ejb);
   public int theSecret();
}

