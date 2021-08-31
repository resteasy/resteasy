package org.jboss.resteasy.plugins.providers.jaxb.fastinfoset;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlSeeAlsoProvider;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Consumes({"application/fastinfoset", "application/*+fastinfoset"})
@Produces({"application/fastinfoset", "application/*+fastinfoset"})
public class FastinfoSetXmlSeeAlsoProvider extends JAXBXmlSeeAlsoProvider
{
   @Override
   protected boolean needsSecurity()
   {
      return false;
   }
}
