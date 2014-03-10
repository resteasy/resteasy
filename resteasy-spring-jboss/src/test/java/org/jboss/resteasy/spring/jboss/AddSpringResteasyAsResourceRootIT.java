package org.jboss.resteasy.spring.jboss;

import static org.jboss.resteasy.spring.jboss.TestResource.*;
import static org.jboss.resteasy.util.HttpResponseCodes.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.arquillian.container.spi.client.deployment.DeploymentDescription;
import org.jboss.arquillian.container.spi.client.deployment.DeploymentScenario;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.plugins.spring.SpringContextLoaderListener;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;

@RunWith(Arquillian.class)
@RunAsClient
public class AddSpringResteasyAsResourceRootIT {

    @ArquillianResource
    private Deployer deployer;

    @ArquillianResource
    private DeploymentScenario deploymentScenario;

    private HttpClient client;

    private String springVersion;
    private String deploymentName;

    @Before
    public void before() throws Exception {
        springVersion = getSpringVersion();
        client = new HttpClient();
    }

    @After
    public void after() {
        deployer.undeploy(deploymentName);
    }


    @Test
    public void testDeploymentWithSpringMvcDispatcherAddsResourceRoot() throws Exception {
        deploymentName = "deploymentWithSpringMvcDispatcher" + (springDependenciesInDeployment() ? "SpringIncluded" : "SpringInModule");
        DeploymentDescription deploymentDescription = new DeploymentDescription(deploymentName, createDeploymentWithSpringMvcDispatcher(deploymentName));
        deploymentDescription.shouldBeManaged(false);

        deploymentScenario.addDeployment(deploymentDescription);
        deployer.deploy(deploymentDescription.getName());

        assertResponse(deploymentName);

        assertTrue(springClassesAreAvailableToDeployment(deploymentName));
        assertTrue(resteasySpringClassesAreAvailableToDeployment(deploymentName));
    }

    @Test
    public void testDeploymentWithSpringContextLoaderListenerAddsResourceRoot() throws Exception {
        deploymentName = "deploymentWithSpringContextLoaderListener" + (springDependenciesInDeployment() ? "SpringIncluded" : "SpringInModule");
        DeploymentDescription deploymentDescription = new DeploymentDescription(deploymentName, createDeploymentWithSpringContextLoaderListener(deploymentName));
        deploymentDescription.shouldBeManaged(false);

        deploymentScenario.addDeployment(deploymentDescription);
        deployer.deploy(deploymentDescription.getName());

        assertResponse(deploymentName);

        assertTrue(springClassesAreAvailableToDeployment(deploymentName));
        assertTrue(resteasySpringClassesAreAvailableToDeployment(deploymentName));
    }

    @Test
    public void testDeploymentWithoutSpringMvcDispatcherOrListenerDoesNotAddResourceRoot() throws Exception {
        deploymentName = "deploymentWithoutSpringMvcDispatcherOrListener" + (springDependenciesInDeployment() ? "SpringIncluded" : "SpringInModule");
        DeploymentDescription deploymentDescription = new DeploymentDescription(deploymentName, createDeploymentWithoutSpringMvcDispatcherOrListener(deploymentName));
        deploymentDescription.shouldBeManaged(false);

        deploymentScenario.addDeployment(deploymentDescription);
        deployer.deploy(deploymentDescription.getName());

        assertResponse(deploymentName);
        assertTrue(springClassesAreAvailableToDeployment(deploymentName));
        assertFalse(resteasySpringClassesAreAvailableToDeployment(deploymentName));
    }



