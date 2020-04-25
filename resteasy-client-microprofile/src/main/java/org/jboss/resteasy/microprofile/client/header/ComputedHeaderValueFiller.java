package org.jboss.resteasy.microprofile.client.header;

import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.jboss.logging.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates a value for dynamically computed headers (using {someMethod} as value in {@link org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam})
 */
class ComputedHeaderValueFiller {

    private static final Logger LOGGER = Logger.getLogger(ComputedHeaderValueFiller.class);

    private final Method method;
    private final MethodHandle methodHandle;
    private final String headerName;
    private final boolean required;
    private final boolean withParam;

    ComputedHeaderValueFiller(final String methodSpecifierString,
                              final String headerName,
                              final boolean required,
                              final Class<?> interfaceClass,
                              final Object clientProxy) {
        this.required = required;
        this.headerName = headerName;

        String methodSpecifier =
                methodSpecifierString.substring(1, methodSpecifierString.length() - 1);
        method = resolveMethod(methodSpecifier, interfaceClass);

        methodHandle = method.isDefault() ? createMethodHandle(method, clientProxy) : null;
        withParam = method.getParameterCount() == 1;
    }

    private MethodHandle createMethodHandle(final Method method, final Object clientProxy) {
        try {
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class);
            constructor.setAccessible(true);
            MethodHandles.Lookup lookup = constructor.newInstance(method.getDeclaringClass());
            return lookup
                    .in(method.getDeclaringClass())
                    .unreflectSpecial(method, method.getDeclaringClass())
                    .bindTo(clientProxy);
        } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            throw new RestClientDefinitionException("Failed to generate method handle for " + method, e);
        }
    }

    private Method resolveMethod(String methodSpecifier,
                                 Class<?> interfaceClass) {
        int lastDot = methodSpecifier.lastIndexOf('.');
        if (lastDot == methodSpecifier.length()) {
            throw new RestClientDefinitionException("Invalid string to specify method: " + methodSpecifier +
                    " for header: '" + headerName + "' on class " + interfaceClass.getCanonicalName());
        }
        String methodName;
        Class<?> clazz;
        if (lastDot > -1) { // class.method specified
            methodName = methodSpecifier.substring(lastDot + 1);

            String className = methodSpecifier.substring(0, lastDot);
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try {
                clazz = Class.forName(className, true, loader);
            } catch (ClassNotFoundException e) {
                throw new RestClientDefinitionException("No class '" + className + "' found " +
                        "for filling header '" + headerName + " on " + interfaceClass.getCanonicalName(),
                        e);
            }
        } else {
            clazz = interfaceClass;
            methodName = methodSpecifier;
        }

        Method method = null;
        boolean resolved = false;
        try {
            method = clazz.getMethod(methodName);
            resolved = true;
        } catch (NoSuchMethodException ignored) {
        }
        if (!resolved) {
            try {
                method = clazz.getMethod(methodName, String.class);
                resolved = true;
            } catch (NoSuchMethodException ignored) {
            }
        }
        if (resolved) {
            return method;
        } else {
            throw new RestClientDefinitionException("Could not resolve method '" + methodSpecifier
                    + "' for filling header '" + headerName + " on " + interfaceClass.getCanonicalName());
        }
    }


    List<String> generateValues() {
        try {
            Object result;
            if (methodHandle != null) {
                if (withParam) {
                    result = methodHandle.invokeWithArguments(headerName);
                } else {
                    result = methodHandle.invokeWithArguments();
                }
            } else if (withParam) {
                result = method.invoke(null, headerName);
            } else {
                result = method.invoke(null);
            }

            if (result instanceof String[]) {
                return Arrays.asList((String[]) result);
            } else if (result instanceof List) {
                return castListToListOfStrings((List<?>) result);
            } else {
                return Collections.singletonList(String.valueOf(result));
            }
        } catch (Throwable e) {
            if (required) {
                throw new ClientHeaderFillingException("Failed to invoke header generation method: " + method, e);
            } else {
                LOGGER.warnv(e, "Invoking header generation method {0} failed", method.toString());
            }
        }
        return Collections.emptyList();
    }

    private List<String> castListToListOfStrings(List<?> result) {
        return result.stream()
                .map(val -> val instanceof String
                        ? (String) val
                        : String.valueOf(val))
                .collect(Collectors.toList());
    }
}
