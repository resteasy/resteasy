package org.jboss.resteasy.spi.metadata;

/**
 * SPI which allows implementations to modify the resource metadata discovered by RESTEasy.
 *
 * @author Christian Kaltepoth
 */
public interface ResourceClassProcessor
{

  /**
   * Allows the implementation of this method to modify the resource metadata represented by
   * the supplied {@link ResourceClass} instance. Implementation will typically create
   * wrappers which modify only certain aspects of the metadata.
   *
   * @param clazz The original metadata
   * @return the (potentially modified) metadata (never null)
   */
  ResourceClass process(ResourceClass clazz);

}