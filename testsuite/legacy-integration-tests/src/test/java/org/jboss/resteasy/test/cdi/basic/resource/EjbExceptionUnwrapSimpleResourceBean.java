package org.jboss.resteasy.test.cdi.basic.resource;

import org.jboss.logging.Logger;

import javax.ejb.Stateless;

@Stateless
public class EjbExceptionUnwrapSimpleResourceBean implements EjbExceptionUnwrapSimpleResource {
    private static Logger logger = Logger.getLogger(EjbExceptionUnwrapSimpleResourceBean.class);

    public String getBasic() {
        logger.info("getBasic()");
        return "basic";
    }

    public void putBasic(String body) {
        logger.info(body);
    }

    public String getQueryParam(String param) {
        return param;
    }

    public String getMatrixParam(String param) {
        return param;
    }

    public int getUriParam(int param) {
        return param;
    }

}
