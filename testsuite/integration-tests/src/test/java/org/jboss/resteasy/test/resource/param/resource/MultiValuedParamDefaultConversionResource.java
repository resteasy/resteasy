package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.*;

public class MultiValuedParamDefaultConversionResource {

    @Path("queryParam")
    public static class QueryParamResource {

        // http://xxx/queryParam/defaultConversionConstructor_list?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionConstructor_list")
        public Response defaultConversionConstructor_list(@QueryParam("person") List<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/queryParam/defaultConversionConstructor_arrayList?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionConstructor_arrayList")
        public Response defaultConversionConstructor_arrayList(@QueryParam("person") ArrayList<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/queryParam/defaultConversionConstructor_set?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionConstructor_set")
        public Response defaultConversionConstructor_set(@QueryParam("person") Set<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/queryParam/defaultConversionConstructor_hashSet?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionConstructor_hashSet")
        public Response defaultConversionConstructor_hashSet(@QueryParam("person") HashSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/queryParam/defaultConversionConstructor_sortedSet?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionConstructor_sortedSet")
        public Response defaultConversionConstructor_sortedSet(@QueryParam("person") SortedSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/queryParam/defaultConversionConstructor_treeSet?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionConstructor_treeSet")
        public Response defaultConversionConstructor_treeSet(@QueryParam("person") TreeSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }



        // http://xxx/queryParam/defaultConversionValueOf_list?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionValueOf_list")
        public Response defaultConversionValueOf_list(@QueryParam("person") List<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/queryParam/defaultConversionValueOf_arrayList?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionValueOf_arrayList")
        public Response defaultConversionValueOf_arrayList(@QueryParam("person") ArrayList<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/queryParam/defaultConversionValueOf_set?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionValueOf_set")
        public Response defaultConversionValueOf_set(@QueryParam("person") Set<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/queryParam/defaultConversionValueOf_hashSet?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionValueOf_hashSet")
        public Response defaultConversionValueOf_hashSet(@QueryParam("person") HashSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/queryParam/defaultConversionValueOf_sortedSet?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionValueOf_sortedSet")
        public Response defaultConversionValueOf_sortedSet(@QueryParam("person") SortedSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/queryParam/defaultConversionValueOf_treeSet?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionValueOf_treeSet")
        public Response defaultConversionValueOf_treeSet(@QueryParam("person") TreeSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }



        // http://xxx/queryParam/defaultConversionFromString_list?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionFromString_list")
        public Response defaultConversionFromString_list(@QueryParam("person") List<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // http://xxx/queryParam/defaultConversionFromString_arrayList?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionFromString_arrayList")
        public Response defaultConversionFromString_arrayList(@QueryParam("person") ArrayList<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // http://xxx/queryParam/defaultConversionFromString_set?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionFromString_set")
        public Response defaultConversionFromString_set(@QueryParam("person") Set<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // http://xxx/queryParam/defaultConversionFromString_hashSet?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionFromString_hashSet")
        public Response defaultConversionFromString_hashSet(@QueryParam("person") HashSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // http://xxx/queryParam/defaultConversionFromString_sortedSet?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionFromString_sortedSet")
        public Response defaultConversionFromString_sortedSet(@QueryParam("person") SortedSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }


        // http://xxx/queryParam/defaultConversionConverter_treeSet?person=George&person=Jack&person=John
        @GET
        @Path("defaultConversionFromString_treeSet")
        public Response defaultConversionFromString_treeSet(@QueryParam("person") TreeSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }
    }

    @Path("headerParam")
    public static class HeaderParamResource {

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionConstructor_list")
        public Response defaultConversionConstructor_list(@HeaderParam("person") List<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionConstructor_arrayList")
        public Response defaultConversionConstructor_arrayList(@HeaderParam("person") ArrayList<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionConstructor_set")
        public Response defaultConversionConstructor_set(@HeaderParam("person") Set<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionConstructor_hashSet")
        public Response defaultConversionConstructor_hashSet(@HeaderParam("person") HashSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionConstructor_sortedSet")
        public Response defaultConversionConstructor_sortedSet(@HeaderParam("person") SortedSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionConstructor_treeSet")
        public Response defaultConversionConstructor_treeSet(@HeaderParam("person") TreeSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }




        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionValueOf_list")
        public Response defaultConversionValueOf_list(@HeaderParam("person") List<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionValueOf_arrayList")
        public Response defaultConversionValueOf_arrayList(@HeaderParam("person") ArrayList<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionValueOf_set")
        public Response defaultConversionValueOf_set(@HeaderParam("person") Set<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionValueOf_hashSet")
        public Response defaultConversionValueOf_hashSet(@HeaderParam("person") HashSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionValueOf_sortedSet")
        public Response defaultConversionValueOf_sortedSet(@HeaderParam("person") SortedSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionValueOf_treeSet")
        public Response defaultConversionValueOf_treeSet(@HeaderParam("person") TreeSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }




        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionFromString_list")
        public Response defaultConversionFromString_list(@HeaderParam("person") List<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionFromString_arrayList")
        public Response defaultConversionFromString_arrayList(@HeaderParam("person") ArrayList<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionFromString_set")
        public Response defaultConversionFromString_set(@HeaderParam("person") Set<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionFromString_hashSet")
        public Response defaultConversionFromString_hashSet(@HeaderParam("person") HashSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionFromString_sortedSet")
        public Response defaultConversionFromString_sortedSet(@HeaderParam("person") SortedSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // person:George    person:Jack     person:John
        @GET
        @Path("defaultConversionFromString_treeSet")
        public Response defaultConversionFromString_treeSet(@HeaderParam("person") TreeSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }
    }

    @Path("matrixParam")
    public static class MatrixParamResource {

        // http://xxx/matrixParam/defaultConversionConstructor_list;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionConstructor_list")
        public Response defaultConversionConstructor_list(@MatrixParam("person") List<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionConstructor_arrayList;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionConstructor_arrayList")
        public Response defaultConversionConstructor_arrayList(@MatrixParam("person") ArrayList<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionConstructor_set;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionConstructor_set")
        public Response defaultConversionConstructor_set(@MatrixParam("person") Set<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionConstructor_hashSet;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionConstructor_hashSet")
        public Response defaultConversionConstructor_hashSet(@MatrixParam("person") HashSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionConstructor_sortedSet;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionConstructor_sortedSet")
        public Response defaultConversionConstructor_sortedSet(@MatrixParam("person") SortedSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionConstructor_treeSet;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionConstructor_treeSet")
        public Response defaultConversionConstructor_treeSet(@MatrixParam("person") TreeSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }




        // http://xxx/matrixParam/defaultConversionValueOf_list;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionValueOf_list")
        public Response defaultConversionValueOf_list(@MatrixParam("person") List<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionValueOf_arrayList;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionValueOf_arrayList")
        public Response defaultConversionValueOf_arrayList(@MatrixParam("person") ArrayList<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionValueOf_set;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionValueOf_set")
        public Response defaultConversionValueOf_set(@MatrixParam("person") Set<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionValueOf_hashSet;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionValueOf_hashSet")
        public Response defaultConversionValueOf_hashSet(@MatrixParam("person") HashSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionValueOf_sortedSet;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionValueOf_sortedSet")
        public Response defaultConversionValueOf_sortedSet(@MatrixParam("person") SortedSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionValueOf_treeSet;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionValueOf_treeSet")
        public Response defaultConversionValueOf_treeSet(@MatrixParam("person") TreeSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }




        // http://xxx/matrixParam/defaultConversionFromString_list;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionFromString_list")
        public Response defaultConversionFromString_list(@MatrixParam("person") List<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionFromString_arrayList;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionFromString_arrayList")
        public Response defaultConversionFromString_arrayList(@MatrixParam("person") ArrayList<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionFromString_set;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionFromString_set")
        public Response defaultConversionFromString_set(@MatrixParam("person") Set<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionFromString_hashSet;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionFromString_hashSet")
        public Response defaultConversionFromString_hashSet(@MatrixParam("person") HashSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionFromString_sortedSet;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionFromString_sortedSet")
        public Response defaultConversionFromString_sortedSet(@MatrixParam("person") SortedSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // http://xxx/matrixParam/defaultConversionFromString_treeSet;person=George;person=Jack;person=John
        @GET
        @Path("defaultConversionFromString_treeSet")
        public Response defaultConversionFromString_treeSet(@MatrixParam("person") TreeSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }
    }

    @Path("cookieParam")
    public static class CookieParamResource {

        // cookie:person=George
        @GET
        @Path("defaultConversionConstructor_list")
        public Response defaultConversionConstructor_list(@CookieParam("person") List<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionConstructor_arrayList")
        public Response defaultConversionConstructor_arrayList(@CookieParam("person") ArrayList<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionConstructor_set")
        public Response defaultConversionConstructor_set(@CookieParam("person") Set<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionConstructor_hashSet")
        public Response defaultConversionConstructor_hashSet(@CookieParam("person") HashSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionConstructor_sortedSet")
        public Response defaultConversionConstructor_sortedSet(@CookieParam("person") SortedSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionConstructor_treeSet")
        public Response defaultConversionConstructor_treeSet(@CookieParam("person") TreeSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }





        // cookie:person=George
        @GET
        @Path("defaultConversionValueOf_list")
        public Response defaultConversionValueOf_list(@CookieParam("person") List<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionValueOf_arrayList")
        public Response defaultConversionValueOf_arrayList(@CookieParam("person") ArrayList<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionValueOf_set")
        public Response defaultConversionValueOf_set(@CookieParam("person") Set<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionValueOf_hashSet")
        public Response defaultConversionValueOf_hashSet(@CookieParam("person") HashSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionValueOf_sortedSet")
        public Response defaultConversionValueOf_sortedSet(@CookieParam("person") SortedSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionValueOf_treeSet")
        public Response defaultConversionValueOf_treeSet(@CookieParam("person") TreeSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }





        // cookie:person=George
        @GET
        @Path("defaultConversionFromString_list")
        public Response defaultConversionFromString_list(@CookieParam("person") List<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionFromString_arrayList")
        public Response defaultConversionFromString_arrayList(@CookieParam("person") ArrayList<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionFromString_set")
        public Response defaultConversionFromString_set(@CookieParam("person") Set<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionFromString_hashSet")
        public Response defaultConversionFromString_hashSet(@CookieParam("person") HashSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionFromString_sortedSet")
        public Response defaultConversionFromString_sortedSet(@CookieParam("person") SortedSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // cookie:person=George
        @GET
        @Path("defaultConversionFromString_treeSet")
        public Response defaultConversionFromString_treeSet(@CookieParam("person") TreeSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }
    }

    @Path("pathParam")
    public static class PathParamResource {

        // http://xxx/pathParam/defaultConversionConstructor_list/George
        @GET
        @Path("defaultConversionConstructor_list/{person}/{person}/{person}")
        public Response defaultConversionConstructor_list(@PathParam("person") List<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/pathParam/defaultConversionConstructor_arrayList/George
        @GET
        @Path("defaultConversionConstructor_arrayList/{person}/{person}/{person}")
        public Response defaultConversionConstructor_arrayList(@PathParam("person") ArrayList<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/pathParam/defaultConversionConstructor_set/George
        @GET
        @Path("defaultConversionConstructor_set/{person}/{person}/{person}")
        public Response defaultConversionConstructor_set(@PathParam("person") Set<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/pathParam/defaultConversionConstructor_hashSet/George
        @GET
        @Path("defaultConversionConstructor_hashSet/{person}/{person}/{person}")
        public Response defaultConversionConstructor_hashSet(@PathParam("person") HashSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/pathParam/defaultConversionConstructor_sortedSet/George
        @GET
        @Path("defaultConversionConstructor_sortedSet/{person}/{person}/{person}")
        public Response defaultConversionConstructor_sortedSet(@PathParam("person") SortedSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }

        // http://xxx/pathParam/defaultConversionConstructor_treeSet/George
        @GET
        @Path("defaultConversionConstructor_treeSet/{person}/{person}/{person}")
        public Response defaultConversionConstructor_treeSet(@PathParam("person") TreeSet<PersonWithConstructor> people) {
            return Response.ok(formatPeople(people)).build();
        }



        // http://xxx/pathParam/defaultConversionValueOf_list/George
        @GET
        @Path("defaultConversionValueOf_list/{person}/{person}/{person}")
        public Response defaultConversionValueOf_list(@PathParam("person") List<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/pathParam/defaultConversionValueOf_arrayList/George
        @GET
        @Path("defaultConversionValueOf_arrayList/{person}/{person}/{person}")
        public Response defaultConversionValueOf_arrayList(@PathParam("person") ArrayList<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/pathParam/defaultConversionValueOf_set/George
        @GET
        @Path("defaultConversionValueOf_set/{person}/{person}/{person}")
        public Response defaultConversionValueOf_set(@PathParam("person") Set<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/pathParam/defaultConversionValueOf_hashSet/George
        @GET
        @Path("defaultConversionValueOf_hashSet/{person}/{person}/{person}")
        public Response defaultConversionValueOf_hashSet(@PathParam("person") HashSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/pathParam/defaultConversionValueOf_sortedSet/George
        @GET
        @Path("defaultConversionValueOf_sortedSet/{person}/{person}/{person}")
        public Response defaultConversionValueOf_sortedSet(@PathParam("person") SortedSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }

        // http://xxx/pathParam/defaultConversionValueOf_treeSet/George
        @GET
        @Path("defaultConversionValueOf_treeSet/{person}/{person}/{person}")
        public Response defaultConversionValueOf_treeSet(@PathParam("person") TreeSet<PersonWithValueOf> people) {
            return Response.ok(formatPeopleWithValueOf(people)).build();
        }




        // http://xxx/pathParam/defaultConversionFromString_list/George
        @GET
        @Path("defaultConversionFromString_list/{person}/{person}/{person}")
        public Response defaultConversionFromString_list(@PathParam("person") List<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // http://xxx/pathParam/defaultConversionFromString_arrayList/George
        @GET
        @Path("defaultConversionFromString_arrayList/{person}/{person}/{person}")
        public Response defaultConversionFromString_arrayList(@PathParam("person") ArrayList<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // http://xxx/pathParam/defaultConversionFromString_set/George
        @GET
        @Path("defaultConversionFromString_set/{person}/{person}/{person}")
        public Response defaultConversionFromString_set(@PathParam("person") Set<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // http://xxx/pathParam/defaultConversionFromString_hashSet/George
        @GET
        @Path("defaultConversionFromString_hashSet/{person}/{person}/{person}")
        public Response defaultConversionFromString_hashSet(@PathParam("person") HashSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // http://xxx/pathParam/defaultConversionFromString_sortedSet/George
        @GET
        @Path("defaultConversionFromString_sortedSet/{person}/{person}/{person}")
        public Response defaultConversionFromString_sortedSet(@PathParam("person") SortedSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }

        // http://xxx/pathParam/defaultConversionFromString_treeSet/George
        @GET
        @Path("defaultConversionFromString_treeSet/{person}/{person}/{person}")
        public Response defaultConversionFromString_treeSet(@PathParam("person") TreeSet<PersonWithFromString> people) {
            return Response.ok(formatPeopleWithFromString(people)).build();
        }
    }

    private static String formatPeople(Collection<PersonWithConstructor> people) {
        StringBuilder stringBuilder = new StringBuilder();
        int personCount = people.size();
        int i = 0;
        for (PersonWithConstructor person : new TreeSet<>(people)) {
            stringBuilder.append(person.toString());
            ++i;
            if (i < personCount) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    private static String formatPeopleWithValueOf(Collection<PersonWithValueOf> people) {
        StringBuilder stringBuilder = new StringBuilder();
        int personCount = people.size();
        int i = 0;
        for (PersonWithValueOf person : new TreeSet<>(people)) {
            stringBuilder.append(person.toString());
            ++i;
            if (i < personCount) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    private static String formatPeopleWithFromString(Collection<PersonWithFromString> people) {
        StringBuilder stringBuilder = new StringBuilder();
        int personCount = people.size();
        int i = 0;
        for (PersonWithFromString person : new TreeSet<>(people)) {
            stringBuilder.append(person.toString());
            ++i;
            if (i < personCount) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }
}
