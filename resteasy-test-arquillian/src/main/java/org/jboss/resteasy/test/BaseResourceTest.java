package org.jboss.resteasy.test;

import static org.jboss.resteasy.arquillian.extension.DeploymentScenarioUtils.*;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.arquillian.container.spi.client.deployment.DeploymentDescription;
import org.jboss.arquillian.container.spi.client.deployment.DeploymentScenario;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public abstract class BaseResourceTest
{

  private static final Logger logger = Logger.getLogger(BaseResourceTest.class.getName());

  public final static String DEPLOYMENT = "app";

  @ArquillianResource
  protected Deployer deployer;

  @ArquillianResource
  private DeploymentScenario deploymentScenario;

  protected WebArchive war;
  protected boolean deployed;
  protected boolean manualStart;
  protected Map<String,String> initParams = new Hashtable<String,String>();
  protected Map<String,String> contextParams = new Hashtable<String,String>();

//  protected static ResteasyDeployment deployment;
//  protected static Dispatcher dispatcher;

  public void createDeploymentIfNotCreated() {
      if (war == null)
          war = createDeployment();
  }

  public WebArchive createDeployment() {
      WebArchive war = ShrinkWrap.create(WebArchive.class,  DEPLOYMENT + ".war");
      war.addClass(TestApplication.class);
      return war;
    }

  @Before
  public void before () throws Exception {
    info("before");
    createDeploymentIfNotCreated();
    if (!deployed && !manualStart)
      startContainer();
  }

  protected TestApplication createTestApplication() {
    return new TestApplication();
}

@After
  public void after() throws Exception
  {
    stopContainer();
  }

  public void addPerRequestResource(Class<?> ... resources)
  {
    createDeploymentIfNotCreated();
    Class<?> resourceClass = resources[0];
    info("added request resource " + resourceClass.getName());
    TestApplication.classes.add(resourceClass);
    war.addClasses(resources);
  }

  protected void addPackageInfo(final Class<?> clazz) {
      war.addPackages(false, new org.jboss.shrinkwrap.api.Filter<org.jboss.shrinkwrap.api.ArchivePath>()
              {
                 @Override
                 public boolean include(ArchivePath path)
                 {
                     return path.get().endsWith("package-info.class");
                 }
              }, clazz.getPackage());

  }


  protected void addWebResource(File file) {
    createDeploymentIfNotCreated();
    assertTrue("Resource not found " + file.getAbsolutePath(), file.exists());
    info("added web resource " + file.getAbsolutePath());
    war.addAsWebResource(file);
  }

  @Provider
  @ApplicationPath("/")
  public static class TestApplication extends Application
  {

     public final static Set<Class<?>> classes = new HashSet<Class<?>>();

     @Override
     public Set<Class<?>> getClasses()
     {
        return classes;
     }
  }


  public String readString(InputStream in) throws IOException
  {
     char[] buffer = new char[1024];
     StringBuilder builder = new StringBuilder();
     BufferedReader reader = new BufferedReader(new InputStreamReader(in));
     int wasRead = 0;
     do
     {
        wasRead = reader.read(buffer, 0, 1024);
        if (wasRead > 0)
        {
           builder.append(buffer, 0, wasRead);
        }
     }
     while (wasRead > -1);

     return builder.toString();
  }

  public void registerProvider(Class<?> clazz) {
    createDeploymentIfNotCreated();
    info("register provider " + clazz.getName());
    war.addClass(clazz);
    TestApplication.classes.add(clazz);
  }

  public void addExceptionMapper(Class<? extends ExceptionMapper<?>> clazz) {
    createDeploymentIfNotCreated();
    war.addClass(clazz);
    TestApplication.classes.add(clazz);
  }

  protected void createContainer(Map<String,String> initParams, Map<String, String> contextParams) throws Exception {
    info("create container");
    this.initParams = initParams;
    this.contextParams = contextParams;
  }

  protected void startContainer() throws Exception {
    info("start container - deploy " + DEPLOYMENT);
    createDeploymentIfNotCreated();
    if (contextParams != null && contextParams.size() > 0 && !war.contains("WEB-INF/web.xml")) {
      StringBuilder webXml = new StringBuilder();
      webXml.append("<web-app version=\"3.0\" xmlns=\"http://java.sun.com/xml/ns/javaee\" \n");
      webXml.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n");
      webXml.append( " xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd\"> \n");
      for (Map.Entry<String, String> entry : contextParams.entrySet()) {
        String paramName = entry.getKey();
        String paramValue = entry.getValue();
        info("Context param " + paramName + " value " + paramValue);
        webXml.append("<context-param>\n");
        webXml.append("<param-name>" + paramName + "</param-name>\n");
        webXml.append("<param-value>" + paramValue + "</param-value>\n");
        webXml.append("</context-param>\n");
      }
      webXml.append("</web-app>\n");
      Asset resource = new StringAsset(webXml.toString());
      war.addAsWebInfResource(resource, "web.xml");
    }
    if (System.getProperty("STORE_WAR") != null) {
        war.as(ZipExporter.class).exportTo(new File("target", war.getName()), true);
    }
    DeploymentDescription deploymentDescription = new DeploymentDescription(DEPLOYMENT, war);
    deploymentDescription.shouldBeManaged(false);
    deploymentScenario.addDeployment(deploymentDescription);
    deployer.deploy(DEPLOYMENT);
    deployed = true;
  }

  protected void stopContainer() throws Exception {
    info("stop container - undeploy " + DEPLOYMENT);
    if (deployed) {
      deployer.undeploy(DEPLOYMENT);
      removeDeploymentFromDeploymentScenario(deploymentScenario, DEPLOYMENT);
    }
    deployed = false;
    war = null;
    TestApplication.classes.clear();
  }

  protected void info (String message) {
    logger.info(message);
    System.err.println("BaseResourceTest - " + message);
  }

  public void addLibraryWithTransitiveDependencies(String gav) {
	   war.addAsLibraries(resolveProviderDependencies(gav));

  }
  private File[] resolveProviderDependencies(String gav) {
      List<File> runtimeDependencies = new ArrayList<File>();
      runtimeDependencies.addAll(Arrays.asList(Maven.resolver().resolve(gav).withTransitivity().asFile()));
      return runtimeDependencies.toArray(new File []{});
  }

}