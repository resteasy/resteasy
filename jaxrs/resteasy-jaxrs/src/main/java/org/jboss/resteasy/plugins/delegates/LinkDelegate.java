package org.jboss.resteasy.plugins.delegates;

import org.jboss.resteasy.spi.LinkHeader;

import javax.ws.rs.core.Link;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LinkDelegate implements RuntimeDelegate.HeaderDelegate<Link>
{
   @Override
   public Link fromString(String value) throws IllegalArgumentException
   {
      LinkHeader header = new LinkHeaderDelegate().fromString(value);
      org.jboss.resteasy.spi.Link resteasyLink = header.getLinks().get(0);
      return resteasyLink.toJaxrsLink();
   }

   @Override
   public String toString(Link value) throws IllegalArgumentException
   {
      LinkHeader header = new LinkHeader();

      org.jboss.resteasy.spi.Link link = new org.jboss.resteasy.spi.Link();
      link.setHref(value.getUri().toString());
      link.getExtensions().putAll(value.getParams());
      header.addLink(link);
      return header.toString();
   }
}
