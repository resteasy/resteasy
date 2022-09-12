package org.jboss.resteasy.rxjava2.propagation;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Function;
import org.jboss.resteasy.concurrent.ContextualExecutors;

import java.util.concurrent.Executor;

@SuppressWarnings("rawtypes")
class ContextPropagatorOnObservableAssemblyAction implements Function<Observable, Observable> {

    ContextPropagatorOnObservableAssemblyAction() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Observable apply(final Observable t) throws Exception {
        return new ContextPropagatorObservable(t, ContextualExecutors.executor());
    }

    private static class ContextPropagatorObservable<T> extends Observable<T> {

        private final Observable<T> source;

        private final Executor contextExecutor;

        private ContextPropagatorObservable(final Observable<T> t, final Executor contextExecutor) {
            this.source = t;
            this.contextExecutor = contextExecutor;
        }

        @Override
        protected void subscribeActual(final Observer<? super T> observer) {
            contextExecutor.execute(() -> source.subscribe(observer));
        }

    }

}
