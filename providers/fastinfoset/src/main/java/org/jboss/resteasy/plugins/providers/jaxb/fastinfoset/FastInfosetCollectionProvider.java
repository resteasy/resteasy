package org.jboss.resteasy.plugins.providers.jaxb.fastinfoset;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.jaxb.CollectionProvider;

/**
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
@Provider
@Consumes({"application/fastinfoset", "application/*+fastinfoset"})
@Produces({"application/fastinfoset", "application/*+fastinfoset"})
public class FastInfosetCollectionProvider extends CollectionProvider {

  @Override
  protected boolean needsSecurity() {
    return false;
  }
}
