/**
 * JAX-RS specification 1.1 (<a href="https://jcp.org/en/jsr/detail?id=311">https://jcp.org/en/jsr/detail?id=311</a>)
 * had no interceptor framework, so Resteasy release 2.x provided one.
 * <p>
 * However, JAX-RS specification 2.0 
 * (<a href="https://www.jcp.org/aboutJava/communityprocess/final/jsr339/index.html">https://www.jcp.org/aboutJava/communityprocess/final/jsr339/index.html</a>)
 * introduced an official interceptor/filter framework, so the older Resteasy interceptor framework is now deprecated. 
 * 
 * The relevant interfaces are defined in the javax.ws.rs.ext package of the jaxrs-api module.
 */
package org.jboss.resteasy.spi.interception;
