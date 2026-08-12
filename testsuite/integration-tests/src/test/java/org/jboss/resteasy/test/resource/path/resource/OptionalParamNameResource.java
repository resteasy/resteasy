package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * Resource class for testing optional parameter name inference.
 * Demonstrates the new feature where parameter annotations can infer
 * parameter names from method parameter names when not explicitly specified.
 *
 * IMPORTANT: This class must be compiled with -parameters flag to preserve
 * parameter names in bytecode for method parameters.
 */
@Path("/")
public class OptionalParamNameResource {

    /**
     * Test @PathParam with inferred parameter names
     */
    @GET
    @Path("path/{name}/{surname}")
    public String pathParamInferred(
            @PathParam String name,
            @PathParam String surname) {
        return "Hello, " + name + " " + surname;
    }

    /**
     * Test @PathParam with explicit parameter names (backward compatibility)
     */
    @GET
    @Path("path-explicit/{firstName}/{lastName}")
    public String pathParamExplicit(
            @PathParam("firstName") String name,
            @PathParam("lastName") String surname) {
        return "Hello, " + name + " " + surname;
    }

    /**
     * Test @QueryParam with inferred parameter names
     */
    @GET
    @Path("query")
    public String queryParamInferred(
            @QueryParam String search,
            @QueryParam int page) {
        return "search=" + search + ", page=" + page;
    }

    /**
     * Test @HeaderParam with inferred parameter name
     */
    @GET
    @Path("header")
    public String headerParamInferred(
            @HeaderParam String authorization) {
        return "Auth: " + authorization;
    }

    /**
     * Test @MatrixParam with inferred parameter names
     */
    @GET
    @Path("matrix/{segment}")
    public String matrixParamInferred(
            @PathParam String segment,
            @MatrixParam String color,
            @MatrixParam String size) {
        return segment + ", color=" + color + ", size=" + size;
    }

    /**
     * Test @CookieParam with inferred parameter name
     */
    @GET
    @Path("cookie")
    public String cookieParamInferred(
            @CookieParam String sessionId) {
        return "Session: " + sessionId;
    }

    /**
     * Test @FormParam with inferred parameter names
     */
    @POST
    @Path("form")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String formParamInferred(
            @FormParam String username,
            @FormParam String password) {
        return "User: " + username;
    }

    /**
     * Test mixed usage - some explicit, some inferred
     */
    @GET
    @Path("mixed/{userId}/{itemId}")
    public String mixedUsage(
            @PathParam String userId, // Inferred
            @PathParam("itemId") String item, // Explicit
            @QueryParam String sort, // Inferred
            @QueryParam("order") String direction) { // Explicit
        return "userId=" + userId + ", itemId=" + item + ", sort=" + sort + ", order=" + direction;
    }

    /**
     * Field injection - always works without -parameters flag
     */
    @PathParam
    private String id;

    @QueryParam
    private String filter;

    @GET
    @Path("field/{id}")
    public String fieldInjection() {
        return "ID: " + id + ", Filter: " + filter;
    }
}

// Made with Bob
