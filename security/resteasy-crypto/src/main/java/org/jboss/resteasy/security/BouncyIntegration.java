package org.jboss.resteasy.security;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BouncyIntegration {
    static {
        if (Security.getProvider("BC") == null)
            Security.addProvider(new BouncyCastleProvider());
    }

    public static void init() {
        // empty, the static class does it
    }
}
