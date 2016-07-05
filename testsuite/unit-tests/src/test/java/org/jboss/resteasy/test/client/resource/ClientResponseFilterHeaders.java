package org.jboss.resteasy.test.client.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ClientResponseFilterHeaders implements ClientResponseFilter {

    private static Logger logger = Logger.getLogger(ClientResponseFilterHeaders.class);

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        for (Map.Entry<String, List<String>> header : responseContext.getHeaders().entrySet()) {
            StringBuilder sb = new StringBuilder(header.getKey() + ": ");
            for (String val : header.getValue()) {
                sb.append(val + " ");
            }
            logger.info(sb.toString());
        }
    }
}
