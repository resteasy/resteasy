package org.jboss.resteasy.plugins.delegates;

import org.jboss.resteasy.spi.LinkHeader;

import javax.ws.rs.core.Link;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.HashMap;
import java.util.Map;

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
      link.setRelationship(value.getRel());
      link.setTitle(value.getTitle());
      link.setType(value.getType());
      HashMap<String, String> copy = new HashMap<String, String>();
      copy.putAll(value.getParams());
      copy.remove(Link.REL);
      copy.remove(Link.TITLE);
      copy.remove(Link.TYPE);
      for (Map.Entry<String, String> entry : copy.entrySet())
      {
         link.getExtensions().add(entry.getKey(), entry.getValue());
      }
      header.addLink(link);
      return header.toString();
   }
}
