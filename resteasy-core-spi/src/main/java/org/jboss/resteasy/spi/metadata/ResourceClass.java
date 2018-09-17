package org.jboss.resteasy.spi.metadata;

/**
 * @author Christian Kaltepoth
 */
public interface ResourceClass
{
  String getPath();

  Class<?> getClazz();

  ResourceConstructor getConstructor();

  FieldParameter[] getFields();

  SetterParameter[] getSetters();

  ResourceMethod[] getResourceMethods();

  ResourceLocator[] getResourceLocators();
}
