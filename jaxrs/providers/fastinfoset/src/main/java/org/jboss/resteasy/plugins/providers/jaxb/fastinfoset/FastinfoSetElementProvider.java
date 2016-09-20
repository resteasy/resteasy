package org.jboss.resteasy.plugins.providers.jaxb.fastinfoset;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBElementProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.Logger.Level;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Consumes("application/*+fastinfoset")
@Produces("application/*+fastinfoset")
public class FastinfoSetElementProvider extends JAXBElementProvider
{
   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider : org.jboss.resteasy.plugins.providers.jaxb.fastinfoset.FastinfoSetElementProvider , method call : needsSecurity .")
   protected boolean needsSecurity()
   {
      return false;
   }
}
