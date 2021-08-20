package org.jboss.resteasy.rxjava2.propagation;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import org.jboss.resteasy.concurrent.ContextualExecutors;

import java.util.concurrent.Executor;

class ContextPropagatorOnCompletableCreateAction
        implements BiFunction<Completable, CompletableObserver, CompletableObserver> {

    ContextPropagatorOnCompletableCreateAction() {
    }

    @Override
    public CompletableObserver apply(final Completable completable, final CompletableObserver observer)
            throws Exception {
        return new ContextCapturerCompletable(completable, observer, ContextualExecutors.executor());
    }

    private static class ContextCapturerCompletable implements CompletableObserver {

        private final CompletableObserver source;
        private final Executor contextExecutor;

        private ContextCapturerCompletable(final Completable s, final CompletableObserver o,
                                           final Executor contextExecutor) {
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
        public void onComplete() {
            contextExecutor.execute(source::onComplete);
        }
    }

}
