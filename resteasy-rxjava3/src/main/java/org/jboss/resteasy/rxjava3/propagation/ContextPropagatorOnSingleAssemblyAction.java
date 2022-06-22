package org.jboss.resteasy.rxjava3.propagation;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.functions.Function;
import org.jboss.resteasy.concurrent.ContextualExecutors;

import java.util.concurrent.Executor;

@SuppressWarnings("rawtypes")
class ContextPropagatorOnSingleAssemblyAction implements Function<Single, Single> {

    ContextPropagatorOnSingleAssemblyAction() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Single apply(final Single t) throws Exception {
        return new ContextPropagatorSingle(t, ContextualExecutors.executor());
    }

    private static class ContextPropagatorSingle<T> extends Single<T> {

        private final Single<T> source;
        private final Executor contextExecutor;

        private ContextPropagatorSingle(final Single<T> t, final Executor contextExecutor) {
            this.source = t;
            this.contextExecutor = contextExecutor;
        }

        @Override
        protected void subscribeActual(final SingleObserver<? super T> observer) {
            contextExecutor.execute(() -> source.subscribe(observer));
        }

    }

}
