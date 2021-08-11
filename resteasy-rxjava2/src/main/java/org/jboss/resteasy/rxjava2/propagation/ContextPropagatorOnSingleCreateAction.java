package org.jboss.resteasy.rxjava2.propagation;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import org.jboss.resteasy.concurrent.ContextualExecutors;

import java.util.concurrent.Executor;

@SuppressWarnings("rawtypes")
class ContextPropagatorOnSingleCreateAction implements BiFunction<Single, SingleObserver, SingleObserver> {

    ContextPropagatorOnSingleCreateAction() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public SingleObserver apply(final Single s, final SingleObserver o) throws Exception {
        return new ContextCapturerSingle(s, o, ContextualExecutors.executor());
    }

    private static class ContextCapturerSingle<T> implements SingleObserver<T> {

        private final SingleObserver<T> source;
        private final Executor contextExecutor;

        private ContextCapturerSingle(final Single<T> s, final SingleObserver<T> o, final Executor contextExecutor) {
            this.source = o;
            this.contextExecutor = contextExecutor;
        }

        @Override
        public void onError(final Throwable t) {
            contextExecutor.execute(() -> source.onError(t));
        }

        @Override
        public void onSubscribe(final Disposable d) {
            contextExecutor.execute(() -> source.onSubscribe(d));
        }

        @Override
        public void onSuccess(final T v) {
            contextExecutor.execute(() -> source.onSuccess(v));
        }
    }
}
