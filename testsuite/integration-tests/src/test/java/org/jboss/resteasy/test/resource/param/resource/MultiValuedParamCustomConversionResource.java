package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.*;

public class MultiValuedParamCustomConversionResource {

    @Path("queryParam")
    public static class QueryParamResource {

        // http://xxx/queryParam/customConversion_list?person=George,Jack,John
        @GET
        @Path("customConversion_list")
        public Response customConversion_list(@QueryParam("person") List<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversion_arrayList?person=George,Jack,John
        @GET
        @Path("customConversion_arrayList")
        public Response customConversion_arrayList(@QueryParam("person") ArrayList<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversion_set?person=George,Jack,John
        @GET
        @Path("customConversion_set")
        public Response customConversion_set(@QueryParam("person") Set<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversion_hashSet?person=George,Jack,John
        @GET
        @Path("customConversion_hashSet")
        public Response customConversion_hashSet(@QueryParam("person") HashSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversion_sortedSet?person=George,Jack,John
        @GET
        @Path("customConversion_sortedSet")
        public Response customConversion_sortedSet(@QueryParam("person") SortedSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversion_treeSet?person=George,Jack,John
        @GET
        @Path("customConversion_treeSet")
        public Response customConversion_treeSet(@QueryParam("person") TreeSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversion_array?person=George,Jack,John
        @GET
        @Path("customConversion_array")
        public Response customConversion_array(@QueryParam("person") PersonWithConverter[] people) {
            return Response.ok(formatPeopleWithConverter(Arrays.asList(people))).build();
        }
    }

    @Path("headerParam")
    public static class HeaderParamResource {

        // person:George,Jack,John
        @GET
        @Path("customConversion_list")
        public Response customConversion_list(@HeaderParam("person") List<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_arrayList")
        public Response customConversion_arrayList(@HeaderParam("person") ArrayList<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_set")
        public Response customConversion_set(@HeaderParam("person") Set<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_hashSet")
        public Response customConversion_hashSet(@HeaderParam("person") HashSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_sortedSet")
        public Response customConversion_sortedSet(@HeaderParam("person") SortedSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_treeSet")
        public Response customConversion_treeSet(@HeaderParam("person") TreeSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_array")
        public Response customConversion_array(@HeaderParam("person") PersonWithConverter[] people) {
            return Response.ok(formatPeopleWithConverter(Arrays.asList(people))).build();
        }
    }

    @Path("matrixParam")
    public static class MatrixParamResource {

        // person:George,Jack,John
        @GET
        @Path("customConversion_list")
        public Response customConversion_list(@MatrixParam("person") List<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_arrayList")
        public Response customConversion_arrayList(@MatrixParam("person") ArrayList<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_set")
        public Response customConversion_set(@MatrixParam("person") Set<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_hashSet")
        public Response customConversion_hashSet(@MatrixParam("person") HashSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_sortedSet")
        public Response customConversion_sortedSet(@MatrixParam("person") SortedSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_treeSet")
        public Response customConversion_treeSet(@MatrixParam("person") TreeSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_array")
        public Response customConversion_array(@MatrixParam("person") PersonWithConverter[] people) {
            return Response.ok(formatPeopleWithConverter(Arrays.asList(people))).build();
        }
    }

    @Path("cookieParam")
    public static class CookieParamResource {

        // person:George,Jack,John
        @GET
        @Path("customConversion_list")
        public Response customConversion_list(@CookieParam("person") List<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_arrayList")
        public Response customConversion_arrayList(@CookieParam("person") ArrayList<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_set")
        public Response customConversion_set(@CookieParam("person") Set<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_hashSet")
        public Response customConversion_hashSet(@CookieParam("person") HashSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_sortedSet")
        public Response customConversion_sortedSet(@CookieParam("person") SortedSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_treeSet")
        public Response customConversion_treeSet(@CookieParam("person") TreeSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_array")
        public Response customConversion_array(@CookieParam("person") PersonWithConverter[] people) {
            return Response.ok(formatPeopleWithConverter(Arrays.asList(people))).build();
        }
    }

    @Path("pathParam")
    public static class PathParamResource {

        // person:George,Jack,John
        @GET
        @Path("customConversion_list/{person}")
        public Response customConversion_list(@PathParam("person") List<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_arrayList/{person}")
        public Response customConversion_arrayList(@PathParam("person") ArrayList<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_set/{person}")
        public Response customConversion_set(@PathParam("person") Set<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_hashSet/{person}")
        public Response customConversion_hashSet(@PathParam("person") HashSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_sortedSet/{person}")
        public Response customConversion_sortedSet(@PathParam("person") SortedSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_treeSet/{person}")
        public Response customConversion_treeSet(@PathParam("person") TreeSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // person:George,Jack,John
        @GET
        @Path("customConversion_array/{person}")
        public Response customConversion_array(@PathParam("person") PersonWithConverter[] people) {
            return Response.ok(formatPeopleWithConverter(Arrays.asList(people))).build();
        }
    }

    private static String formatPeopleWithConverter(Collection<PersonWithConverter> people) {
        StringBuilder stringBuilder = new StringBuilder();
        int personCount = people.size();
        int i = 0;
        for (PersonWithConverter person : new TreeSet<>(people)) {
            stringBuilder.append(person.toString());
            ++i;
            if (i < personCount) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }
}
