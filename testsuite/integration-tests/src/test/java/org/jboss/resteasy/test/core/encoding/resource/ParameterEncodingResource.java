package org.jboss.resteasy.test.core.encoding.resource;

import java.util.Iterator;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;

import org.jboss.logging.Logger;

@Path("/")
public class ParameterEncodingResource {
    private static Logger logger = Logger.getLogger(ParameterEncodingResource.class);

    @GET
    @Produces("text/plain")
    @Path("encoded/pathparam/{pathParam}")
    public String getEncodedPathParam(@Encoded @PathParam("pathParam") String pathParam) {
        logger.info("getEncodedPathParam(): encoded: " + pathParam);
        return pathParam;
    }

    @GET
    @Produces("text/plain")
    @Path("decoded/pathparam/{pathParam}")
    public String getDecodedPathParam(@PathParam("pathParam") String pathParam) {
        logger.info("getDecodedPathParam(): decoded: " + pathParam);
        return pathParam;
    }

    @GET
    @Produces("text/plain")
    @Path("encoded/matrix")
    public String getEncodedMatrixParam(@Encoded @MatrixParam("m") String matrixParam) {
        logger.info("getEncodedMatrixParam(): encoded: " + matrixParam);
        return matrixParam;
    }

    @GET
    @Produces("text/plain")
    @Path("decoded/matrix")
    public String getDecodedMatrixParam(@MatrixParam("m") String matrixParam) {
        logger.info("getDecodedMatrixParam(): decoded: " + matrixParam);
        return matrixParam;
    }

    @GET
    @Produces("text/plain")
    @Path("encoded/query")
    public String getEncodedQueryParam(@Encoded @QueryParam("m") String queryParam) {
        logger.info("getEncodedQueryParam(): encoded: " + queryParam);
        return queryParam;
    }

    @GET
    @Produces("text/plain")
    @Path("decoded/query")
    public String getDecodedQueryParam(@QueryParam("m") String queryParam) {
        logger.info("getDecodedQueryParam(): decoded: " + queryParam);
        return queryParam;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("text/plain")
    @Path("encoded/form")
    public String getEncodedFormParam(@Encoded @FormParam("f") String formParam) {
        logger.info("getEncodedFormParamPost(): encoded: " + formParam);
        return formParam;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("text/plain")
    @Path("decoded/form")
    public String getDecodedFormParam(@FormParam("f") String formParam) {
        logger.info("getDecodedFormParamPost(): decoded: " + formParam);
        return formParam;
    }

    @GET
    @Produces("text/plain")
    @Path("encoded/segment/{pathParam}")
    public String getEncodedSegmentPathParam(@Encoded @PathParam("pathParam") PathSegment segment) {
        logger.info("getEncodedSegmentPathParam(): encoded segment: " + segment.getPath());
        return segment.getPath();
    }

    @GET
    @Produces("text/plain")
    @Path("decoded/segment/{pathParam}")
    public String getDecodedSegmentPathParam(@PathParam("pathParam") PathSegment segment) {
        logger.info("getDecodedSegmentPathParam(): decoded segment: " + segment.getPath());
        return segment.getPath();
    }

    @GET
    @Produces("text/plain")
    @Path("encoded/segment/matrix/{params}")
    public String getEncodedSegmentMatrixParam(@Encoded @PathParam("params") PathSegment segment) {
        MultivaluedMap<String, String> map = segment.getMatrixParameters();
        Iterator<String> it = map.keySet().iterator();
        logger.info("getEncodedSegmentMatrixParam(): encoded matrix params: ");
        StringBuilder builder = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            logger.info("  " + key + "->" + map.getFirst(key));
            builder.append(map.getFirst(key));
        }
        return builder.toString();
    }

    @GET
    @Produces("text/plain")
    @Path("decoded/segment/matrix/{params}")
    public String getDecodedSegmentMatrixParam(@PathParam("params") PathSegment segment) {
        MultivaluedMap<String, String> map = segment.getMatrixParameters();
        Iterator<String> it = map.keySet().iterator();
        logger.info("getDecodedSegmentMatrixParam(): decoded matrix params: ");
        StringBuilder builder = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            logger.info("  " + key + "->" + map.getFirst(key));
            builder.append(map.getFirst(key));
        }
        return builder.toString();
    }
}
