package org.jboss.resteasy.test.providers.yaml.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Path("/message")
@ApplicationScoped
public class MessageResource {
    private final Map<String, Message> messages = new ConcurrentHashMap<>();

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces("text/yaml")
    public Map<String, Message> messages() throws InterruptedException {
        return new HashMap<>(messages);
    }

    @GET
    @Produces("text/yaml")
    @Path("/{id}")
    public Response get(@PathParam("id") final String id) {
        final Message message = messages.get(id);
        return message == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(message).build();
    }

    @GET
    @Path("/check/attacked")
    public boolean attacked() {
        return AttackVector.CONSTRUCTOR_INVOKED.get();
    }

    @GET
    @Path("/check/static/attacked")
    public boolean staticAttacked() {
        return AttackVector.STATIC_BLOCK_INVOKED.get();
    }

    @POST
    @Consumes("text/yaml")
    @Path("/string")
    public Response post(final String payload) {
        final Message message = new Message();
        message.setText(payload);
        return post(message);
    }

    @POST
    @Consumes("text/yaml")
    @Path("/list")
    public Response post(final List<Message> payload) {
        payload.forEach(this::addMessage);
        return Response.created(uriInfo.getBaseUriBuilder().path("/message/").build()).build();
    }

    @POST
    @Consumes("text/yaml")
    @Path("/set")
    public Response post(final Set<Message> payload) {
        payload.forEach(this::addMessage);
        return Response.created(uriInfo.getBaseUriBuilder().path("/message/").build()).build();
    }

    @POST
    @Consumes("text/yaml")
    public Response post(final Message message) {
        final String id = addMessage(message);
        return Response.created(uriInfo.getBaseUriBuilder().path("/message/" + id).build()).build();
    }

    @PUT
    @Consumes("text/yaml")
    @Path("/all")
    public Response put(final Map<String, Message> payload) {
        messages.putAll(payload);
        return Response.created(uriInfo.getBaseUriBuilder().path("/message/").build()).build();
    }

    @DELETE
    public Response reset() {
        messages.clear();
        AttackVector.CONSTRUCTOR_INVOKED.set(false);
        AttackVector.STATIC_BLOCK_INVOKED.set(false);
        return Response.noContent().build();
    }

    private String addMessage(final Message message) {
        final String id = UUID.randomUUID().toString();
        messages.put(id, message);
        return id;
    }
}
