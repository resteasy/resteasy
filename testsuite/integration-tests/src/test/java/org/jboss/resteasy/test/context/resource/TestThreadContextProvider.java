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

package org.jboss.resteasy.test.context.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.jboss.resteasy.spi.concurrent.ThreadContext;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class TestThreadContextProvider implements ThreadContext<Map<String, String>> {

    public static final ThreadLocal<Map<String, String>> localState = ThreadLocal.withInitial(HashMap::new);
    private final CountDownLatch reset;

    public TestThreadContextProvider(final CountDownLatch reset) {
        this.reset = reset;
    }

    @Override
    public Map<String, String> capture() {
        // We use putIfAbsent here as other threads could come into play and we really only want the first threads name
        localState.get().putIfAbsent("captured", Thread.currentThread().getName());
        return localState.get();
    }

    @Override
    public void push(final Map<String, String> context) {
        context.put("push", Thread.currentThread().getName());
        localState.set(context);
    }

    @Override
    public void reset(final Map<String, String> context) {
        context.put("reset", Thread.currentThread().getName());
        localState.remove();
        reset.countDown();
    }
}
