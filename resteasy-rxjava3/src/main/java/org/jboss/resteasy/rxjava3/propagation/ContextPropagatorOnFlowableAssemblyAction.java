package org.jboss.resteasy.rxjava3.propagation;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Function;
import org.jboss.resteasy.concurrent.ContextualExecutors;
import org.reactivestreams.Subscriber;

import java.util.concurrent.Executor;

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
