/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2023 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.client.jaxrs.engines;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.resteasy.client.jaxrs.i18n.LogMessages;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
record ClientCleanupAction(AtomicBoolean closed, AtomicBoolean autoClosed, AutoCloseable client) implements Runnable {

    @Override
    public void run() {
        if (closed.compareAndSet(false, true)) {
            if (client != null) {
                if (autoClosed.get()) {
                    LogMessages.LOGGER.closingForYou(client.getClass());
                }
                try {
                    client.close();
                } catch (Exception e) {
                    LogMessages.LOGGER.debugf(e, "Failed to close client %s", client);
                }
            }
        }
    }
}
