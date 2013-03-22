/**
 * This test verifies that org.jboss.as.jaxrs.deployment.JaxrsComponentDeployer is 
 * compatible with org.jboss.resteasy.util.GetRestful in resteasy-jaxrs.
 * 
 * It currently uses a homemade version of AS 7.2.0.Final, with an appropriate fix to JaxrsComponentDeployer,
 * generated from EAP 6.x commit 7602ae10bdd169e0006cbe80d101f8f820439d10.  This will be replaced, once
 * a version of AS 7.2.0.Final with the fix to JaxrsComponentDeployer is available in the nexus repo.
 */
package org.jboss.resteasy.resteasy802;
