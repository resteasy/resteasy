package org.jboss.resteasy.plugins.providers.atom;

import org.jboss.resteasy.plugins.providers.resteasy_atom.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import java.net.URI;

/**
 * If invoked within the context of a JAX-RS call, it will automatically build a
 * URI based the base URI of the JAX-RS application.  Same URI as UriInfo.getBaseUri().
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "link")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class BaseLink extends Link
{
   public BaseLink()
   {
   }

   public BaseLink(String rel, String relativeLink)
   {
      UriInfo uriInfo = ResteasyProviderFactory.getContextData(UriInfo.class);
      if (uriInfo == null)
         throw new IllegalStateException(Messages.MESSAGES.consructorMustBeCalled());
      URI uri = uriInfo.getBaseUriBuilder().path(relativeLink).build();
      setHref(uri);
      setRel(rel);
   }

   public BaseLink(String rel, String relativeLink, MediaType mediaType)
   {
      this(rel, relativeLink);
      this.setType(mediaType);
   }

   public BaseLink(String rel, String relativeLink, String mediaType)
   {
      this(rel, relativeLink);
      this.setType(MediaType.valueOf(mediaType));
   }
}
