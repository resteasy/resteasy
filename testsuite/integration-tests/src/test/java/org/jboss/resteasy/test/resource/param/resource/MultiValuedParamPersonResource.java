package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MultiValuedParamPersonResource {

    @Path("queryParam")
    public static class QueryParamResource {

        // http://xxx/queryParam/customConversion_list?person=George,Jack,John
        @GET
        @Path("customConversion_list")
        public Response customConversion_list(@QueryParam("person") List<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversion_arrayList?person=George,Jack,John
        @GET
        @Path("customConversion_arrayList")
        public Response customConversion_arrayList(@QueryParam("person") ArrayList<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversion_set?person=George,Jack,John
        @GET
        @Path("customConversion_set")
        public Response customConversion_set(@QueryParam("person") Set<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversion_hashSet?person=George,Jack,John
        @GET
        @Path("customConversion_hashSet")
        public Response customConversion_hashSet(@QueryParam("person") HashSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversion_sortedSet?person=George,Jack,John
        @GET
        @Path("customConversion_sortedSet")
        public Response customConversion_sortedSet(@QueryParam("person") SortedSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversion_treeSet?person=George,Jack,John
        @GET
        @Path("customConversion_treeSet")
        public Response customConversion_treeSet(@QueryParam("person") TreeSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversion_array?person=George,Jack,John
        @GET
        @Path("customConversion_array")
        public Response customConversion_array(@QueryParam("person") MultiValuedParamPersonWithConverter[] people) {
            return Response.ok(formatPeopleWithConverter(Arrays.asList(people))).build();
        }
    }

    @Path("headerParam")
    public static class HeaderParamResource {

        // person:George,Jack,John
        @GET
        @Path("customConversion_list")
        public Response customConversion_list(@HeaderParam("person") List<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_arrayList")
        public Response customConversion_arrayList(@HeaderParam("person") ArrayList<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_set")
        public Response customConversion_set(@HeaderParam("person") Set<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_hashSet")
        public Response customConversion_hashSet(@HeaderParam("person") HashSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_sortedSet")
        public Response customConversion_sortedSet(@HeaderParam("person") SortedSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_treeSet")
        public Response customConversion_treeSet(@HeaderParam("person") TreeSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_array")
        public Response customConversion_array(@HeaderParam("person") MultiValuedParamPersonWithConverter[] people) {
            return Response.ok(formatPeopleWithConverter(Arrays.asList(people))).build();
        }
    }

    @Path("matrixParam")
    public static class MatrixParamResource {

        // person:George,Jack,John
        @GET
        @Path("customConversion_list")
        public Response customConversion_list(@MatrixParam("person") List<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_arrayList")
        public Response customConversion_arrayList(@MatrixParam("person") ArrayList<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_set")
        public Response customConversion_set(@MatrixParam("person") Set<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_hashSet")
        public Response customConversion_hashSet(@MatrixParam("person") HashSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_sortedSet")
        public Response customConversion_sortedSet(@MatrixParam("person") SortedSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_treeSet")
        public Response customConversion_treeSet(@MatrixParam("person") TreeSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_array")
        public Response customConversion_array(@MatrixParam("person") MultiValuedParamPersonWithConverter[] people) {
            return Response.ok(formatPeopleWithConverter(Arrays.asList(people))).build();
        }
    }

    @Path("cookieParam")
    public static class CookieParamResource {

        // person:George,Jack,John
        @GET
        @Path("customConversion_list")
        public Response customConversion_list(@CookieParam("person") List<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_arrayList")
        public Response customConversion_arrayList(@CookieParam("person") ArrayList<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_set")
        public Response customConversion_set(@CookieParam("person") Set<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_hashSet")
        public Response customConversion_hashSet(@CookieParam("person") HashSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_sortedSet")
        public Response customConversion_sortedSet(@CookieParam("person") SortedSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_treeSet")
        public Response customConversion_treeSet(@CookieParam("person") TreeSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_array")
        public Response customConversion_array(@CookieParam("person") MultiValuedParamPersonWithConverter[] people) {
            return Response.ok(formatPeopleWithConverter(Arrays.asList(people))).build();
        }
    }

    @Path("pathParam")
    public static class PathParamResource {

        // person:George,Jack,John
        @GET
        @Path("customConversion_list/{person}")
        public Response customConversion_list(@PathParam("person") List<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_arrayList/{person}")
        public Response customConversion_arrayList(@PathParam("person") ArrayList<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_set/{person}")
        public Response customConversion_set(@PathParam("person") Set<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_hashSet/{person}")
        public Response customConversion_hashSet(@PathParam("person") HashSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_sortedSet/{person}")
        public Response customConversion_sortedSet(@PathParam("person") SortedSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_treeSet/{person}")
        public Response customConversion_treeSet(@PathParam("person") TreeSet<MultiValuedParamPersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_array/{person}")
        public Response customConversion_array(@PathParam("person") MultiValuedParamPersonWithConverter[] people) {
            return Response.ok(formatPeopleWithConverter(Arrays.asList(people))).build();
        }
    }

    private static String formatPeopleWithConverter(Collection<MultiValuedParamPersonWithConverter> people) {
        StringBuilder stringBuilder = new StringBuilder();
        int personCount = people.size();
        int i = 0;
        for (MultiValuedParamPersonWithConverter person : new TreeSet<>(people)) {
            stringBuilder.append(person.toString());
            ++i;
            if (i < personCount) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }
}
