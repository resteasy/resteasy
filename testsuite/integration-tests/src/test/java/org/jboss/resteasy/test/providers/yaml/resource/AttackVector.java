package org.jboss.resteasy.test.providers.yaml.resource;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class AttackVector {
    static final AtomicBoolean CONSTRUCTOR_INVOKED = new AtomicBoolean(false);
    static final AtomicBoolean STATIC_BLOCK_INVOKED = new AtomicBoolean(false);

    static {
        STATIC_BLOCK_INVOKED.set(true);
    }

    public AttackVector() {
        CONSTRUCTOR_INVOKED.set(true);
    }
}
