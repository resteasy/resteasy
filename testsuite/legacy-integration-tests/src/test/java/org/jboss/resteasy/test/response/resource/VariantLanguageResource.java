package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.EntityTag;
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
        List<Variant> list = Variant.encodings("CP1250", "UTF-8")
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
