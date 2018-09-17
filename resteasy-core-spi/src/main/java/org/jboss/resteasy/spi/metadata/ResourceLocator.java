package org.jboss.resteasy.spi.metadata;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author Christian Kaltepoth
 */
public interface ResourceLocator
{
  ResourceClass getResourceClass();

  Class<?> getReturnType();

  Type getGenericReturnType();

  Method getMethod();

  Method getAnnotatedMethod();

  MethodParameter[] getParams();

  String getFullpath();

  String getPath();

}
