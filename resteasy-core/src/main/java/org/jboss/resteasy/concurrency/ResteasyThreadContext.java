/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2021 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.concurrency;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.concurrent.ThreadContext;

import java.util.Map;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyThreadContext implements ThreadContext<Map<Class<?>, Object>> {
    @Override
    public Map<Class<?>, Object> capture() {
        return ResteasyContext.getContextDataMap();
    }

    @Override
    public void push(final Map<Class<?>, Object> context) {
        ResteasyContext.pushContextDataMap(context);
    }

    @Override
    public void reset() {
        ResteasyContext.removeContextDataLevel();
    }
}
