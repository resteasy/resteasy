package org.jboss.resteasy.arquillian.extension;

import java.lang.annotation.Annotation;

import org.jboss.arquillian.container.spi.client.deployment.DeploymentScenario;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class DeploymentScenarioProvider implements ResourceProvider
{
    @Inject
    private Instance<DeploymentScenario> deploymentScenario;

    @Override
    public Object lookup(ArquillianResource resource, Annotation... qualifiers)
    {
       return deploymentScenario.get();
    }

    @Override
    public boolean canProvide(Class<?> type)
    {
       return DeploymentScenario.class.isAssignableFrom(type);
    }

 }