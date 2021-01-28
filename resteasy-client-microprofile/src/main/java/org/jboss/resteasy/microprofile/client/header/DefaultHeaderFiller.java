package org.jboss.resteasy.microprofile.client.header;

import org.jboss.logging.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.jboss.resteasy.microprofile.client.header.HeaderUtils.castListToListOfStrings;
import static org.jboss.resteasy.microprofile.client.header.HeaderUtils.createMethodHandle;
import static org.jboss.resteasy.microprofile.client.header.HeaderUtils.resolveMethod;

/**
 * Generates a value for dynamically computed headers (using {someMethod} as value in {@link org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam})
 */
class DefaultHeaderFiller implements HeaderFiller {

    private static final Logger LOGGER = Logger.getLogger(DefaultHeaderFiller.class);

    private final Method method;
    private final MethodHandle methodHandle;
    private final String headerName;
    private final boolean required;
    private final boolean withParam;

    DefaultHeaderFiller(final String methodSpecifierString,
                        final String headerName,
                        final boolean required,
                        final Class<?> interfaceClass,
                        final Object clientProxy) {
        this.required = required;
        this.headerName = headerName;

        String methodSpecifier =
                methodSpecifierString.substring(1, methodSpecifierString.length() - 1);
        method = resolveMethod(methodSpecifier, interfaceClass, headerName);

        methodHandle = method.isDefault() ? createMethodHandle(method, clientProxy) : null;
        withParam = method.getParameterCount() == 1;
    }


    public List<String> generateValues() {
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
}
