package org.jboss.resteasy.spi.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import jakarta.ws.rs.core.Context;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;

/**
 * Pick
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public final class PickConstructor {

    /**
     * Pick best constructor for a provider or resource class
     * <p>
     * Picks constructor with most parameters. Will ignore constructors that have parameters with no @Context annotation
     *
     * @param clazz class
     * @return constructor
     */
    public static Constructor pickSingletonConstructor(Class clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        Constructor<?> constructor = null;
        // prefer a no-arg constructor
        int numParameters = 0;
        Constructor pick = null;
        boolean potentialConflict = false; // https://issues.jboss.org/browse/RESTEASY-645
        for (Constructor con : constructors) {
            if (Modifier.isPublic(con.getModifiers()) == false) {
                continue;
            }

            if (con.getParameterCount() >= numParameters) {
                if (con.getParameterCount() > numParameters) {
                    potentialConflict = false;
                }
                boolean noContextAnnotation = false;
                if (con.getParameterAnnotations() != null) {
                    for (Annotation[] ann : con.getParameterAnnotations()) {
                        if (FindAnnotation.findAnnotation(ann, Context.class) == null) {
                            noContextAnnotation = true;
                        }
                    }
                }
                if (noContextAnnotation)
                    continue;
                if (con.getParameterCount() == numParameters && numParameters != 0) {
                    potentialConflict = true;
                }
                numParameters = con.getParameterCount();
                pick = con;

            }
        }
        if (potentialConflict) {
            LogMessages.LOGGER.ambiguousConstructorsFound(clazz);
        }
        return pick;
    }

    /**
     * Pick best constructor for a provider or resource class
     * <p>
     * Picks constructor with most parameters. Will ignore constructors that have parameters with no @Context annotation
     *
     * @param clazz class
     * @return constructor
     */
    public static Constructor pickPerRequestConstructor(Class clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
        Constructor<?> constructor = null;
        // prefer a no-arg constructor
        int numParameters = 0;
        Constructor pick = null;
        boolean potentialConflict = false; // https://issues.jboss.org/browse/RESTEASY-645
        for (Constructor con : constructors) {
            if (Modifier.isPublic(con.getModifiers()) == false) {
                continue;
            }
            if (con.getParameterCount() >= numParameters) {
                if (con.getParameterCount() > numParameters) {
                    potentialConflict = false;
                }

                boolean noContextAnnotation = false;
                if (con.getParameterAnnotations() != null) {
                    for (Annotation[] ann : con.getParameterAnnotations()) {
                        if (FindAnnotation.findJaxRSAnnotations(ann).length == 0) {
                            noContextAnnotation = true;
                        }
                    }
                }
                if (noContextAnnotation)
                    continue;
                if (con.getParameterCount() == numParameters && numParameters != 0) {
                    potentialConflict = true;
                }
                numParameters = con.getParameterCount();
                pick = con;
            }
        }

        if (potentialConflict) {
            LogMessages.LOGGER.ambiguousConstructorsFound(clazz);
        }
        return pick;
    }
}
