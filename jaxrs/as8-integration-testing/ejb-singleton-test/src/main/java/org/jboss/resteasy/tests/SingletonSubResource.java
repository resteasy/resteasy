package org.jboss.resteasy.tests;

import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Singleton
public class SingletonSubResource
{
    @GET
    @Produces("text/plain")
    public String hello() {
        return "hello";
    }
}
