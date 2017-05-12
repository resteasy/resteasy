package org.jboss.resteasy.utils;

import org.jboss.resteasy.utils.maven.MavenUtil;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Base util class for RESTEasy spring related testing.
 */
public class TestUtilSpring {

    private static String defaultSpringVersion = "4.2.4.RELEASE";
    //protected static Logger logger;


    /**
     * Read system proprty
     *
     * @param name         of the property
     * @param defaultValue which will be used if system property name is not defined
     * @return property value
     */
    private static String readSystemProperty(String name, String defaultValue) {
        String value = System.getProperty(name);
        return (value == null) ? defaultValue : value;
    }

    /**
     * Get spring version
     *
     * @return Spring version.
     */
    private static String getSpringVersion() {
        return readSystemProperty("version.org.springframework", defaultSpringVersion);
    }

    /**
     * Get Spring dependencies for specified spring version
     *
     * @param springVersion
     * @return Spring libraries
     */
    private static File[] resolveSpringDependencies(String springVersion) {
        MavenUtil mavenUtil;
        mavenUtil = MavenUtil.create(true);
        List<File> runtimeDependencies = new ArrayList<>();

        try {
            runtimeDependencies.add(mavenUtil.createMavenGavFile("org.springframework:spring-core:" + springVersion));
            runtimeDependencies.add(mavenUtil.createMavenGavFile("org.springframework:spring-web:" + springVersion));
            runtimeDependencies.add(mavenUtil.createMavenGavFile("org.springframework:spring-webmvc:" + springVersion));
            runtimeDependencies.add(mavenUtil.createMavenGavFile("org.springframework:spring-context:" + springVersion));
            runtimeDependencies.add(mavenUtil.createMavenGavFile("org.springframework:spring-expression:" + springVersion));
            runtimeDependencies.add(mavenUtil.createMavenGavFile("org.springframework:spring-beans:" + springVersion));
            runtimeDependencies.add(mavenUtil.createMavenGavFile("org.springframework:spring-aop:" + springVersion));
        } catch (Exception e) {
            throw new RuntimeException("Unable to get artifacts from maven via Aether library", e);
        }

        File[] dependencies = runtimeDependencies.toArray(new File[]{});
        return dependencies;
    }

    /**
     * Adds Spring libraries and its dependencies into webarchove
     *
     * @param archive
     */
    public static void addSpringLibraries(WebArchive archive) {
        archive.addAsLibraries(resolveSpringDependencies(getSpringVersion()));
    }

}
