package org.jboss.resteasy.test.core.encoding.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
