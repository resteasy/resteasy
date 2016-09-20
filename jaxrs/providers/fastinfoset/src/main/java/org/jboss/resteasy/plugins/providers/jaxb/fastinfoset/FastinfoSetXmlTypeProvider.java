package org.jboss.resteasy.plugins.providers.jaxb.fastinfoset;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlTypeProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */

import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.Logger.Level;

@Provider
@Consumes("application/*+fastinfoset")
@Produces("application/*+fastinfoset")
public class FastinfoSetXmlTypeProvider extends JAXBXmlTypeProvider
{
   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Provider : org.jboss.resteasy.plugins.providers.jaxb.fastinfoset.FastinfoSetXmlTypeProvider , method call : needsSecurity .")
   protected boolean needsSecurity()
   {
      return false;
   }
}
