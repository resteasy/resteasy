package org.jboss.resteasy.test.microprofile.config.resource;

import java.util.Optional;

import javax.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/")
public class OptionalConfigPropertyInjectionResource {

   public static final String MISSING_OPTIONAL_PROPERTY_PATH = "/misingOptionalProperty";
   public static final String PRESENT_OPTIONAL_PROPERTY_PATH = "/presentOptionalProperty";

   private static final String MISSING_OPTIONAL_PROPERTY_NAME = "org.jboss.resteasy.test.missingOptionalProperty";
   private static final String PRESENT_OPTIONAL_PROPERTY_NAME = "org.jboss.resteasy.test.presentOptionalProperty";
   public static final String OPTIONAL_PROPERTY_VALUE = "I'm a optional property but I'm here";

   static {
      System.setProperty(PRESENT_OPTIONAL_PROPERTY_NAME, OPTIONAL_PROPERTY_VALUE);
    }

   @Inject
   @ConfigProperty(name = MISSING_OPTIONAL_PROPERTY_NAME)
   private Optional<String> missingOptionalProperty;
   @Inject
   @ConfigProperty(name = PRESENT_OPTIONAL_PROPERTY_NAME)
   private Optional<String> presentOptionalProperty;

   @GET
   @Produces("text/plain")
   @Path(MISSING_OPTIONAL_PROPERTY_PATH)
   public String getMisingOptionalProperty() {
      return missingOptionalProperty.orElse(null);
   }

   @GET
   @Produces("text/plain")
   @Path(PRESENT_OPTIONAL_PROPERTY_PATH)
   public String getPresentOptionalProperty() {
      return presentOptionalProperty.orElseThrow(InternalServerErrorException::new);
   }

}
