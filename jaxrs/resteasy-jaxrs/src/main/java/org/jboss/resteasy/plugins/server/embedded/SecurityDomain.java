package org.jboss.resteasy.plugins.server.embedded;

import java.security.Principal;

/**
 * Simple plugin to provide authentication/authorization to embedded implementations
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface SecurityDomain
{
   Principal authenticate(String username, String password) throws SecurityException;

   boolean isUserInRole(Principal username, String role);
}
