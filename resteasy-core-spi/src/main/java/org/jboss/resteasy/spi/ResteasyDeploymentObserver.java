package org.jboss.resteasy.spi;

/**
 * Observer interface for {@link ResteasyDeployment} life cycle.
 */
public interface ResteasyDeploymentObserver
{
   void start(ResteasyDeployment deployment);

   void stop(ResteasyDeployment deployment);
}
