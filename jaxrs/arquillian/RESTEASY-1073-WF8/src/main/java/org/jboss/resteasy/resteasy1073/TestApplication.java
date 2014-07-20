package org.jboss.resteasy.resteasy1073;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class TestApplication extends Application
{
   @Override
   public Set<Class<?>> getClasses() {
      HashSet<Class<?>> set = new HashSet<Class<?>>();
      set.add(TestResource.class);
      return set;
   }
}

