package org.jboss.resteasy.rxjava3.propagation;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import org.jboss.resteasy.concurrent.ContextualExecutors;

import java.util.concurrent.Executor;

@SuppressWarnings("rawtypes")
class ContextPropagatorOnMaybeCreateAction implements BiFunction<Maybe, MaybeObserver, MaybeObserver> {

    ContextPropagatorOnMaybeCreateAction() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public MaybeObserver apply(final Maybe maybe, final MaybeObserver observer) throws Exception {
        return new ContextCapturerMaybe<>(maybe, observer, ContextualExecutors.executor());
    }

    private static class ContextCapturerMaybe<T> implements MaybeObserver<T> {

        private final MaybeObserver<T> source;
        private final Executor contextExecutor;

        private ContextCapturerMaybe(final Maybe<T> observable, final MaybeObserver<T> observer,
                                     final Executor contextExecutor) {
            this.source = observer;
            this.contextExecutor = contextExecutor;
        }

        @Override
        public void onComplete() {
            contextExecutor.execute(source::onComplete);
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
        public void onSuccess(T v) {
            contextExecutor.execute(() -> source.onSuccess(v));
        }
    }

}
