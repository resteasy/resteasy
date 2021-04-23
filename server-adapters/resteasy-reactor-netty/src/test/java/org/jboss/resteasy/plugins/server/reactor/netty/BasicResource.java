package org.jboss.resteasy.plugins.server.reactor.netty;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/basic")
public class BasicResource {

    @GET
    @Produces("text/plain")
    public String get() {
        return "Hello world!";
    }

    @PUT
    public String post(String input) {
        return "PUT " + input;
    }

    @POST
    public String put(String input) {
        return "POST " + input;
    }

    @DELETE
    public String delete(String input) {
        return "DELETE " + input;
    }

    @PATCH
    public String patch(String input) {
        return "PATCH " + input;
    }

    @GET
    @Path("/pojo")
    @Produces(MediaType.APPLICATION_JSON)
    public Pojo pojo() {
        return new Pojo();
    }

    public static class Pojo {
        private int answer = 42;

        public int getAnswer() {
            return answer;
        }

        public void setAnswer(int answer) {
            this.answer = answer;
        }
    }
}
