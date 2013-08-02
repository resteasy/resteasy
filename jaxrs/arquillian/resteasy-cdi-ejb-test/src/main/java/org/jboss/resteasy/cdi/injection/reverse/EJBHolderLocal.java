package org.jboss.resteasy.cdi.injection.reverse;

import javax.ejb.Local;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 30, 2012
 */
@Local
public interface EJBHolderLocal
{
   public boolean testScopes();
   public void setup();
   public boolean test();
  
   public void sleSetup();
   public boolean sleTest();
   
   public void sfdeSetup();
   public boolean sfdeTest();
   
   public void sfreSetup();
   public boolean sfreTest();
   
   public void sfaeSetup();
   public boolean sfaeTest(); 
   
   public void sliSetup();
   public boolean sliTest();
   
   public void sfdiSetup();
   public boolean sfdiTest();
   
   public void sfriSetup();;
   public boolean sfriTest();
   
   public void sfaiSetup();
   public boolean sfaiTest();
   
   public boolean theSame(EJBHolderLocal that);
   public int theSecret();
}

