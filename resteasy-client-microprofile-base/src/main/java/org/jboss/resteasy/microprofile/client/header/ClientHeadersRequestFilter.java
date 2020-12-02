package org.jboss.resteasy.microprofile.client.header;

import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.eclipse.microprofile.rest.client.ext.DefaultClientHeadersFactoryImpl;
import org.jboss.resteasy.microprofile.client.impl.MpClientInvocation;
import org.jboss.resteasy.microprofile.client.utils.ClientRequestContextUtils;

import javax.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.jboss.resteasy.microprofile.client.utils.ListCastUtils.castToListOfStrings;


/**
 * First the headers from `@ClientHeaderParam` annotations are applied,
 * they can be overwritten by JAX-RS `@HeaderParam` (coming in the `requestContext`)
 *
 * Then, if a `ClientHeadersFactory` is defined, all the headers, together with incoming container headers,
 * are passed to it and it can overwrite them.
 */
@Priority(Integer.MIN_VALUE)
public class ClientHeadersRequestFilter implements ClientRequestFilter {

    private static final MultivaluedMap<String, String> EMPTY_MAP = new MultivaluedHashMap<>();

    @Override
    public void filter(ClientRequestContext requestContext) {
        Method method = ClientRequestContextUtils.getMethod(requestContext);

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        Optional<ClientHeaderProvider> handler = ClientHeaderProviders.getProvider(method);
        handler.ifPresent(h -> h.addHeaders(headers));

        Optional<ClientHeadersFactory> factory = ClientHeaderProviders.getFactory(ClientRequestContextUtils.getDeclaringClass(requestContext));

        requestContext.getHeaders().forEach(
                (key, values) -> headers.put(key, castToListOfStrings(values))
        );

        @SuppressWarnings("unchecked")
        MultivaluedMap<String,String> containerHeaders = (MultivaluedMap<String, String>) requestContext.getProperty(MpClientInvocation.CONTAINER_HEADERS);
        if(containerHeaders == null)
            containerHeaders = EMPTY_MAP;
        // stupid final rules
        MultivaluedMap<String,String> incomingHeaders = containerHeaders;

        if (!factory.isPresent() || factory.get() instanceof DefaultClientHeadersFactoryImpl) {
            // When using the default factory, pass the proposed outgoing headers onto the request context.
            // Propagation with the default factory will then overwrite any values if required.
            headers.forEach((key, values) -> requestContext.getHeaders().put(key, castToListOfObjects(values)));
        }

        factory.ifPresent(f -> f.update(incomingHeaders, headers)
                .forEach((key, values) -> requestContext.getHeaders().put(key, castToListOfObjects(values)))
        );
    }

    private static List<Object> castToListOfObjects(List<String> values) {
        return new ArrayList<>(values);
    }
}
