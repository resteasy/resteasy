package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.List;

@Path("/")
public class WebTargetResource {
   @GET
   @Path("/users/{username}/{id}")
   @Produces("text/plain")
   public String get(@PathParam("username") String username, @PathParam("id") String id) {
      return "username: " + username + ", " + id;
   }

   @GET
   @Path("/users/{username}/{id}/{question}/{question}")
   @Produces("text/plain")
   public String getMultiple(@PathParam("username") String username, @PathParam("id") String id, @PathParam("question") String q) {
      return "username: " + username + ", " + id + ", " + q;
   }

   @GET
   @Path("/users/{username}/param/{id}")
   @Produces("text/plain")
   public String getParam(@PathParam("username") String username, @PathParam("id") String id, @QueryParam("q") List<String> q, @QueryParam("k") List<String> k) {
      return "username: " + username + ", " + id + ", q: " + q.toString() + ", k: " + k.toString();
   }

   @GET
   @Path("/users/{username}/matrix/{id}")
   @Produces("text/plain")
   public String getParamMatrix(@PathParam("username") String username, @PathParam("id") String id, @MatrixParam("m1") List<String> m1, @MatrixParam("m2") List<String> m2) {
      return "username: " + username + ", " + id + ", m1: " + m1.toString() + ", m2: " + m2.toString();
   }

   @GET
   @Path("/users/{username}/matrix/{id}/path")
   @Produces("text/plain")
   public String getParamMatrixPath(@PathParam("username") String username, @PathParam("id") String id, @MatrixParam("m1") List<String> m1, @MatrixParam("m2") List<String> m2) {
      return "username: " + username + ", " + id + ", m1: " + m1.toString() + ", m2: " + m2.toString();
   }
}
