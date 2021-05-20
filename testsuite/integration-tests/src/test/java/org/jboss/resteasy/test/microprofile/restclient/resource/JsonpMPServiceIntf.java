package org.jboss.resteasy.test.microprofile.restclient.resource;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.inject.Singleton;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@RegisterRestClient(baseUri ="http://localhost:8080/jsonP_service")
@Path("/jsonpService")
@Singleton
public interface JsonpMPServiceIntf {
    @Path("array")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    JsonArray array(JsonArray array);

    @Path("object")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    JsonObject object(JsonObject obj);

    @Path("structure")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    JsonStructure object(JsonStructure struct);

    @Path("number")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    JsonNumber testNumber(JsonNumber number);

    @Path("string")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    JsonString testString(JsonString string);
}
