package org.jboss.resteasy.test.core.interceptors.resource;

import java.io.Serializable;

public class Pair implements Serializable {

    private static final long serialVersionUID = 1L;
    private String P1;
    private String P2;

    public String getP1() {
        return this.P1;
    }

    public String getP2() {
        return this.P2;
    }

    public void setP1(String p1) {
        this.P1 = p1;
    }

    public void setP2(String p2) {
        this.P2 = p2;
    }
}
