package org.jboss.resteasy.plugins.providers.jaxb.fastinfoset;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.jaxb.CollectionProvider;

import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.Logger.Level;

/**
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
@Provider
@Consumes("application/*+fastinfoset")
@Produces("application/*+fastinfoset")
public class FastInfosetCollectionProvider extends CollectionProvider {

  @Override
  @LogMessage(level = Level.DEBUG)
  @Message(value = "Provider : org.jboss.resteasy.plugins.providers.jaxb.fastinfoset.FastInfosetCollectionProvider , method call : needsSecurity .")
  protected boolean needsSecurity() {
    return false;
  }
}
