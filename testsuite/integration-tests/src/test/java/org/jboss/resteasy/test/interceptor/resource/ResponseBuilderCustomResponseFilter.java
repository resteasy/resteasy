package org.jboss.resteasy.test.interceptor.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.ClientRequestContext;

/**
 * The objective of this filter is to show that the data from the original
 * response object can be accessed by a filter.  Interface  ClientResponseContext
 * provides limited access to the response object's data, thus a new Response
 * object must be created to retrieve the entity from the inputStream.  This code
 * also shows that a new InputStream must be provided to the response object via
 * ClientResponseContext's setEntityStream method so the client can have access
 * to the data.
 *
 * This filter is (fully) reading the responses' inputStream.  This causes
 * the inputStream to be closed.   This filter MUST provide a new inputStream
 * for the client to retrieve.   The data on the stream could be different
 * than the original data.  This filter is providing the original data
 * for client access.
 */
public class ResponseBuilderCustomResponseFilter implements ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext requestContext,
                       ClientResponseContext responseContext) throws IOException {

        Response response = Response.ok(responseContext.getEntityStream()).build();
        if (responseContext.getStatus() == 200 ) {
            ResponseChecker checker = new ResponseChecker();
            BufferedInputStream fis = checker.check(response);
            responseContext.setEntityStream(fis);
        }
    }

    private class ResponseChecker {

        public BufferedInputStream check(Response response) {
            String responseContent = response.readEntity(String.class);
            if (responseContent.contains("Error")) {
                throw new RuntimeException(responseContent);
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(responseContent.getBytes());
            return new BufferedInputStream((InputStream)bis);

        }
    }
}

