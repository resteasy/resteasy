package org.jboss.resteasy.test.cdi.modules.resource;

import jakarta.ejb.Stateless;
import jakarta.enterprise.context.Dependent;

@Stateless
@Dependent
@CDIModulesInjectableBinder
public class CDIModulesInjectable implements CDIModulesInjectableIntf {
}
