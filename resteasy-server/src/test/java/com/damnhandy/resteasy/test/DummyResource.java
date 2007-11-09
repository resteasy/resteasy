package com.damnhandy.resteasy.test;

import com.damnhandy.resteasy.annotations.WebResource;
import com.damnhandy.resteasy.annotations.HttpMethod;
import com.damnhandy.resteasy.annotations.RepresentationIn;
import com.damnhandy.resteasy.annotations.RepresentationOut;
import com.damnhandy.resteasy.annotations.QueryParam;

/**
 * Sample Resteasy service
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@WebResource("/dummy")
@RepresentationIn("text/plain")
@RepresentationOut("text/plain")
public class DummyResource {

    @HttpMethod("GET") public String get(@QueryParam("echo") String echo)
    {
        System.out.println("Received echo: " + echo);
        return echo;   
    }
}
