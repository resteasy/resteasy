package org.jboss.resteasy.test.resource.constructor.resource;

import jakarta.ws.rs.QueryParam;

public class HappinessParams {
    @QueryParam("duration")
    int duration;
    @QueryParam("timeUnit")
    String timeUnit;

    @QueryParam("type")
    String type;

    private HappinessParams() {}
    public HappinessParams(final int duration, final String timeUnit) {}
    public HappinessParams(final String type) {}
}
