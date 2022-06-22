package org.jboss.resteasy.rxjava3.propagation;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import org.jboss.resteasy.concurrent.ContextualExecutors;

import java.util.concurrent.Executor;

@SuppressWarnings("rawtypes")
class ContextPropagatorOnObservableCreateAction implements BiFunction<Observable, Observer, Observer> {

    ContextPropagatorOnObservableCreateAction() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Observer apply(final Observable observable, final Observer observer) throws Exception {
        return new ContextCapturerObservable(observable, observer, ContextualExecutors.executor());
    }

    private static class ContextCapturerObservable<T> implements Observer<T> {

        private final Observer<T> source;
        private final Executor contextExecutor;

        private ContextCapturerObservable(final Observable<T> observable, final Observer<T> observer,
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
        public void onNext(final T v) {
            contextExecutor.execute(() -> source.onNext(v));
        }

        @Override
        public void onSubscribe(final Disposable d) {
            contextExecutor.execute(() -> source.onSubscribe(d));
        }
    }
}
