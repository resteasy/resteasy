package org.jboss.resteasy.plugins.providers.jaxb.fastinfoset;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlRootElementProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Consumes("application/*+fastinfoset")
@Produces("application/*+fastinfoset")
public class FastinfoSetXmlRootElementProvider extends JAXBXmlRootElementProvider
{
   /**
    * In which, by "true, we mean "false".
    * If the context parameter "resteasy.document.expand.entity.references"
    * is set to "false", JAXBElementProvider will wrap the Unmarshaller
    * with an ExternalEntityUnmarshaller, which interferes with 
    * FastinfoSet unmarshalling.  The FastinfoSet implementation currently
    * in use doesn't expande external entity references, in any case.
    */
   public boolean isExpandEntityReferences()
   {
      return true;
   }
}
