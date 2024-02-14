package org.jboss.resteasy.test.core.basic.resource;

import java.util.List;
import java.util.Locale;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;

@Path("/lang")
public class AcceptLanguagesResource {

    private static Logger logger = Logger.getLogger(AcceptLanguagesResource.class);

    @GET
    @Produces("text/plain")
    public String get(@Context HttpHeaders headers) {
        // en-US;q=0,en;q=0.8,de-AT,de;q=0.9
        List<Locale> accepts = headers.getAcceptableLanguages();

        StringBuilder locales = new StringBuilder("Locales for accepting: ");
        for (Locale locale : accepts) {
            locales.append(locale);
            locales.append(", ");
        }
        logger.info(locales.toString());

        Assertions.assertEquals(accepts.get(0).toString(), "de_AT");
        Assertions.assertEquals(accepts.get(1).toString(), "de");
        Assertions.assertEquals(accepts.get(2).toString(), "en");
        Assertions.assertEquals(accepts.get(3).toString(), "en_US");

        return "hello";
    }
}
