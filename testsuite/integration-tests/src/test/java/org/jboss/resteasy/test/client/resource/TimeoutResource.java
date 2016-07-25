package org.jboss.resteasy.test.client.resource;

import org.jboss.resteasy.test.client.TimeoutTest;

import javax.ws.rs.QueryParam;

public class TimeoutResource implements TimeoutTest.TimeoutResourceInterface {
    @Override
    public String get(@QueryParam("sleep") int sleep) throws Exception {
        Thread.sleep(sleep * 1000);
        return "OK";
    }
}
