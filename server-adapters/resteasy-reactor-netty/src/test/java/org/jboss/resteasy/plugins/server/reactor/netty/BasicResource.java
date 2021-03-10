package org.jboss.resteasy.plugins.server.reactor.netty;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/basic")
public class BasicResource {

    @GET
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