    private Archive<?> createDeploymentWithoutSpringMvcDispatcherOrListener(String name) {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, name + ".war")
                .addClass(TestResource.class)
                .addClass(TestApplication.class)
                .addAsWebInfResource("web-no-mvc-no-listener.xml", "web.xml")
                .addAsWebInfResource("applicationContext.xml");
        addSpringLibraries(archive);
        archive.as(ZipExporter.class).exportTo(new File("target", name + ".war"), true);
        return archive;
    }

    private boolean resteasySpringClassesAreAvailableToDeployment(String deploymentName) throws IOException, HttpException {

        return isClassAvailableToDeployment(deploymentName, SpringContextLoaderListener.class);
    }

    private boolean isClassAvailableToDeployment(String deploymentName, Class<?> clazz) throws IOException,
            HttpException {
        String className = clazz.getName();
        String CONTEXT_URL = "http://localhost:8080/" + deploymentName;
        HttpMethod httpMethod = new GetMethod(CONTEXT_URL + "/" + TestResource.LOAD_CLASS_PATH + "?" + CLASSNAME_PARAM + "=" + className);
        try {
            return (SC_OK == client.executeMethod(httpMethod))
                    && (className.equals(httpMethod.getResponseBodyAsString()));
        } finally {
            httpMethod.releaseConnection();
        }
    }

    private boolean springClassesAreAvailableToDeployment(String deploymentName) throws IOException, HttpException {
        return isClassAvailableToDeployment(deploymentName, ApplicationContext.class);
    }

    private void assertResponse(String deploymentName) throws IOException, HttpException {

        String CONTEXT_URL = "http://localhost:8080/" + deploymentName;

        HttpMethod httpMethod = new GetMethod(CONTEXT_URL + "/" + TEST_PATH);

        assertEquals(SC_OK, client.executeMethod(httpMethod));
        assertEquals(TEST_RESPONSE, httpMethod.getResponseBodyAsString());
        httpMethod.releaseConnection();
    }

    private WebArchive createDeploymentWithSpringMvcDispatcher(String name) {

        WebArchive archive = ShrinkWrap.create(WebArchive.class, name + ".war")
                .addClass(TestResource.class)
                .addAsWebInfResource("mvc-dispatcher-servlet/web.xml")
                .addAsWebInfResource("mvc-dispatcher-servlet/mvc-dispatcher-servlet.xml")
                .addAsWebInfResource("mvc-dispatcher-servlet/applicationContext.xml");

        addSpringLibraries(archive);
        archive.as(ZipExporter.class).exportTo(new File("target", name + ".war"), true);
        return archive;
    }

    private Archive<?> createDeploymentWithSpringContextLoaderListener(String name) {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, name + ".war")
                .addClass(TestResource.class)
                .addAsWebInfResource("web.xml")
                .addAsWebInfResource("applicationContext.xml");
        addSpringLibraries(archive);
        archive.as(ZipExporter.class).exportTo(new File("target", name + ".war"), true);
        return archive;
    }

    private void addSpringLibraries(WebArchive archive) {
        if (springDependenciesInDeployment()) {
            archive.addAsLibraries(resolveSpringDependencies());
        } else {
            // you need to use the 'meta-inf' attribute to import the contents of meta-inf so spring can find the correct namespace handlers
            if (isDefinedSystemProperty("use-jboss-deployment-structure"))
                archive.addAsManifestResource("jboss-deployment-structure.xml");
            else
                archive.addAsManifestResource(new StringAsset("Dependencies: org.springframework.spring meta-inf\n"), "MANIFEST.MF");
        }
    }

    private File[] resolveSpringDependencies() {
        List<File> runtimeDependencies = new ArrayList<File>();
        runtimeDependencies.addAll(Arrays.asList(Maven.resolver().resolve("org.springframework:spring-core:" + springVersion).withTransitivity().asFile()));
        runtimeDependencies.addAll(Arrays.asList(Maven.resolver().resolve("org.springframework:spring-web:" + springVersion).withTransitivity().asFile()));
        runtimeDependencies.addAll(Arrays.asList(Maven.resolver().resolve("org.springframework:spring-webmvc:" + springVersion).withTransitivity().asFile()));
        File[] dependencies = runtimeDependencies.toArray(new File []{});
        return dependencies;
    }

    private boolean springDependenciesInDeployment() {
        return ! isDefinedSystemProperty("spring-in-module");
    }



    private String getSpringVersion() {
        return readSystemProperty("version.org.springframework", "3.2.5.RELEASE");
    }

    private String readSystemProperty(String name, String defaultValue) {
        String value = System.getProperty(name);
        return (value == null) ? defaultValue : value;
    }

    private boolean isDefinedSystemProperty(String name) {
        String value = System.getProperty(name);
        return (value != null);
    }

}
