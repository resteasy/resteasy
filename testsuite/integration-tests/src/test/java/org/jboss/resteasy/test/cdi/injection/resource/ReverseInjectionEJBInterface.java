package org.jboss.resteasy.test.cdi.injection.resource;

import javax.ejb.Remote;

@Remote
public interface ReverseInjectionEJBInterface {
    void setUp(String key);

    boolean test(String key);

    Class<?> theClass();

    boolean theSame(ReverseInjectionEJBInterface ejb);

    int theSecret();
}

