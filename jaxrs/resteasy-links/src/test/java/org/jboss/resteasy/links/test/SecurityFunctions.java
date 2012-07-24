package org.jboss.resteasy.links.test;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.SecurityContext;

public class SecurityFunctions {
	public static boolean hasPermission(Object target, String permission){
		SecurityContext context = ResteasyProviderFactory.getContextData(SecurityContext.class);
		if(context.isUserInRole("admin"))
			return true;
		if(context.isUserInRole("power-user")){
			return allow(permission, "add", "update", "read");
		}
		return allow(permission, "read");
	}

	private static boolean allow(String permission, String... allowedPermissions) {
		for (String allowedPermission : allowedPermissions) {
			if(permission.equals(allowedPermission))
				return true;
		}
		return false;
	}

	public static boolean hasRole(String role){
		SecurityContext context = ResteasyProviderFactory.getContextData(SecurityContext.class);
		return context.isUserInRole(role);
	}
}
