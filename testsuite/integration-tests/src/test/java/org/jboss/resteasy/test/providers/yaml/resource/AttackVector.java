package org.jboss.resteasy.test.providers.yaml.resource;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class AttackVector {
    static final AtomicBoolean CONSTRUCTOR_INVOKED = new AtomicBoolean(false);

    public AttackVector() {
        CONSTRUCTOR_INVOKED.set(true);
    }
}
