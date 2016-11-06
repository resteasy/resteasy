package org.jboss.resteasy.test.cdi.extensions.resource;

import javax.inject.Inject;

@ScopeExtensionPlannedObsolescenceScope(2)
public class ScopeExtensionObsolescentAfterTwoUses implements ScopeExtensionObsolescent {
    @Inject
    private int secret;

    public int getSecret() {
        return secret;
    }

    public String toString() {
        return "ObsolescenceObject[" + System.identityHashCode(this) + "," + secret + "]";
    }
}

