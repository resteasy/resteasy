package org.jboss.resteasy.plugins.providers.jaxb.fastinfoset;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.jaxb.MapProvider;

/**
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
@Provider
@Consumes({"application/fastinfoset", "application/*+fastinfoset"})
@Produces({"application/fastinfoset", "application/*+fastinfoset"})
public class FastInfosetMapProvider extends MapProvider {

   @Override
   protected boolean needsSecurity() {
      return false;
   }
}
