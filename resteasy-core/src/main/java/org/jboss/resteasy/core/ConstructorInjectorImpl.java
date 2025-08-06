package org.jboss.resteasy.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ValueInjector;
import org.jboss.resteasy.spi.metadata.ConstructorParameter;
import org.jboss.resteasy.spi.metadata.ResourceConstructor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ConstructorInjectorImpl implements ConstructorInjector {
    @SuppressWarnings("rawtypes")
    protected Constructor constructor;
    protected ValueInjector[] params;

    public ConstructorInjectorImpl(final ResourceConstructor constructor, final ResteasyProviderFactory factory) {
        this.constructor = constructor.getConstructor();
        params = new ValueInjector[constructor.getParams().length];
        int i = 0;
        for (ConstructorParameter parameter : constructor.getParams()) {
            params[i++] = factory.getInjectorFactory().createParameterExtractor(parameter, factory);
        }

    }

    public ConstructorInjectorImpl(@SuppressWarnings("rawtypes") final Constructor constructor,
            final ResteasyProviderFactory factory) {
        this.constructor = constructor;
        params = new ValueInjector[constructor.getParameterCount()];
        Parameter[] reflectionParameters = constructor.getParameters();
        for (int i = 0; i < constructor.getParameterCount(); i++) {
            Class<?> type = constructor.getParameterTypes()[i];
            Type genericType = constructor.getGenericParameterTypes()[i];
            Annotation[] annotations = constructor.getParameterAnnotations()[i];
            String name = reflectionParameters[i].getName();
            params[i] = factory.getInjectorFactory().createParameterExtractor(constructor.getDeclaringClass(), constructor,
                    name, type, genericType, annotations, factory);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object injectableArguments(HttpRequest input,
            HttpResponse response,
            boolean unwrapAsync) {
        if (params != null && params.length > 0) {
            Object[] args = new Object[params.length];
            int i = 0;
            CompletionStage<Void> stage = null;
            for (ValueInjector extractor : params) {
                int ifinal = i++;
                Object injectedObject = extractor.inject(input, response, unwrapAsync);
                if (injectedObject != null && injectedObject instanceof CompletionStage) {
                    if (stage == null)
                        stage = CompletableFuture.completedFuture(null);
                    stage = stage.thenCompose(v -> ((CompletionStage<Object>) injectedObject)
                            .thenAccept(value -> args[ifinal] = CompletionStageHolder.resolve(value)));
                } else {
                    args[ifinal] = CompletionStageHolder.resolve(injectedObject);
                }

            }
            if (stage == null)
                return args;
            else
                return stage.thenApply(v -> args);
        } else
            return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object injectableArguments(boolean unwrapAsync) {
        if (params != null && params.length > 0) {
            Object[] args = new Object[params.length];
            int i = 0;
            CompletionStage<Void> stage = null;
            for (ValueInjector extractor : params) {
                int ifinal = i++;
                Object injectedObject = extractor.inject(unwrapAsync);
                if (injectedObject != null && injectedObject instanceof CompletionStage) {
                    if (stage == null)
                        stage = CompletableFuture.completedFuture(null);
                    stage = stage.thenCompose(v -> ((CompletionStage<Object>) injectedObject)
                            .thenAccept(value -> args[ifinal] = CompletionStageHolder.resolve(value)));

                } else {
                    args[ifinal] = CompletionStageHolder.resolve(injectedObject);
                }
            }
            if (stage == null)
                return args;
            else
                return stage.thenApply(v -> args);
        } else
            return null;
    }

    public Object construct(HttpRequest request, HttpResponse httpResponse, boolean unwrapAsync)
            throws Failure, ApplicationException, WebApplicationException {
        Object obj = injectableArguments(request, httpResponse, unwrapAsync);

        if (obj == null || !(obj instanceof CompletionStage)) {
            return constructInRequest((Object[]) obj);
        }

        @SuppressWarnings("unchecked")
        CompletionStage<Object[]> stagedArgs = (CompletionStage<Object[]>) obj;
        return stagedArgs.exceptionally(e -> {
            //CompletionStage does not support rethrow of exception.
            //Must create new exception object and throw it.
            Throwable t = e.getCause();
            if (t != null) {
                if (t instanceof NotFoundException) {
                    throw new NotFoundException(t.getMessage(), t.getCause());
                } else if (t instanceof BadRequestException) {
                    throw new BadRequestException(t.getMessage(), t.getCause());
                }
            }
            throw new InternalServerErrorException(
                    Messages.MESSAGES.failedProcessingArguments(constructor.toString()), e);

        }).thenApply(args -> {
            return constructInRequest(args);
        });
    }

    protected Object constructInRequest(Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException e) {
            throw new InternalServerErrorException(Messages.MESSAGES.failedToConstruct(constructor.toString()), e);
        } catch (IllegalAccessException e) {
            throw new InternalServerErrorException(Messages.MESSAGES.failedToConstruct(constructor.toString()), e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof WebApplicationException) {
                throw (WebApplicationException) cause;
            }
            throw new ApplicationException(Messages.MESSAGES.failedToConstruct(
                    constructor.toString()), e.getCause());
        } catch (IllegalArgumentException e) {
            String msg = Messages.MESSAGES.badArguments(constructor.toString() + "  (");
            boolean first = false;
            for (Object arg : args) {
                if (!first) {
                    first = true;
                } else {
                    msg += ",";
                }
                if (arg == null) {
                    msg += " null";
                    continue;
                }
                msg += " " + arg;
            }
            throw new InternalServerErrorException(msg, e);
        }
    }

    @Override
    public Object construct(boolean unwrapAsync) {
        Object obj = injectableArguments(unwrapAsync);
        if (obj == null || !(obj instanceof CompletionStage)) {
            return constructOutsideRequest((Object[]) obj);
        }

        @SuppressWarnings("unchecked")
        CompletionStage<Object[]> stagedArgs = (CompletionStage<Object[]>) obj;
        return stagedArgs
                .thenApply(args -> {
                    return constructOutsideRequest(args);
                });
    }

    protected Object constructOutsideRequest(Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException e) {
            throw new RuntimeException(Messages.MESSAGES.failedToConstruct(constructor.toString()), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(Messages.MESSAGES.failedToConstruct(constructor.toString()), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(Messages.MESSAGES.failedToConstruct(constructor.toString()), e.getCause());
        } catch (IllegalArgumentException e) {
            String msg = Messages.MESSAGES.badArguments(constructor.toString() + "  (");
            boolean first = false;
            for (Object arg : args) {
                if (!first) {
                    first = true;
                } else {
                    msg += ",";
                }
                if (arg == null) {
                    msg += " null";
                    continue;
                }
                msg += " " + arg;
            }
            throw new RuntimeException(msg, e);
        }
    }
}
