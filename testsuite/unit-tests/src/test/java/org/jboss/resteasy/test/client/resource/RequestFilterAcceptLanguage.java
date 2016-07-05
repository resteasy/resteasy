package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RequestFilterAcceptLanguage implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        List<Locale> locales = requestContext.getAcceptableLanguages();
        StringBuilder builder = new StringBuilder();
        for (Locale locale : locales) {
            builder.append(locale.toString()).append(",");
        }
        Response r = Response.ok(builder.toString()).build();
        requestContext.abortWith(r);
    }
}
