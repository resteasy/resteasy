package org.jboss.resteasy.test.core.smoke.resource;


import org.jboss.logging.Logger;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class ResourceWithInterfaceResourceWithInterface implements ResourceWithInterfaceSimpleClient {
    private static Logger logger = Logger.getLogger(ResourceWithInterfaceResourceWithInterface.class);

    public String getWild() {
        return "Wild";
    }

    public String getBasic() {
        logger.info("getBasic()");
        return "basic";
    }

    public void putBasic(String body) {
        logger.info(body);
    }

    public String getQueryParam(@QueryParam("param") String param) {
        return param;
    }

    public String getMatrixParam(@MatrixParam("param") String param) {
        return param;
    }

    public int getUriParam(@PathParam("param") int param) {
        return param;
    }


}
