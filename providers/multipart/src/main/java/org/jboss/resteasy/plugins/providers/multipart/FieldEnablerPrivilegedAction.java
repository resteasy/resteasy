package org.jboss.resteasy.plugins.providers.multipart;

import java.lang.reflect.Field;
import java.security.PrivilegedAction;

/**
 * Helper class to make a field accessible.
 * 
 * Directly calling field.setAccessible(true); is not advised as it could be
 * invoked in a context without security permissions. For more information
 * please check java.security.AccessController API in JavaSE.
 * 
 * Usage example:
 * 
 * AccessController.doPrivileged(new FieldEnablerPrivilegedAction(field));
 * 
 * @author Attila Kiraly
 * 
 */
public class FieldEnablerPrivilegedAction implements PrivilegedAction<Object> {
	private final Field field;

	public FieldEnablerPrivilegedAction(Field field) {
		super();
		this.field = field;
	}

	public Object run() {
		field.setAccessible(true);
		return null;
	}
}
