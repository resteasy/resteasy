/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.resource.patch;

import java.util.Map;

import jakarta.json.JsonMergePatch;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Resource for testing JsonMergePatch as a direct method parameter.
 * Demonstrates the ability to use JsonMergePatch directly without
 * manual deserialization.
 */
@Path("/students")
public class JsonMergePatchParameterResource {
    private static Map<Long, Student> studentsMap = new java.util.concurrent.ConcurrentHashMap<Long, Student>();

    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Student getStudent(@PathParam("id") long id) {
        Student student = studentsMap.get(id);
        if (student == null) {
            throw new NotFoundException();
        }
        return student;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Student addStudent(Student student) {
        studentsMap.put(student.getId(), student);
        return student;
    }

    /**
     * PATCH endpoint using JsonMergePatch parameter directly.
     * Tests if JsonMergePatch can be used as a direct method parameter.
     *
     * Note: JsonpPatchMethodFilter intercepts this PATCH request and converts it to a GET
     * on the same path to retrieve the current entity. That's why we need a GET endpoint
     * at the same path. We reuse the existing GET /{id} endpoint by using the same path.
     *
     * @param id         the student ID
     * @param mergePatch the JsonMergePatch to apply
     * @return the patched student
     */
    @PATCH
    @Path("/{id}")
    @Consumes("application/merge-patch+json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchStudentWithJsonMergePatch(
            @PathParam("id") Long id,
            JsonMergePatch mergePatch) {
        Student student = studentsMap.get(id);
        if (student == null) {
            throw new NotFoundException();
        }

        try (Jsonb jsonb = JsonbBuilder.create()) {
            // Convert student to JsonValue
            JsonValue studentJson = jsonb.fromJson(
                    jsonb.toJson(student), JsonValue.class);

            // Apply the merge patch directly
            JsonValue patchedJson = mergePatch.apply(studentJson);

            // Convert back to Student
            Student patchedStudent = jsonb.fromJson(
                    patchedJson.toString(), Student.class);

            // Update the map
            studentsMap.put(id, patchedStudent);

            return Response.ok(patchedStudent).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid patch: " + e.getMessage())
                    .build();
        }
    }
}
