package org.jboss.resteasy.utils;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.Provider;

/**
 * Base class used from deployment.
 */
@Provider
@ApplicationPath("/")
public class TestApplication extends Application {

    public static Set<Class<?>> classes = new HashSet<Class<?>>();
    public static Set<Object> singletons = new HashSet<Object>();

    /**
     * Load resources from classes.txt file from deployment
     *
     * @return Array of class names.
     */
    public static String[] getClassesFromDeployment(String name) {
        String resource = name + ".txt";
        String stripped = resource.startsWith("/") ? resource.substring(1) : resource;

        InputStream stream = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            stream = classLoader.getResourceAsStream(stripped);
        }
        if (stream == null) {
            stream = TestApplication.class.getResourceAsStream(resource);
        }
        if (stream == null) {
            stream = TestApplication.class.getClassLoader().getResourceAsStream(stripped);
        }
        if (stream == null) {
            return new String[0];
        }
        return convertStreamToString(stream).split(",");
    }

    /**
     * Convert input stream to String
     *
     * @param is Input stream
     * @return string
     */
    private static String convertStreamToString(final java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * @see jakarta.ws.rs.core.Application#getClasses()
     */
    @Override
    public Set<Class<?>> getClasses() {
        if (classes.isEmpty()) {
            for (String clazz : getClassesFromDeployment("classes")) {
                if (!clazz.isEmpty()) {
                    try {
                        classes.add(Class.forName(clazz));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Class " + clazz + " not found.", e);
                    }
                }
            }
        }
        return classes;
    }

    /**
     * @see jakarta.ws.rs.core.Application#getSingletons()
     */
    @Override
    public Set<Object> getSingletons() {
        if (singletons.isEmpty()) {
            for (String clazz : getClassesFromDeployment("singletons")) {
                if (!clazz.isEmpty()) {
                    try {
                        singletons.add(Class.forName(clazz).newInstance());
                    } catch (Exception e) {
                        throw new RuntimeException("Class " + clazz + " not found.", e);
                    }
                }
            }
        }
        return singletons;
    }
}
