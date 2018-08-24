package org.jboss.resteasy.utils;

import org.jboss.resteasy.utils.maven.MavenUtil;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Util class for RESTEasy rxjava (1) related testing.
 */
public class TestUtilRxJava {

    private static String defaultReactiveContextsVersion = "0.0.4";
    private static String defaultRxJavaVersion = "1.3.2";
    private static String defaultRxJavaReactiveStreamsVersion = "1.2.1";


    private static String readSystemProperty(String name, String defaultValue) {
        String value = System.getProperty(name);
        return (value == null) ? defaultValue : value;
    }

    private static String getReactiveContextsVersion() {
        return readSystemProperty("version.reactive-contexts", defaultReactiveContextsVersion);
    }

    private static String getRxJavaVersion() {
        return readSystemProperty("version.rxjava", defaultRxJavaVersion);
    }

    private static String getRxJavaReactiveStreamsVersion() {
        return readSystemProperty("version.rxjava-reactive-streams", defaultRxJavaReactiveStreamsVersion);
    }

    private static File[] resolveRxJavaDependencies() {
        MavenUtil mavenUtil;
        mavenUtil = MavenUtil.create(true);
        List<File> runtimeDependencies = new ArrayList<>();
        
        try {
            runtimeDependencies.add(mavenUtil.createMavenGavFile("org.jboss.resteasy:resteasy-reactive-context:" + System.getProperty("version.resteasy.testsuite")));
            runtimeDependencies.add(mavenUtil.createMavenGavFile("org.jboss.resteasy:resteasy-rxjava:" + System.getProperty("project.version")));
            runtimeDependencies.add(mavenUtil.createMavenGavFile("io.reactiverse:reactive-contexts-core:" + getReactiveContextsVersion()));
            runtimeDependencies.add(mavenUtil.createMavenGavFile("io.reactiverse:reactive-contexts-propagators-rxjava1:" + getReactiveContextsVersion()));
            runtimeDependencies.add(mavenUtil.createMavenGavFile("io.reactivex:rxjava:" + getRxJavaVersion()));
            runtimeDependencies.add(mavenUtil.createMavenGavFile("io.reactivex:rxjava-reactive-streams:" + getRxJavaReactiveStreamsVersion()));
        } catch (Exception e) {
            throw new RuntimeException("Unable to get artifacts from maven via Aether library", e);
        }

        File[] dependencies = runtimeDependencies.toArray(new File[]{});
        return dependencies;
    }

    public static void addRxJavaLibraries(WebArchive archive) {
        archive.addAsLibraries(resolveRxJavaDependencies());
    }
    
    public static void setManifestWithReactiveStreamsDependency(WebArchive archive) {
    	archive.setManifest(new StringAsset("Manifest-Version: 1.0\n" + "Dependencies: org.reactivestreams"));
    }
    
    public static void setupRxJava(WebArchive archive) {
    	addRxJavaLibraries(archive);
    	setManifestWithReactiveStreamsDependency(archive);
    }

}
