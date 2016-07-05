package org.jboss.resteasy.test.cdi.injection.resource;

import javax.ejb.Remote;

@Remote
public interface ReverseInjectionEJBHolderRemote {
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
}

