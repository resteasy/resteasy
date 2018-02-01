package org.jboss.resteasy.specimpl;


import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LinkBuilderImpl implements Link.Builder
{
   /**
    * A map for all the link parameters such as "rel", "type", etc. 
    */
   protected final Map<String, String> map = new HashMap<String, String>();
   protected UriBuilder uriBuilder;
   protected URI baseUri;

   @Override
   public Link.Builder link(Link link)
   {
      uriBuilder = UriBuilder.fromUri(link.getUri());
      this.map.clear();
      this.map.putAll(link.getParams());
      return this;
   }

   @Override
   public Link.Builder link(String link)
   {
      Link l = LinkImpl.valueOf(link);
      return link(l);
   }

   @Override
   public Link.Builder uriBuilder(UriBuilder uriBuilder)
   {
      this.uriBuilder = uriBuilder.clone();
      return this;
   }

   @Override
   public Link.Builder uri(URI uri) {
      if (uri == null) throw new IllegalArgumentException(Messages.MESSAGES.uriParamNull());
      uriBuilder = UriBuilder.fromUri(uri);
      return this;
   }

   @Override
   public Link.Builder uri(String uri) throws IllegalArgumentException {
      if (uri == null) throw new IllegalArgumentException(Messages.MESSAGES.uriParamNull());
      uriBuilder = UriBuilder.fromUri(uri);
      return this;
   }

   @Override
   public Link.Builder rel(String rel) {
      if (rel == null) throw new IllegalArgumentException(Messages.MESSAGES.relParamNull());
      final String rels = this.map.get(Link.REL);
      param(Link.REL, rels == null ? rel : rels + " " + rel);
      return this;
   }

   @Override
   public Link.Builder title(String title) {
      if (title == null) throw new IllegalArgumentException(Messages.MESSAGES.titleParamNull());
      param(Link.TITLE, title);
      return this;

   }

   @Override
   public Link.Builder type(String type) {
      if (type == null) throw new IllegalArgumentException(Messages.MESSAGES.typeParamNull());
      param(Link.TYPE, type);
      return this;
   }

   @Override
   public Link.Builder param(String name, String value) throws IllegalArgumentException {
      if (name == null) throw new IllegalArgumentException(Messages.MESSAGES.nameParamWasNull());
      if (value == null) throw new IllegalArgumentException(Messages.MESSAGES.valueParamWasNull());
      this.map.put(name, value);
      return this;
   }

   @Override
   public Link build(Object... values) throws UriBuilderException
   {
      if (values == null) throw new IllegalArgumentException(Messages.MESSAGES.valuesParamWasNull());
      URI built = null;
      if (uriBuilder == null)
      {
        built = baseUri;
      }
      else
      {
        built = this.uriBuilder.build(values);
      }
      if (!built.isAbsolute() && baseUri != null)
      {
        built = baseUri.resolve(built);
      }
      return new LinkImpl(built, this.map);
   }

   @Override
   public Link buildRelativized(URI uri, Object... values)
   {
      if (uri == null) throw new IllegalArgumentException(Messages.MESSAGES.uriParamNull());
      if (values == null) throw new IllegalArgumentException(Messages.MESSAGES.valuesParamWasNull());
      URI built = uriBuilder.build(values);
      URI with = built;
      if (baseUri != null) with = baseUri.resolve(built);
      return new LinkImpl(uri.relativize(with), this.map);
   }

   @Override
   public Link.Builder baseUri(URI uri)
   {
      this.baseUri = uri;
      return this;
   }

   @Override
   public Link.Builder baseUri(String uri)
   {
      this.baseUri = URI.create(uri);
      return this;
   }
}