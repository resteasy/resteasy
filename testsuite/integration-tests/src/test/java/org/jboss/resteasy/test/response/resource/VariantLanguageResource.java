package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Variant;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.EntityTag;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Path("/")
public  class VariantLanguageResource {


   @GET
   @Produces("text/plain")
   public Response doGet(@Context Request req) {
      List<Variant> vs = Variant.VariantListBuilder.newInstance().languages(new Locale("zh")).languages(
            new Locale("fr")).languages(new Locale("en")).add().build();

      Variant v = req.selectVariant(vs);
      if (v == null) {
         return Response.notAcceptable(vs).build();
      } else {
         return Response.ok(v.getLanguage(), v).build();
      }
   }

   @Path("/brazil")
   @GET
   @Produces("text/plain")
   public Response doGetBrazil(@Context Request req) {
      List<Variant> vs = Variant.VariantListBuilder.newInstance().languages(new Locale("pt", "BR")).add().build();

      Variant v = req.selectVariant(vs);
      if (v == null) {
         return Response.notAcceptable(vs).build();
      } else {
         return Response.ok(v.getLanguage(), v).build();
      }
   }

   @GET
   @Path("/SelectVariantTestResponse")
   public Response selectVariantTestResponse(@Context Request req) {
      List<Variant> list = Variant.encodings("CP1250", StandardCharsets.UTF_8.name())
            .languages(Locale.ENGLISH)
            .mediaTypes(MediaType.APPLICATION_JSON_TYPE).add().build();
      Variant selectedVariant = req.selectVariant(list);
      if (null == selectedVariant) {
         return Response.notAcceptable(list).build();
      }
      return Response.ok("entity").build();
   }

   @GET
   @Path("/SelectVariantTestGet")
   public Response selectVariantTestGet(@Context Request req) {
      List<Variant> vs = null;

      try {
         req.selectVariant(vs);
         return Response.ok("Test FAILED - no exception thrown").build();
      } catch (IllegalArgumentException ile) {
         return Response.ok("PASSED")
               .build();
      } catch (Throwable th) {
         //logger.error("This not the expected exception", th);
         return Response.ok(
               "Test FAILED - wrong type exception thrown" +
                     th.getMessage()).build();
      }
   }

   @PUT
   @Path("/SelectVariantTestPut")
   public Response selectVariantTestPut(@Context Request req) {
      return selectVariantTestGet(req);
   }

   @POST
   @Path("/SelectVariantTestPost")
   public Response selectVariantTestPost(@Context Request req) {
      return selectVariantTestGet(req);
   }

   @DELETE
   @Path("/SelectVariantTestDelete")
   public Response selectVariantTestDelete(@Context Request req) {
      return selectVariantTestGet(req);
   }

   @GET
   @Path("/preconditionsSimpleGet")
   public Response evaluatePreconditionsEntityTagGetSimpleTest(
         @Context Request req) {
      boolean ok = evaluatePreconditionsEntityTag(req, "AAA");
      if (!ok) {
         return Response.status(Response.Status.GONE).build();
      }
      ok &= evaluatePreconditionsNowEntityTagNull(req);
      if (!ok) {
         return Response.status(Response.Status.NOT_ACCEPTABLE).build();
      }
      ok &= evaluatePreconditionsEntityTagNull(req);
      return createResponse(ok);
   }

   private boolean evaluatePreconditionsEntityTagNull(Request req) {
      try {
         req.evaluatePreconditions((EntityTag) null);
         return false;
      } catch (IllegalArgumentException iae) {
         return true;
      }
   }

   private boolean evaluatePreconditionsNowEntityTagNull(Request req) {
      try {
         Date now = Calendar.getInstance().getTime();
         req.evaluatePreconditions(now, (EntityTag) null);
         return false;
      } catch (IllegalArgumentException iae) {
         return true;
      }
   }

   private EntityTag createTag(String tag) {
      String xtag = new StringBuilder().append("\"").append(tag).append("\"")
            .toString();
      return EntityTag.valueOf(xtag);
   }

   private boolean evaluatePreconditionsEntityTag(Request req, String tag) {
      Response.ResponseBuilder rb = req.evaluatePreconditions(createTag(tag));
      return rb == null;
   }

   private Response createResponse(boolean ok) {
      Response.Status status = ok ? Response.Status.OK : Response.Status.PRECONDITION_FAILED;
      return Response.status(status).build();
   }
}
