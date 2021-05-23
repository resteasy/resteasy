package org.jboss.resteasy.test.cdi.modules.resource;

import jakarta.ejb.Stateless;

@Stateless
@CDIModulesInjectableBinder
public class CDIModulesInjectable implements CDIModulesInjectableIntf {
}
