package org.jboss.resteasy.test.resource.param.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/")
public interface MultiValuedParamCustomConversionResourceClient {

    @Path("queryParam")
    QueryParamResourceClient queryParam();

    @Path("headerParam")
    HeaderParamResourceClient headerParam();

    @Path("matrixParam")
    MatrixParamResourceClient matrixParam();

    @Path("cookieParam")
    CookieParamResourceClient cookieParam();

    @Path("pathParam")
    PathParamResourceClient pathParam();

    @Path("/")
    interface QueryParamResourceClient {

        @GET
        @Path("customConversion_list")
        String customConversion_list(@QueryParam("person") String people);

        @GET
        @Path("customConversion_arrayList")
        String customConversion_arrayList(@QueryParam("person") String people);

        @GET
        @Path("customConversion_set")
        String customConversion_set(@QueryParam("person") String people);

        @GET
        @Path("customConversion_hashSet")
        String customConversion_hashSet(@QueryParam("person") String people);

        @GET
        @Path("customConversion_sortedSet")
        String customConversion_sortedSet(@QueryParam("person") String people);

        @GET
        @Path("customConversion_treeSet")
        String customConversion_treeSet(@QueryParam("person") String people);

        @GET
        @Path("customConversion_array")
        String customConversion_array(@QueryParam("person") String people);
        
    }

    @Path("/")
    interface HeaderParamResourceClient {

        @GET
        @Path("customConversion_list")
        String customConversion_list(@HeaderParam("person") String people);

        @GET
        @Path("customConversion_arrayList")
        String customConversion_arrayList(@HeaderParam("person") String people);

        @GET
        @Path("customConversion_set")
        String customConversion_set(@HeaderParam("person") String people);

        @GET
        @Path("customConversion_hashSet")
        String customConversion_hashSet(@HeaderParam("person") String people);

        @GET
        @Path("customConversion_sortedSet")
        String customConversion_sortedSet(@HeaderParam("person") String people);

        @GET
        @Path("customConversion_treeSet")
        String customConversion_treeSet(@HeaderParam("person") String people);

        @GET
        @Path("customConversion_array")
        String customConversion_array(@HeaderParam("person") String people);

    }

    @Path("/")
    interface MatrixParamResourceClient {

        @GET
        @Path("customConversion_list")
        String customConversion_list(@MatrixParam("person") String people);

        @GET
        @Path("customConversion_arrayList")
        String customConversion_arrayList(@MatrixParam("person") String people);;

        @GET
        @Path("customConversion_set")
        String customConversion_set(@MatrixParam("person") String people);

        @GET
        @Path("customConversion_hashSet")
        String customConversion_hashSet(@MatrixParam("person") String people);

        @GET
        @Path("customConversion_sortedSet")
        String customConversion_sortedSet(@MatrixParam("person") String people);

        @GET
        @Path("customConversion_treeSet")
        String customConversion_treeSet(@MatrixParam("person") String people);

        @GET
        @Path("customConversion_array")
        String customConversion_array(@MatrixParam("person") String people);

    }

    @Path("/")
    interface CookieParamResourceClient {

        @GET
        @Path("customConversion_list")
        String customConversion_list(@CookieParam("person") String people);

        @GET
        @Path("customConversion_arrayList")
        String customConversion_arrayList(@CookieParam("person") String people);

        @GET
        @Path("customConversion_set")
        String customConversion_set(@CookieParam("person") String people);

        @GET
        @Path("customConversion_hashSet")
        String customConversion_hashSet(@CookieParam("person") String people);

        @GET
        @Path("customConversion_sortedSet")
        String customConversion_sortedSet(@CookieParam("person") String people);

        @GET
        @Path("customConversion_treeSet")
        String customConversion_treeSet(@CookieParam("person") String people);

        @GET
        @Path("customConversion_array")
        String customConversion_array(@CookieParam("person") String people);

    }

    @Path("/")
    interface PathParamResourceClient {
        
        @GET
        @Path("customConversion_list/{person}")
        String customConversion_list(@PathParam("person") String people);

        @GET
        @Path("customConversion_arrayList/{person}")
        String customConversion_arrayList(@PathParam("person") String people);

        @GET
        @Path("customConversion_set/{person}")
        String customConversion_set(@PathParam("person") String people);

        @GET
        @Path("customConversion_hashSet/{person}")
        String customConversion_hashSet(@PathParam("person") String people);

        @GET
        @Path("customConversion_sortedSet/{person}")
        String customConversion_sortedSet(@PathParam("person") String people);

        @GET
        @Path("customConversion_treeSet/{person}")
        String customConversion_treeSet(@PathParam("person") String people);

        @GET
        @Path("customConversion_array/{person}")
        String customConversion_array(@PathParam("person") String people);

    }

}
