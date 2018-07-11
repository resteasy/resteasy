package org.jboss.resteasy.test.resource.param.resource;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.*;

@RequestScoped
public class MultiValuedParamCdiResource {

    @Path("queryParam")
    public static class QueryParamResource {

        // http://xxx/queryParam/customConversionCdi_list?person=George,Jack,John
        @GET
        @Path("customConversionCdi_list")
        public Response customConversionCdi_list(@QueryParam("person") List<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversionCdi_arrayList?person=George,Jack,John
        @GET
        @Path("customConversionCdi_arrayList")
        public Response customConversionCdi_arrayList(@QueryParam("person") ArrayList<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversionCdi_set?person=George,Jack,John
        @GET
        @Path("customConversionCdi_set")
        public Response customConversionCdi_set(@QueryParam("person") Set<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversionCdi_hashSet?person=George,Jack,John
        @GET
        @Path("customConversionCdi_hashSet")
        public Response customConversionCdi_hashSet(@QueryParam("person") HashSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversionCdi_sortedSet?person=George,Jack,John
        @GET
        @Path("customConversionCdi_sortedSet")
        public Response customConversionCdi_sortedSet(@QueryParam("person") SortedSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversionCdi_treeSet?person=George,Jack,John
        @GET
        @Path("customConversionCdi_treeSet")
        public Response customConversionCdi_treeSet(@QueryParam("person") TreeSet<PersonWithConverter> people) {
            return Response.ok(formatPeopleWithConverter(people)).build();
        }

        // http://xxx/queryParam/customConversionCdi_array?person=George,Jack,John
        @GET
        @Path("customConversionCdi_array")
        public Response customConversionCdi_array(@QueryParam("person") PersonWithConverter[] people) {
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
