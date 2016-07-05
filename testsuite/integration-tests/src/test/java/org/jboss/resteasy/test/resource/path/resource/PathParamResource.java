package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

@Path(value = "/PathParamTest")
public class PathParamResource {
    @GET
    @Path("/{id}")
    public Response single(@PathParam("id") String id) {
        return Response.ok("single=" + id).build();
    }

    @GET
    @Path("/{id}/{id1}")
    public Response two(@PathParam("id") String id,
                        @PathParam("id1") PathSegment id1) {
        return Response.ok("double=" + id + id1.getPath()).build();
    }

    @GET
    @Path("/{id}/{id1}/{id2}")
    public Response triple(@PathParam("id") int id,
                           @PathParam("id1") PathSegment id1,
                           @PathParam("id2") float id2) {
        return Response.ok("triple=" + id + id1.getPath() + id2).build();
    }

    @GET
    @Path("/{id}/{id1}/{id2}/{id3}")
    public Response quard(@PathParam("id") double id,
                          @PathParam("id1") boolean id1,
                          @PathParam("id2") byte id2,
                          @PathParam("id3") PathSegment id3) {
        return Response.ok("quard=" + id + id1 + id2 + id3.getPath()).build();
    }

    @GET
    @Path("/{id}/{id1}/{id2}/{id3}/{id4}")
    public Response penta(@PathParam("id") long id,
                          @PathParam("id1") String id1,
                          @PathParam("id2") short id2,
                          @PathParam("id3") boolean id3,
                          @PathParam("id4") PathSegment id4) {
        return Response.ok("penta=" + id + id1 + id2 + id3 + id4.getPath()).
                build();
    }

    @Produces("text/plain")
    @GET
    @Path("/{id}/{id}/{id}/{id}/{id}/{id}")
    public Response list(@PathParam("id") List<String> id) {
        StringBuffer sb = new StringBuffer();
        sb.append("list=");
        for (String tmp : id) {
            sb.append(tmp);
        }
        return Response.ok(sb.toString()).build();
    }

    @Produces("text/plain")
    @GET
    @Path("/matrix/{id}")
    public Response matrixparamtest(@PathParam("id") PathSegment id) {
        StringBuffer sb = new StringBuffer();
        sb.append("matrix=");

        sb.append("/" + id.getPath());
        MultivaluedMap<String, String> matrix = id.getMatrixParameters();
        Set keys = matrix.keySet();
        for (Object key : keys) {
            sb.append(";" + key.toString() + "=" +
                    matrix.getFirst(key.toString()));

        }
        return Response.ok(sb.toString()).build();
    }
}
