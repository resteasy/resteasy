package org.jboss.resteasy.test.context.resource;

import java.util.stream.Collectors;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.concurrent.ThreadContexts;

/**
 * Builds a client with a {@link ClientThreadContext} registered and a second client without it, inside a request.
 * Returns the thread contexts of one of them, or of the deployment.
 * <p>
 * The entity is a comma separated list of the class names of the thread contexts.
 * </p>
 *
 * @since 8.0
 */
@Path("/client-thread-context")
@RequestScoped
public class ClientThreadContextResource {

    @Inject
    private Client injectedClient;

    /**
     * Returns the thread contexts after a client registered a {@link ClientThreadContext}.
     *
     * @param registration {@code class} to register the type or {@code instance} to register a singleton
     * @param scope        {@code registered} for the registering client, {@code new-client} for a client built
     *                     afterward, {@code injected} for the CDI-injected client or {@code server} for the
     *                     deployment
     *
     * @return a comma separated list of the class names of the thread contexts
     */
    @GET
    @Path("{registration}/{scope}")
    @Produces(MediaType.TEXT_PLAIN)
    public String threadContexts(@PathParam("registration") final String registration,
            @PathParam("scope") final String scope) {
        final ClientBuilder builder = switch (registration) {
            case "class" -> ClientBuilder.newBuilder().register(ClientThreadContext.class);
            case "instance" -> ClientBuilder.newBuilder().register(new ClientThreadContext());
            default -> throw new NotFoundException("Unknown registration %s".formatted(registration));
        };
        try (
                Client registered = builder.build();
                Client newClient = ClientBuilder.newBuilder().build()) {
            final ThreadContexts threadContexts = switch (scope) {
                case "registered" -> threadContexts(registered);
                case "new-client" -> threadContexts(newClient);
                case "injected" -> threadContexts(injectedClient);
                case "server" -> ResteasyProviderFactory.getInstance().getThreadContexts();
                default -> throw new NotFoundException("Unknown scope %s".formatted(scope));
            };
            return threadContexts.getThreadContexts()
                    .stream()
                    .map(context -> context.getClass().getName())
                    .collect(Collectors.joining(","));
        }
    }

    private static ThreadContexts threadContexts(final Client client) {
        return ((ClientConfiguration) client.getConfiguration()).getThreadContexts();
    }
}
