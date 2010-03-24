package org.jboss.resteasy.cdi.test.ejb;

import javax.ejb.Stateless;

@Stateless
public class InjectedStatelessEjb implements InjectedStatelessEjbLocal
{
   public boolean foo()
   {
      return true;
   }
}
