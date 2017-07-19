package org.jboss.resteasy.plugins.providers.jaxb.json;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlRootElementProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces({"application/json", "application/*+json"})
@Consumes({"application/json", "application/*+json"})
public class JettisonXmlRootElementProvider extends JAXBXmlRootElementProvider
{
   @Override
   protected boolean needsSecurity()
   {
      return false;
   }
}
