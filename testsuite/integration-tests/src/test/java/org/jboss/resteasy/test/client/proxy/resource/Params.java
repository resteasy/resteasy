package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.PathParam;

import org.jboss.resteasy.annotations.jaxrs.QueryParam;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 3/4/19.
 */
public class Params {
    @PathParam("p1")
    private String p1;

    @QueryParam("q1")
    private String q1;

    private String p3;

    public String getP1() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    @PathParam("p3")
    public String getP3() {
        return p3;
    }

    @PathParam("p3")
    public void setP3(String p3) {
        this.p3 = p3;
    }

    public String getQ1() {
        return q1;
    }

    public void setQ1(String q1) {
        this.q1 = q1;
    }

}
