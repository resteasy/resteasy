package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

@Path("/")
public class ParameterListResource implements ParameterListInterface {
   @Override
   @GET
   @Path("matrix/list")
   public Response matrixList(@MatrixParam("m1") List<String> list) {
      StringBuilder sb = new StringBuilder();
      for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
         sb.append(it.next()).append(":");
      }
      return Response.ok().entity(sb.toString()).build();
   }

   @Override
   @GET
   @Path("matrix/set")
   public Response matrixSet(@MatrixParam("m1") Set<String> set) {
      List<String> list = new ArrayList<String>(set);
      Collections.sort(list);
      StringBuilder sb = new StringBuilder();
      for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
         sb.append(it.next()).append(":");
      }
      return Response.ok().entity(sb.toString()).build();
   }

   @Override
   @GET
   @Path("matrix/sortedset")
   public Response matrixSortedSet(@MatrixParam("m1") SortedSet<String> set) {
      StringBuilder sb = new StringBuilder();
      for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
         sb.append(it.next()).append(":");
      }
      return Response.ok().entity(sb.toString()).build();
   }

   @Override
   @PUT
   @Consumes("text/plain")
   @Path("matrix/entity")
   public Response matrixWithEntity(@MatrixParam("m1") List<String> list, String entity) {
      StringBuilder sb = new StringBuilder(entity + ":");
      for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
         sb.append(it.next()).append(":");
      }
      return Response.ok().entity(sb.toString()).build();
   }

   @Override
   @GET
   @Path("query/list")
   public Response queryList(@QueryParam("q1") List<String> list) {
      StringBuilder sb = new StringBuilder();
      for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
         sb.append(it.next()).append(":");
      }
      return Response.ok().entity(sb.toString()).build();
   }

   @Override
   @GET
   @Path("query/set")
   public Response querySet(@QueryParam("q1") Set<String> set) {
      List<String> list = new ArrayList<String>(set);
      Collections.sort(list);
      StringBuilder sb = new StringBuilder();
      for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
         sb.append(it.next()).append(":");
      }
      return Response.ok().entity(sb.toString()).build();
   }

   @Override
   @GET
   @Path("query/sortedset")
   public Response querySortedSet(@QueryParam("q1") SortedSet<String> set) {
      StringBuilder sb = new StringBuilder();
      for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
         sb.append(it.next()).append(":");
      }
      return Response.ok().entity(sb.toString()).build();
   }

   @Override
   @PUT
   @Consumes("text/plain")
   @Path("query/entity")
   public Response queryWithEntity(@QueryParam("q1") List<String> list, String entity) {
      StringBuilder sb = new StringBuilder(entity + ":");
      for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
         sb.append(it.next()).append(":");
      }
      return Response.ok().entity(sb.toString()).build();
   }

   @Override
   @PUT
   @Consumes("text/plain")
   @Path("matrix/query/entity")
   public Response matrixQueryWithEntity(@MatrixParam("m1") List<String> matrixParams, @QueryParam("q1") List<String> queryParams, String entity) {
      StringBuilder sb = new StringBuilder(entity + ":");
      for (Iterator<String> it = matrixParams.iterator(); it.hasNext(); ) {
         sb.append(it.next()).append(":");
      }
      for (Iterator<String> it = queryParams.iterator(); it.hasNext(); ) {
         sb.append(it.next()).append(":");
      }
      return Response.ok().entity(sb.toString()).build();
   }
}
