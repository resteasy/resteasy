package org.jboss.resteasy.rxjava3.propagation;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.Dispatcher;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

/**
 * Reactive Context propagator for RxJava 2. Supports propagating context to all
 * {@link Single}, {@link Observable}, {@link Completable}, {@link Flowable} and
 * {@link Maybe} types.
 *
 * @author Stéphane Épardaud
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Provider
public class RxJava3ContextPropagator implements Feature {

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
