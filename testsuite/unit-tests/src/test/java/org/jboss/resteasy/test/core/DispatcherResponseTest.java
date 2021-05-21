package org.jboss.resteasy.test.core;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonMappingException;

public class DispatcherResponseTest {
    
    private Dispatcher dispatcher;
    
    @Before
    public void before() {
        System.setProperty(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB, "true");
        
        ResteasyProviderFactory.getInstance()
                .getExceptionMappers()
                .put(JsonMappingException.class, new JsonMappingExceptionMapper());
        
        dispatcher = new SynchronousDispatcher(ResteasyProviderFactory.getInstance());
    }
    
    @Test
    public void testResponseAfterSerializationFailureDoesNotContainsGarbage()
            throws UnsupportedEncodingException, URISyntaxException {
        dispatcher.getRegistry().addSingletonResource(new MockResource());
        
        MockHttpRequest request = MockHttpRequest.get("/mockresource");
        MockUncommitedResponse response = new MockUncommitedResponse();
        
        dispatcher.invoke(request, response);
        
        assertEquals(500, response.getStatus());
        // Below does not fail if RESTEASY-2901 is fixed
        assertEquals("{\"ARQ-502\":\"JSON Serialization Error\"}", response.getContentAsString());
    }
    
    public static class MockUncommitedResponse extends MockHttpResponse {
        /**
         * In the scenario seen when reporting RESTEASY-2901 the response was not commited when checked by
         * {@link SynchronousDispatcher#writeException(org.jboss.resteasy.spi.HttpRequest, org.jboss.resteasy.spi.HttpResponse, Throwable, java.util.function.Consumer)}
         * 
         * TODO: Maybe this check returning false in that scenario is the root cause of the issue?
         */
        @Override
        public boolean isCommitted() {
            return false;
        }
    }
    
    @Path("/mockresource")
    public static class MockResource {
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response get() {
            return Response.ok().entity(new UnsafeDto()).build();
        }
    }
    
    public static class UnsafeDto {
        private String typeCode;
        
        public String getTypeDescription() {
            // Causes NPE during jackson's serialization
            if (this.typeCode.equals("not empty")) {
                return "NOT EMPTY";
            } else {
                return this.typeCode;
            }
        }
    }
    
    public static class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
        @Override
        public Response toResponse(JsonMappingException exception) {
            Map<String, String> errorDetails = new HashMap<>();
            errorDetails.put("ARQ-502", "JSON Serialization Error");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorDetails)
                    .type(MediaType.APPLICATION_JSON).build();
        }
    }
    
}
