package org.jboss.resteasy.plugins.server.servlet;

public interface ResourceRegistrationFilter
{
   boolean include(String beanName, Object object);
}
