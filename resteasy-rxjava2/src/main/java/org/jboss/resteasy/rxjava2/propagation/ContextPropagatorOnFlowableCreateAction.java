package org.jboss.resteasy.rxjava2.propagation;

import java.util.concurrent.Executor;

import org.jboss.resteasy.concurrent.ContextualExecutors;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;

@SuppressWarnings("rawtypes")
class ContextPropagatorOnFlowableCreateAction implements BiFunction<Flowable, Subscriber, Subscriber> {

    ContextPropagatorOnFlowableCreateAction() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Subscriber apply(final Flowable flowable, final Subscriber observer) throws Exception {
        return new ContextCapturerFlowable<>(flowable, observer, ContextualExecutors.executor());
    }

    @SuppressWarnings("ReactiveStreamsSubscriberImplementation")
    private static class ContextCapturerFlowable<T> implements Subscriber<T> {

        private final Subscriber<T> source;
        private final Executor contextExecutor;

        private ContextCapturerFlowable(final Flowable<T> observable, final Subscriber<T> observer,
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
        public void onSubscribe(final Subscription s) {
            contextExecutor.execute(() -> source.onSubscribe(s));
        }
    }

}
