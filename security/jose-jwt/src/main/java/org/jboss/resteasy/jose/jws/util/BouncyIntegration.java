package org.jboss.resteasy.jose.jws.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;

public class BouncyIntegration {
    static {
        if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());
    }

    public static void init() {
        // empty, the static class does it
    }
}
