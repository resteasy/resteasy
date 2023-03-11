package org.jboss.resteasy.rxjava2.propagation;

import java.util.concurrent.Executor;

import org.jboss.resteasy.concurrent.ContextualExecutors;
import org.reactivestreams.Subscriber;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

@SuppressWarnings("rawtypes")
class ContextPropagatorOnFlowableAssemblyAction implements Function<Flowable, Flowable> {

    ContextPropagatorOnFlowableAssemblyAction() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Flowable apply(final Flowable t) throws Exception {
        return new ContextPropagatorFlowable(t, ContextualExecutors.executor());
    }

    private static class ContextPropagatorFlowable<T> extends Flowable<T> {

        private final Flowable<T> source;

        private final Executor contextExecutor;

        private ContextPropagatorFlowable(final Flowable<T> t, final Executor contextExecutor) {
            this.source = t;
            this.contextExecutor = contextExecutor;
        }

        @Override
        protected void subscribeActual(final Subscriber<? super T> observer) {
            contextExecutor.execute(() -> source.subscribe(observer));
        }

    }

}
