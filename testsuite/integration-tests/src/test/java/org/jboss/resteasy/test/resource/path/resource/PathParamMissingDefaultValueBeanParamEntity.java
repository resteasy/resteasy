package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class PathParamMissingDefaultValueBeanParamEntity {

    @CookieParam("bpeCookie")
    public String bpeCookie;

    @FormParam("bpeForm")
    public String bpeForm;

    @HeaderParam("bpeHeader")
    public String bpeHeader;

    @MatrixParam("bpeMatrix")
    public String bpeMatrix;

    @PathParam("bpePath")
    public String bpePath;

    @QueryParam("bpeQuery")
    public String bpeQuery;

    public String toString() {
       return bpeCookie + bpeForm + bpeHeader + bpeMatrix + bpePath + bpeQuery;
    }
}
