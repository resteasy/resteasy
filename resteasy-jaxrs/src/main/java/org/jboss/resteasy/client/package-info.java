/**
 * JAX-RS specification 1.1 (<a href="https://jcp.org/en/jsr/detail?id=311">https://jcp.org/en/jsr/detail?id=311</a>)
 * had no client framework, so Resteasy release 2.x provided one. It has two ways to access a server resource:
 * 
 * <ol>
 * <li>
 * {@link org.jboss.resteasy.client.ClientRequest}
 * </li>
 * <li>
 * a proxy that implements an interface implemented by the target resource
 * </ol>
 * 
 * JAX-RS specification 2.0 
 * (<a href="https://www.jcp.org/aboutJava/communityprocess/final/jsr339/index.html">https://www.jcp.org/aboutJava/communityprocess/final/jsr339/index.html</a>)
 * introduced an official client framework, so the older Resteasy client framework is now deprecated. However, the new JAX-RS
 * framework does not include a proxy feature, so Resteasy extends the official client framework with a new proxy feature.
 * A proxy can be created using {@link org.jboss.resteasy.client.jaxrs.ResteasyWebTarget}, which extends the api class
 * javax.ws.rs.client.WebTarget
 * <p>
 * For more information, see the Resteasy User Guide
 * <a href="http://docs.jboss.org/resteasy/docs/">http://docs.jboss.org/resteasy/docs/</a>.
 */
package org.jboss.resteasy.client;
