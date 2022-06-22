package org.jboss.resteasy.rxjava3.propagation;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.functions.Function;
import org.jboss.resteasy.concurrent.ContextualExecutors;

import java.util.concurrent.Executor;

@SuppressWarnings("rawtypes")
class ContextPropagatorOnMaybeAssemblyAction implements Function<Maybe, Maybe> {

    ContextPropagatorOnMaybeAssemblyAction() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Maybe apply(Maybe t) throws Exception {
        return new ContextPropagatorMaybe(t, ContextualExecutors.executor());
    }

    private static class ContextPropagatorMaybe<T> extends Maybe<T> {

        private final Maybe<T> source;

        private final Executor contextExecutor;

        private ContextPropagatorMaybe(final Maybe<T> t, final Executor contextExecutor) {
            this.source = t;
            this.contextExecutor = contextExecutor;
        }

        @Override
        protected void subscribeActual(final MaybeObserver<? super T> observer) {
            contextExecutor.execute(() -> source.subscribe(observer));
        }

    }

}
