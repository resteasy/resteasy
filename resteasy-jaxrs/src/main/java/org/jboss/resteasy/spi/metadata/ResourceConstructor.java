package org.jboss.resteasy.spi.metadata;

import java.lang.reflect.Constructor;

/**
 * @author Christian Kaltepoth
 */
public interface ResourceConstructor
{
  ResourceClass getResourceClass();

  Constructor getConstructor();

  ConstructorParameter[] getParams();
}
