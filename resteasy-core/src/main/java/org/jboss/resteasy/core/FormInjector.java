package org.jboss.resteasy.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ValueInjector;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormInjector implements ValueInjector {
    private Class type;
    private ConstructorInjector constructorInjector;
    private PropertyInjector propertyInjector;
    private boolean useConstructorInjection;

    @SuppressWarnings(value = "unchecked")
    public FormInjector(final Class type, final ResteasyProviderFactory factory) {
        this.type = type;
        Constructor<?> constructor = null;

        // Debug: Log class being processed
        System.out.println("=== FormInjector for: " + type.getName() + " ===");

        // Check if this is a Record or has an annotated constructor for constructor injection
        constructor = findInjectableConstructor(type);

        if (constructor != null) {
            // Use constructor injection for Records and immutable classes
            System.out.println("✓ Using CONSTRUCTOR injection");
            System.out.println("  Constructor: " + constructor);
            System.out.println("  Parameters: " + constructor.getParameterCount());
            useConstructorInjection = true;
            constructorInjector = factory.getInjectorFactory().createConstructor(constructor, factory);
            propertyInjector = null;
        } else {
            // Fall back to no-arg constructor + property injection for mutable classes
            System.out.println("✓ Using PROPERTY injection (traditional)");
            useConstructorInjection = false;
            try {
                constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(Messages.MESSAGES.unableToInstantiateForm());
            }
            constructorInjector = factory.getInjectorFactory().createConstructor(constructor, factory);
            propertyInjector = factory.getInjectorFactory().createPropertyInjector(type, factory);
        }
    }

    /**
     * Find a constructor suitable for injection. Prioritizes:
     * 1. Record canonical constructor (if class is a Record)
     * 2. Constructor with JAX-RS parameter annotations (@FormParam, @QueryParam, etc.)
     * 3. null (fall back to no-arg constructor + property injection)
     */
    private Constructor<?> findInjectableConstructor(Class<?> clazz) {
        System.out.println("  Checking if Record: " + clazz.getSimpleName());

        // Check if this is a Record (Java 16+)
        if (isRecord(clazz)) {
            System.out.println("  ✓ IS A RECORD!");
            // For Records, use the canonical constructor
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            if (constructors.length > 0) {
                // The canonical constructor is typically the one with the most parameters
                Constructor<?> canonical = constructors[0];
                for (Constructor<?> c : constructors) {
                    if (c.getParameterCount() > canonical.getParameterCount()) {
                        canonical = c;
                    }
                }
                System.out.println("  Found canonical constructor with " + canonical.getParameterCount() + " parameters");
                canonical.setAccessible(true);
                return canonical;
            }
        } else {
            System.out.println("  Not a Record, checking for annotated constructors...");
        }

        // Check for constructors with JAX-RS parameter annotations
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                continue; // Skip no-arg constructor
            }

            // Check if any parameter has JAX-RS annotations
            Annotation[][] paramAnnotations = constructor.getParameterAnnotations();
            boolean hasParamAnnotations = false;
            for (Annotation[] annotations : paramAnnotations) {
                if (hasJaxRsParamAnnotation(annotations)) {
                    hasParamAnnotations = true;
                    break;
                }
            }

            if (hasParamAnnotations) {
                System.out.println("  ✓ Found annotated constructor with " + constructor.getParameterCount() + " parameters");
                constructor.setAccessible(true);
                return constructor;
            }
        }

        System.out.println("  No injectable constructor found, will use property injection");
        return null; // No injectable constructor found
    }

    /**
     * Check if the class is a Record (Java 16+).
     * Uses reflection to avoid compilation issues on older Java versions.
     */
    private boolean isRecord(Class<?> clazz) {
        try {
            // Use reflection to call Class.isRecord() if available (Java 16+)
            java.lang.reflect.Method isRecordMethod = Class.class.getMethod("isRecord");
            return (Boolean) isRecordMethod.invoke(clazz);
        } catch (Exception e) {
            // Method doesn't exist (Java < 16) or other error
            return false;
        }
    }

    /**
     * Check if annotations contain any JAX-RS parameter annotation.
     */
    private boolean hasJaxRsParamAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            String annotationName = annotation.annotationType().getName();
            if (annotationName.startsWith("jakarta.ws.rs.") &&
                    (annotationName.endsWith("Param") || annotationName.equals("jakarta.ws.rs.BeanParam"))) {
                return true;
            }
            // Also check RESTEasy-specific annotations
            if (annotationName.startsWith("org.jboss.resteasy.annotations.jaxrs.")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object inject(boolean unwrapAsync) {
        throw new IllegalStateException(Messages.MESSAGES.cannotInjectIntoForm());
    }

    @Override
    public Object inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
        // If using constructor injection only (Records/immutable classes),
        // construct with request/response for parameter injection
        if (useConstructorInjection) {
            System.out.println("→ Injecting via constructor for: " + type.getSimpleName());
            Object result = constructorInjector.construct(request, response, unwrapAsync);
            System.out.println(
                    "← Constructor injection complete: " + (result != null ? result.getClass().getSimpleName() : "null"));
            return result;
        }

        // Otherwise, use no-arg constructor and perform property injection for mutable classes
        Object obj = constructorInjector.construct(unwrapAsync);
        if (obj instanceof CompletionStage) {
            @SuppressWarnings("unchecked")
            CompletionStage<Object> stage = (CompletionStage<Object>) obj;
            return stage.thenCompose(target -> {
                CompletionStage<Void> propertyStage = propertyInjector.inject(request, response, target, unwrapAsync);
                return propertyStage == null ? CompletableFuture.completedFuture(target)
                        : propertyStage
                                .thenApply(v -> target);
            });
        }
        CompletionStage<Void> propertyStage = propertyInjector.inject(request, response, obj, unwrapAsync);
        return propertyStage == null ? obj : propertyStage.thenApply(v -> obj);

    }
}
