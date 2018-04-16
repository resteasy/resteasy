package org.jboss.resteasy.spi.metadata;

import java.util.Set;

import javax.ws.rs.core.MediaType;

/**
 * @author Christian Kaltepoth
 */
public interface ResourceMethod extends ResourceLocator
{
  Set<String> getHttpMethods();

  MediaType[] getProduces();

  MediaType[] getConsumes();

  boolean isAsynchronous();

  void markAsynchronous();
}
