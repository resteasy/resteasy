package org.jboss.resteasy.microprofile.client.header;

import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;

import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class ClientHeaderProvider {

    static Optional<ClientHeaderProvider> forMethod(final Method method, final Object clientProxy) {
        Class<?> declaringClass = method.getDeclaringClass();

        ClientHeaderParam[] methodAnnotations = method.getAnnotationsByType(ClientHeaderParam.class);
        ClientHeaderParam[] classAnnotations = declaringClass.getAnnotationsByType(ClientHeaderParam.class);

        Map<String, ClientHeaderValueGenerator> generators = new HashMap<>();

        for (ClientHeaderParam annotation : methodAnnotations) {
            if (generators.containsKey(annotation.name())) {
                throw new RestClientDefinitionException("Duplicate " + ClientHeaderParam.class.getSimpleName() +
                        " annotation definitions found on " + method.toString());
            }
            generators.put(annotation.name(), new ClientHeaderValueGenerator(annotation, declaringClass, clientProxy));
        }

        checkForDuplicateClassLevelAnnotations(classAnnotations, declaringClass);

        Stream.of(classAnnotations)
                .filter(a -> !generators.containsKey(a.name()))
                .forEach(a -> generators.put(a.name(), new ClientHeaderValueGenerator(a, declaringClass, clientProxy)));

        return generators.isEmpty()
                ? Optional.empty()
                : Optional.of(new ClientHeaderProvider(generators.values()));
    }

    private static void checkForDuplicateClassLevelAnnotations(final ClientHeaderParam[] classAnnotations, final Class<?> declaringClass) {
        Set<String> headerNames = new HashSet<>();
        Arrays.stream(classAnnotations)
                .map(ClientHeaderParam::name)
                .forEach(
                        name -> {
                            if (!headerNames.add(name)) {
                                throw new RestClientDefinitionException(
                                        "Duplicate ClientHeaderParam definition for header name " + name + " on class "
                                                + declaringClass.getCanonicalName());
                            }
                        }
                );
    }

    private final Collection<ClientHeaderValueGenerator> generators;

    ClientHeaderProvider(final Collection<ClientHeaderValueGenerator> generators) {
        this.generators = generators;
    }

    public void addHeaders(MultivaluedMap<String, String> headers) {
        generators.forEach(g -> g.fillHeaders(headers));
    }

}
