package org.jboss.resteasy.jose.jws.util;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class BouncyIntegration {
    static {
        if (Security.getProvider("BC") == null)
            Security.addProvider(new BouncyCastleProvider());
    }

    public static void init() {
        // empty, the static class does it
    }
}
