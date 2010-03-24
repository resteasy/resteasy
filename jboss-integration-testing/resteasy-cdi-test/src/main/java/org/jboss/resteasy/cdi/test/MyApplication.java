package org.jboss.resteasy.cdi.test;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.jboss.resteasy.cdi.test.alternative.MockResource;
import org.jboss.resteasy.cdi.test.alternative.ProductionResource;
import org.jboss.resteasy.cdi.test.basic.ApplicationScopedTestResource;
import org.jboss.resteasy.cdi.test.basic.TestProvider;
import org.jboss.resteasy.cdi.test.basic.TestResource;
import org.jboss.resteasy.cdi.test.ejb.StatefulSessionBeanResource1;
import org.jboss.resteasy.cdi.test.ejb.StatefulSessionBeanResource2;
import org.jboss.resteasy.cdi.test.ejb.StatelessSessionBeanResource;

public class MyApplication extends Application
{
   @Override
   public Set<Class<?>> getClasses()
   {
      Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(TestProvider.class);
      classes.add(TestResource.class);
      classes.add(StatefulSessionBeanResource1.class);
      classes.add(StatefulSessionBeanResource2.class);
      classes.add(StatelessSessionBeanResource.class);
      classes.add(ProductionResource.class);
      classes.add(MockResource.class);
      classes.add(org.jboss.resteasy.cdi.test.interceptor.TestResource.class);
      classes.add(ApplicationScopedTestResource.class);
      return classes;
   }
}
