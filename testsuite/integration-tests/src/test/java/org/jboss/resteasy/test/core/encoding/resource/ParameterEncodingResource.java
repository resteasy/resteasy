package org.jboss.resteasy.test.core.encoding.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import java.util.Iterator;

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
