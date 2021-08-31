package org.jboss.resteasy.links.impl;

import java.lang.reflect.Method;

import javax.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.SecurityContext;

import org.jboss.resteasy.core.ResteasyContext;

final class EJBConstraintChecker {

    public boolean check(Method method) {
        // Use dynamic class loading here since if the EJB annotation class is not present
        // it cannot be on the method, so we don't have to check for it
        try {
            Class.forName("javax.annotation.security.RolesAllowed");
        } catch (ClassNotFoundException e) {
            // class not here, therefore not on method either
            return true;
        }
        return checkInternal(method);
    }

    public boolean check(Method method, ClassLoader classLoader) {
        // Use dynamic class loading here since if the EJB annotation class is not present
        // it cannot be on the method, so we don't have to check for it
        try {
            Class.forName("javax.annotation.security.RolesAllowed", true, classLoader);
        } catch (ClassNotFoundException e) {
            // class not here, therefore not on method either
            return true;
        }
        return checkInternal(method);
    }

    private boolean checkInternal(Method method) {
        // From now on we can use this class since it's there. I (Stef Epardaud) don't think we need to
        // remove the reference here and use reflection.
        RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
        if (rolesAllowed == null) {
            return true;
        }
        SecurityContext context = ResteasyContext.getContextData(SecurityContext.class);
        for (String role : rolesAllowed.value()) {
            if (context.isUserInRole(role)) {
                return true;
            }
        }
        return false;
    }
}
