package org.jboss.resteasy.wadl;

import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.wadl.i18n.LogMessages;
import org.jboss.resteasy.wadl.i18n.Messages;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public abstract class ResteasyWadlDefaultResource {

   private Map<String, ResteasyWadlServiceRegistry> services = new HashMap<>();

   private void loadServices(ResteasyDeployment deployment) {
         services.put("/", ResteasyWadlGenerator.generateServiceRegistry(deployment));
   }

   public Map<String, ResteasyWadlServiceRegistry> getServices() {
      return services;
   }

   ResteasyWadlWriter wadlWriter = new ResteasyWadlWriter(); // create a default servlet writer.

   public ResteasyWadlWriter getWadlWriter() {
      return wadlWriter;
   }

   @GET
   @Path("/application.xml")
   @Produces("application/xml")
   public String output(@Context ResteasyDeployment deployment) {
      loadServices(deployment);

      try {
         return wadlWriter.getStringWriter("", services).toString();
      } catch (JAXBException e) {
         LogMessages.LOGGER.error(Messages.MESSAGES.cantProcessWadl(), e);
      }
      return null;
   }


   @GET
   @Path("/wadl-extended/{path}")
   @Produces("application/xml")
   public Response grammars(@PathParam("path") String path, @Context ResteasyDeployment deployment) {
      loadServices(deployment);
      wadlWriter.createApplication("", services);

      return Response
            .ok()
            .type(MediaType.APPLICATION_XML_TYPE)
            .entity(wadlWriter.getWadlGrammar().getSchemaOfUrl(path))
            .build();
   }
}
