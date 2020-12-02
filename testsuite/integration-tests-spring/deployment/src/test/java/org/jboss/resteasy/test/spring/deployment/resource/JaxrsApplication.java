package org.jboss.resteasy.test.spring.deployment.resource;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configures a JAX-RS endpoint. Delete this class, if you are not exposing
 * JAX-RS resources in your application.
 * User: rsearls
 * Date: 2/20/17
 */
@ApplicationPath("/resources")
public class JaxrsApplication extends Application {

}
