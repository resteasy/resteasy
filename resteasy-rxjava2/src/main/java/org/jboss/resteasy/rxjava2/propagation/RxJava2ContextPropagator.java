package org.jboss.resteasy.rxjava2.propagation;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.Dispatcher;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Reactive Context propagator for RxJava 2. Supports propagating context to all
 * {@link Single}, {@link Observable}, {@link Completable}, {@link Flowable} and
 * {@link Maybe} types.
 *
 * @author Stéphane Épardaud
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Provider
public class RxJava2ContextPropagator implements Feature {

    @Override
    public boolean configure(final FeatureContext context) {
        // this is tied to the deployment, which is what we want for the reactive context
        if (context.getConfiguration().getRuntimeType() == RuntimeType.CLIENT)
            return false;
        if (ResteasyContext.getContextData(Dispatcher.class) == null) {
            // this can happen, but it means we're not able to find a deployment
            return false;
        }
        configure();
        return true;
    }

    private synchronized void configure() {
        RxJavaPlugins.setOnSingleSubscribe(new ContextPropagatorOnSingleCreateAction());
        RxJavaPlugins.setOnCompletableSubscribe(new ContextPropagatorOnCompletableCreateAction());
        RxJavaPlugins.setOnFlowableSubscribe(new ContextPropagatorOnFlowableCreateAction());
        RxJavaPlugins.setOnMaybeSubscribe(new ContextPropagatorOnMaybeCreateAction());
        RxJavaPlugins.setOnObservableSubscribe(new ContextPropagatorOnObservableCreateAction());

        RxJavaPlugins.setOnSingleAssembly(new ContextPropagatorOnSingleAssemblyAction());
        RxJavaPlugins.setOnCompletableAssembly(new ContextPropagatorOnCompletableAssemblyAction());
        RxJavaPlugins.setOnFlowableAssembly(new ContextPropagatorOnFlowableAssemblyAction());
        RxJavaPlugins.setOnMaybeAssembly(new ContextPropagatorOnMaybeAssemblyAction());
        RxJavaPlugins.setOnObservableAssembly(new ContextPropagatorOnObservableAssemblyAction());
    }
}
