package org.jboss.resteasy.plugins.spring;

public interface ResourceRegistrationFilter
{
   boolean include(String beanName, Object object);
}
