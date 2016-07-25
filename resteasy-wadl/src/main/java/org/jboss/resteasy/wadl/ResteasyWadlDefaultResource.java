package org.jboss.resteasy.wadl;

import org.jboss.resteasy.wadl.i18n.LogMessages;
import org.jboss.resteasy.wadl.i18n.Messages;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Path("/application.xml")
public class ResteasyWadlDefaultResource {

    private final static ResteasyWadlWriter apiWriter = new ResteasyWadlServletWriter();
    private final static Map<String, ResteasyWadlServiceRegistry> services = new HashMap<>();

    public static Map<String, ResteasyWadlServiceRegistry> getServices() {
        return services;
    }

    @GET
    @Produces("application/xml")
    public String output() {
        try {
            return this.apiWriter.getStringWriter("", services).toString();
        } catch (JAXBException e) {
           LogMessages.LOGGER.error(Messages.MESSAGES.cantProcessWadl(), e);
        }
        return null;
    }
}
