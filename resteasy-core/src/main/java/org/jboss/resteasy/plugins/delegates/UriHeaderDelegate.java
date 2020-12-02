package org.jboss.resteasy.plugins.delegates;

import jakarta.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriHeaderDelegate implements RuntimeDelegate.HeaderDelegate<URI>
{
   public static final UriHeaderDelegate INSTANCE = new UriHeaderDelegate();

   public URI fromString(String value) throws IllegalArgumentException
   {
      if (value == null) throw new IllegalArgumentException(Messages.MESSAGES.uriValueNull());
      return URI.create(value);
   }

   public String toString(URI value)
   {
      if (value == null) throw new IllegalArgumentException(Messages.MESSAGES.paramNull());
      URI uri = (URI) value;
      return uri.toASCIIString();
   }
}
