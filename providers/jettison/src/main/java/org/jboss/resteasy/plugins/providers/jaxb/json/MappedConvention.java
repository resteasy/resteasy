package org.jboss.resteasy.plugins.providers.jaxb.json;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;

/**
 * attributeAsElements doesn't work as isElement receives nullvalues for p and ns.
 * QName prefix and namespace values are never null.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MappedConvention extends MappedNamespaceConvention
{
   public MappedConvention()
   {
   }

   public MappedConvention(Configuration configuration)
   {
      super(configuration);
   }

   @Override
   public boolean isElement(String p, String ns, String local)
   {
      if (p == null) p = "";
      if (ns == null) ns = "";
      return super.isElement(p, ns, local);
   }
}
