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

package org.jboss.resteasy.rxjava2.propagation;

import java.util.concurrent.Executor;

import org.jboss.resteasy.concurrent.ContextualExecutors;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.functions.Function;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class ContextPropagatorOnCompletableAssemblyAction implements Function<Completable, Completable> {

    ContextPropagatorOnCompletableAssemblyAction() {
    }

    @Override
    public Completable apply(final Completable t) throws Exception {
        return new ContextPropagatorCompletable(t, ContextualExecutors.executor());
    }

    private static class ContextPropagatorCompletable extends Completable {

        private final Completable source;
        private final Executor contextExecutor;

        private ContextPropagatorCompletable(final Completable t, final Executor contextExecutor) {
            this.source = t;
            this.contextExecutor = contextExecutor;
        }

        @Override
        protected void subscribeActual(final CompletableObserver observer) {
            contextExecutor.execute(() -> source.subscribe(observer));
        }

    }
}
