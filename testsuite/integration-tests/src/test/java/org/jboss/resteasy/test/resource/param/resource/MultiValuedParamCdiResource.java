package org.jboss.resteasy.test.resource.param.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("queryParam")
public class MultiValuedParamCdiResource {

    // http://xxx/queryParam/customConversionCdi_list?person=George,Jack,John
    @GET
    @Path("customConversionCdi_list")
    public Response customConversionCdi_list(@QueryParam("person") List<MultiValuedParamPersonWithConverter> people) {
        return Response.ok(formatPeopleWithConverter(people)).build();
    }

    // http://xxx/queryParam/customConversionCdi_arrayList?person=George,Jack,John
    @GET
    @Path("customConversionCdi_arrayList")
    public Response customConversionCdi_arrayList(@QueryParam("person") ArrayList<MultiValuedParamPersonWithConverter> people) {
        return Response.ok(formatPeopleWithConverter(people)).build();
    }

    // http://xxx/queryParam/customConversionCdi_set?person=George,Jack,John
    @GET
    @Path("customConversionCdi_set")
    public Response customConversionCdi_set(@QueryParam("person") Set<MultiValuedParamPersonWithConverter> people) {
        return Response.ok(formatPeopleWithConverter(people)).build();
    }

    // http://xxx/queryParam/customConversionCdi_hashSet?person=George,Jack,John
    @GET
    @Path("customConversionCdi_hashSet")
    public Response customConversionCdi_hashSet(@QueryParam("person") HashSet<MultiValuedParamPersonWithConverter> people) {
        return Response.ok(formatPeopleWithConverter(people)).build();
    }

    // http://xxx/queryParam/customConversionCdi_sortedSet?person=George,Jack,John
    @GET
    @Path("customConversionCdi_sortedSet")
    public Response customConversionCdi_sortedSet(@QueryParam("person") SortedSet<MultiValuedParamPersonWithConverter> people) {
        return Response.ok(formatPeopleWithConverter(people)).build();
    }

    // http://xxx/queryParam/customConversionCdi_treeSet?person=George,Jack,John
    @GET
    @Path("customConversionCdi_treeSet")
    public Response customConversionCdi_treeSet(@QueryParam("person") TreeSet<MultiValuedParamPersonWithConverter> people) {
        return Response.ok(formatPeopleWithConverter(people)).build();
    }

    // http://xxx/queryParam/customConversionCdi_array?person=George,Jack,John
    @GET
    @Path("customConversionCdi_array")
    public Response customConversionCdi_array(@QueryParam("person") MultiValuedParamPersonWithConverter[] people) {
        return Response.ok(formatPeopleWithConverter(Arrays.asList(people))).build();
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
