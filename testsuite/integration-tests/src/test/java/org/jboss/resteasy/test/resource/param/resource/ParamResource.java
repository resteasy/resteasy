package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.CookieParam;

public class ParamResource implements ParamInterfaceResource {
    public String getMatrix(String matrix) {
        if (matrix == null) {
            return "null";
        }
        return matrix;
    }

    public String getCookie(@CookieParam("param") String cookie) {
        if (cookie == null) {
            return "null";
        }
        return cookie;
    }

    public String getHeader(@CookieParam("custom") String header) {
        if (header == null) {
            return "null";
        }
        return header;
    }
}
