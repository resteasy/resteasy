package org.jboss.resteasy.cdi.test.ejb;

import javax.ejb.Local;

@Local
public interface InjectedStatelessEjbLocal
{
   boolean foo();
}
