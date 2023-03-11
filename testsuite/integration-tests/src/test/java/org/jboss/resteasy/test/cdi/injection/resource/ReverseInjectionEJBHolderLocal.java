package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.ejb.Local;

@Local
public interface ReverseInjectionEJBHolderLocal {
    boolean testScopes();

    void setup();

    boolean test();

    void sleSetup();

    boolean sleTest();

    void sfdeSetup();

    boolean sfdeTest();

    void sfreSetup();

    boolean sfreTest();

    void sfaeSetup();

    boolean sfaeTest();

    void sliSetup();

    boolean sliTest();

    void sfdiSetup();

    boolean sfdiTest();

    void sfriSetup();

    boolean sfriTest();

    void sfaiSetup();

    boolean sfaiTest();

    boolean theSame(ReverseInjectionEJBHolderLocal that);

    int theSecret();
}
