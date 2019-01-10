package org.jboss.resteasy.test.resource;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import org.jboss.resteasy.test.resource.resource.SimpleClassParamConverterProvider;
import org.jboss.resteasy.test.resource.resource.SimpleClassParamConverterResource;
import org.jboss.resteasy.test.resource.resource.SimpleHeaderDelegateAsProviderHeader;
import org.jboss.resteasy.test.resource.resource.SimpleHeaderDelegateAsProviderResource;
import org.jboss.resteasy.test.resource.resource.SimpleHeaderDelegateAsProviderHeaderDelegate;
import org.jboss.resteasy.test.resource.resource.SimpleFromValueProvider;
import org.jboss.resteasy.test.resource.resource.SimpleFromValueResource;
import org.jboss.resteasy.test.resource.resource.SimpleFromStringProvider;
import org.jboss.resteasy.test.resource.resource.SimpleFromStringResource;
import org.jboss.resteasy.test.resource.resource.SimpleValueOfProvider;
import org.jboss.resteasy.test.resource.resource.SimpleValueOfResource;


/**
 * This unit test checks changes required by RESTEASY-2062.  During application
 * deployment when injection of parameter values are declared with @DefaultValue
 * the value must be injected at deployment time.  Errors encountered at this
 * time must be reported.
 *
 * These tests check that an appropriate exception is thrown for an injected parameter.
 * Testing is performed here because such exceptions can not be captured by arquillian
 * at deployment time.
 */
public class ParamInjectionExceptionTest {
    private ResteasyDeploymentImpl deploymentImpl;

    @Before
    public void before() {
        deploymentImpl = new ResteasyDeploymentImpl();
    }

    @Test
    public void paramConverterTest() throws Exception
    {
        ArrayList<Class> providers = new ArrayList<Class>();
        providers.add(SimpleClassParamConverterProvider.class);

        ArrayList<Class> resources = new ArrayList<Class>();
        resources.add(SimpleClassParamConverterResource.class);

        deploymentImpl.setActualProviderClasses(providers);
        deploymentImpl.setActualResourceClasses(resources);

        try {
            deploymentImpl.start();
        } catch (Exception e) {
            return;
        }
        Assert.fail("Test should throw exception but did not");
    }

    @Test
    public void headerDelegateTest() throws Exception {
        ArrayList<Class> providers = new ArrayList<Class>();
        providers.add(SimpleHeaderDelegateAsProviderHeader.class);
        providers.add(SimpleHeaderDelegateAsProviderHeaderDelegate.class);

        ArrayList<Class> resources = new ArrayList<Class>();
        resources.add(SimpleHeaderDelegateAsProviderResource.class);

        deploymentImpl.setActualProviderClasses(providers);
        deploymentImpl.setActualResourceClasses(resources);

        try {
            deploymentImpl.start();
        } catch (Exception e) {
            return;
        }
        Assert.fail("Test should throw exception but did not");
    }

    @Test
    public void fromValueTest() throws Exception {
        ArrayList<Class> providers = new ArrayList<Class>();
        providers.add(SimpleFromValueProvider.class);

        ArrayList<Class> resources = new ArrayList<Class>();
        resources.add(SimpleFromValueResource.class);

        deploymentImpl.setActualProviderClasses(providers);
        deploymentImpl.setActualResourceClasses(resources);

        try {
            deploymentImpl.start();
        } catch (Exception e) {
            return;
        }
        Assert.fail("Test should throw exception but did not");
    }

    @Test
    public void fromStringTest() throws Exception {
        ArrayList<Class> providers = new ArrayList<Class>();
        providers.add(SimpleFromStringProvider.class);

        ArrayList<Class> resources = new ArrayList<Class>();
        resources.add(SimpleFromStringResource.class);

        deploymentImpl.setActualProviderClasses(providers);
        deploymentImpl.setActualResourceClasses(resources);

        try {
            deploymentImpl.start();
        } catch (Exception e) {
            return;
        }
        Assert.fail("Test should throw exception but did not");
    }

    @Test
    public void valueOfTest() throws Exception {
        ArrayList<Class> providers = new ArrayList<Class>();
        providers.add(SimpleValueOfProvider.class);

        ArrayList<Class> resources = new ArrayList<Class>();
        resources.add(SimpleValueOfResource.class);

        deploymentImpl.setActualProviderClasses(providers);
        deploymentImpl.setActualResourceClasses(resources);

        try {
            deploymentImpl.start();
        } catch (Exception e) {
            return;
        }
        Assert.fail("Test should throw exception but did not");
    }
}
