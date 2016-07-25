package org.jboss.resteasy.test.interception.resource;

import javax.annotation.Priority;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

@Priority(300)
public class PriorityClientRequestFilter3 implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {

    }
}
