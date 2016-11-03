package org.jboss.resteasy.test.cdi.extensions.resource;

import javax.inject.Inject;

@CDIExtensionsBoston
public class CDIExtensionsBostonHolder {
    @Inject public CDIExtensionsTestReader reader;

    @Inject @CDIExtensionsBoston
    public CDIExtensionsBostonlLeaf leaf;

    public CDIExtensionsTestReader getReader() {
        return reader;
    }

    public CDIExtensionsBostonlLeaf getLeaf() {
        return leaf;
    }

    public String toString() {
        return String.format("%nthis: %s%nreader: %s%nleaf: %s", System.identityHashCode(this), reader, leaf);
    }
}
