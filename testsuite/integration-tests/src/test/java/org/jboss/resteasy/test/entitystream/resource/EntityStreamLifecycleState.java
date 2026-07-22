/*
 * JBoss, Home of Professional Open Source.
 * Licensed under the Apache License, Version 2.0 (the "License").
 */
package org.jboss.resteasy.test.entitystream.resource;

import java.util.concurrent.atomic.AtomicBoolean;

public final class EntityStreamLifecycleState {
    static final AtomicBoolean CLOSED = new AtomicBoolean();

    private EntityStreamLifecycleState() {
    }
}
