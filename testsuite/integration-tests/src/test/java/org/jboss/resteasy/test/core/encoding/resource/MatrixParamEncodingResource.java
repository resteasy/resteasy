package org.jboss.resteasy.test.core.encoding.resource;

import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.logging.Logger;

@Path("/")
public class MatrixParamEncodingResource {

    private static Logger logger = Logger.getLogger(MatrixParamEncodingResource.class);

    @GET
    @Path("decoded")
    @Produces("text/plain")
    public String matrixParamDecoded(@MatrixParam("param") String param) {
        logger.info("matrixParamDecoded() received: " + param);
        return param;
    }

    @GET
    @Path("decodedMultipleParam")
    @Produces("text/plain")
    public String matrixParamTwoParamDecoded(@MatrixParam("param") String param, @MatrixParam("param2") String param2) {
        logger.info("matrixParamDecoded() received param: " + param);
        logger.info("matrixParamDecoded() received param2: " + param2);
        return param + " " + param2;
    }

    @GET
    @Path("encoded")
    @Produces("text/plain")
    public String returnMatrixParamEncoded(@Encoded @MatrixParam("param") String param) {
        logger.info("matrixParamEncoded() received: " + param);
        return param;
    }
}
