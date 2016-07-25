package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public interface ParameterListInterface {
    @GET
    @Path("matrix/list")
    Response matrixList(@MatrixParam("m1") List<String> list);

    @GET
    @Path("matrix/set")
    Response matrixSet(@MatrixParam("m1") Set<String> set);

    @GET
    @Path("matrix/sortedset")
    Response matrixSortedSet(@MatrixParam("m1") SortedSet<String> set);

    @PUT
    @Consumes("text/plain")
    @Path("matrix/entity")
    Response matrixWithEntity(@MatrixParam("m1") List<String> list, String entity);

    @GET
    @Path("query/list")
    Response queryList(@QueryParam("q1") List<String> list);

    @GET
    @Path("query/set")
    Response querySet(@QueryParam("q1") Set<String> set);

    @GET
    @Path("query/sortedset")
    Response querySortedSet(@QueryParam("q1") SortedSet<String> set);

    @PUT
    @Consumes("text/plain")
    @Path("query/entity")
    Response queryWithEntity(@QueryParam("q1") List<String> list, String entity);

    @PUT
    @Consumes("text/plain")
    @Path("matrix/query/entity")
    Response matrixQueryWithEntity(@MatrixParam("m1") List<String> matrixParams, @QueryParam("q1") List<String> queryParams, String entity);
}
