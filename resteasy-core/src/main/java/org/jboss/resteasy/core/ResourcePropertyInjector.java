package org.jboss.resteasy.core;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ValueInjector;
import org.jboss.resteasy.spi.metadata.FieldParameter;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.SetterParameter;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourcePropertyInjector implements PropertyInjector {
    private static class FieldInjector {
        public FieldParameter param;
        public ValueInjector injector;

        private FieldInjector(final FieldParameter param, final ValueInjector injector) {
            this.param = param;
            this.injector = injector;
        }
    }

    private static class SetterInjector {
        public SetterParameter param;
        public ValueInjector injector;

        private SetterInjector(final SetterParameter param, final ValueInjector injector) {
            this.param = param;
            this.injector = injector;
        }
    }

    protected List<FieldInjector> fields = new ArrayList<FieldInjector>();
    protected List<SetterInjector> setters = new ArrayList<SetterInjector>();
    protected ResourceClass resourceClass;
    protected ResteasyProviderFactory factory;

    public ResourcePropertyInjector(final ResourceClass resourceClass, final ResteasyProviderFactory factory) {
        this.resourceClass = resourceClass;
        this.factory = factory;

        for (FieldParameter param : resourceClass.getFields()) {
            ValueInjector injector = factory.getInjectorFactory().createParameterExtractor(param, factory);
            if (injector == null)
                continue;
            fields.add(new FieldInjector(param, injector));
        }
        for (SetterParameter param : resourceClass.getSetters()) {
            ValueInjector injector = factory.getInjectorFactory().createParameterExtractor(param, factory);
            if (injector == null)
                continue;
            setters.add(new SetterInjector(param, injector));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public CompletionStage<Void> inject(HttpRequest request, HttpResponse response, Object target, boolean unwrapAsync)
            throws Failure {
        CompletionStage<Void> ret = null;
        for (FieldInjector injector : fields) {
            Object injectedObject = injector.injector.inject(request, response, unwrapAsync);
            if (injectedObject != null && injectedObject instanceof CompletionStage) {
                if (ret == null)
                    ret = CompletableFuture.completedFuture(null);
                ret = ret.thenCompose(v -> ((CompletionStage<Object>) injectedObject)
                        .thenAccept(value -> {
                            try {
                                injector.param.getField().set(target, value);
                            } catch (IllegalAccessException e) {
                                throw new InternalServerErrorException(e);
                            }
                        }));

            } else {
                try {
                    injector.param.getField().set(target, CompletionStageHolder.resolve(injectedObject));
                } catch (IllegalAccessException e) {
                    throw new InternalServerErrorException(e);
                }
            }
        }
        for (SetterInjector injector : setters) {
            Object injectedObject = injector.injector.inject(request, response, unwrapAsync);
            if (injectedObject != null && injectedObject instanceof CompletionStage) {
                if (ret == null)
                    ret = CompletableFuture.completedFuture(null);
                ret = ret.thenCompose(v -> ((CompletionStage<Object>) injectedObject)
                        .thenAccept(value -> {
                            try {
                                injector.param.getSetter().invoke(target, value);
                            } catch (IllegalAccessException e) {
                                throw new InternalServerErrorException(e);
                            } catch (InvocationTargetException e) {
                                throw new ApplicationException(e.getCause());
                            }
                        }));
            } else {
                try {
                    injector.param.getSetter().invoke(target, CompletionStageHolder.resolve(injectedObject));
                } catch (IllegalAccessException e) {
                    throw new InternalServerErrorException(e);
                } catch (InvocationTargetException e) {
                    throw new ApplicationException(e.getCause());
                }

            }
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CompletionStage<Void> inject(Object target, boolean unwrapAsync) {
        CompletionStage<Void> ret = null;
        for (FieldInjector injector : fields) {
            Object injectedObject = injector.injector.inject(unwrapAsync);
            if (injectedObject != null && injectedObject instanceof CompletionStage) {
                if (ret == null)
                    ret = CompletableFuture.completedFuture(null);
                ret = ret.thenCompose(v -> ((CompletionStage<Object>) injectedObject)
                        .thenAccept(value -> {
                            try {
                                injector.param.getField().set(target, value);
                            } catch (IllegalAccessException e) {
                                throw new InternalServerErrorException(e);
                            }
                        }));

            } else {
                try {
                    injector.param.getField().set(target, CompletionStageHolder.resolve(injectedObject));
                } catch (IllegalAccessException e) {
                    throw new InternalServerErrorException(e);
                }
            }
        }
        for (SetterInjector injector : setters) {
            Object injectedObject = injector.injector.inject(unwrapAsync);
            if (injectedObject != null && injectedObject instanceof CompletionStage) {
                if (ret == null)
                    ret = CompletableFuture.completedFuture(null);
                ret = ret.thenCompose(v -> ((CompletionStage<Object>) injectedObject)
                        .thenAccept(value -> {
                            try {
                                injector.param.getSetter().invoke(target, value);
                            } catch (IllegalAccessException e) {
                                throw new InternalServerErrorException(e);
                            } catch (InvocationTargetException e) {
                                throw new ApplicationException(e.getCause());
                            }
                        }));
            } else {
                try {
                    injector.param.getSetter().invoke(target, CompletionStageHolder.resolve(injectedObject));
                } catch (IllegalAccessException e) {
                    throw new InternalServerErrorException(e);
                } catch (InvocationTargetException e) {
                    throw new ApplicationException(e.getCause());
                }

            }
        }
        return ret;
    }
}
