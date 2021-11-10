package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriInfo;
import java.util.List;

@Path("/cars/{make}")
public class PathParamCarResource {
   @GET
   @Path("/matrixparam/{model}/{year}")
   @Produces("text/plain")
   public String getFromMatrixParam(@PathParam("make") String make,
                                     @PathParam("model") PathSegment car,
                                     @MatrixParam("color") Color color,
                                     @PathParam("year") String year) {
      return "A " + color + " " + year + " " + make + " " + car.getPath();
   }

   @GET
   @Path("/pathsegment/{model}/{year}")
   @Produces("text/plain")
   public String getFromPathSegment(@PathParam("make") String make,
                                     @PathParam("model") PathSegment car,
                                     @PathParam("year") String year) {
      String carColor = car.getMatrixParameters().getFirst("color");
      return "A " + carColor + " " + year + " " + make + " " + car.getPath();
   }

   @GET
   @Path("/pathsegments/{model : .+}/year/{year}")
   @Produces("text/plain")
   public String getFromMultipleSegments(@PathParam("make") String make,
                                          @PathParam("model") List<PathSegment> car,
                                          @PathParam("year") String year) {
      String output = "A " + year + " " + make;
      for (PathSegment segment : car) {
         output += " " + segment.getPath();
      }
      return output;
   }

   @GET
   @Path("/uriinfo/{model}/{year}")
   @Produces("text/plain")
   public String getFromUriInfo(@Context UriInfo info) {
      String make = info.getPathParameters().getFirst("make");
      String year = info.getPathParameters().getFirst("year");
      PathSegment model = info.getPathSegments().get(3);
      String color = model.getMatrixParameters().getFirst("color");

      return "A " + color + " " + year + " " + make + " " + model.getPath();
   }

   public enum Color {
      red,
      white,
      blue,
      black
   }
}
